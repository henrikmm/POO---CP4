# Checkpoint 4 - Bug Hunt StreamFIAP

## Identificação

**Grupo:** preencher com o nome do grupo

| Integrante | RM | Turma |
|---|---|---|
| Henrique Mandrick | 562715 | 2CCPW|
| Ryan Amorim de Castro Santana | 564393| 2CCPW|


| Campo | Resultado |
|---|---|
| **Total de bugs corrigidos** | **12 / 12** |
| **Total de ajustes de Clean Code** | **6 / 6** |

> Os campos de grupo, RM e turma precisam ser preenchidos pelo grupo antes do push
> final, pois essas informações não vieram no material recebido.

## Parte 1 - Bugs encontrados

| # | Sintoma observado | Causa raiz (arquivo e linha aproximada) | Correção aplicada | Conceito da disciplina |
|---|---|---|---|---|
| bug01 | O usuário era salvo sem nome, mesmo quando o POST enviava o campo corretamente. | `model/Usuario.java`, construtor: havia `nome = nome`, atribuição para o próprio parâmetro. | O construtor passou a usar os setters e a atribuição correta `this.nome`. | Encapsulamento, estado do objeto e construtores. |
| bug02 | O cadastro de usuário não garantia um `id` gerado pelo banco. | `model/Usuario.java`, campo `id`: faltava `@GeneratedValue`. | Foi adicionada a geração automática com `GenerationType.IDENTITY`; o controller continua ignorando o id enviado pelo cliente. | JPA, identidade e persistência. |
| bug03 | Um conteúdo com `duracaoMinutos` igual a zero ou negativa podia ser aceito. Créditos e campos obrigatórios também não eram protegidos. | `model/Conteudo.java` e `model/Usuario.java`: setters/construtores não validavam os dados. | Foram adicionadas validações no ponto de criação e nos setters, com `DadosInvalidosException` tratado como HTTP 400. | Validação, invariantes e exceções de domínio. |
| bug04 | Documentários eram cobrados como conteúdo padrão, em vez de serem gratuitos. | `model/Conteudo.java`: o preço padrão era `9.90` e `Documentario` não sobrescrevia o cálculo. | O cálculo virou abstrato e `Documentario.calcularPrecoAluguel()` retorna `0.0`. | Herança, polimorfismo e abstração. |
| bug05 | Séries cadastradas perdiam título, categoria, duração, classificação e disponibilidade. | `model/Serie.java`, construtor: os parâmetros recebidos não eram enviados ao construtor da superclasse. | O construtor chama `super(...)` e inicializa temporadas pelo setter; séries novas começam disponíveis. | Herança e inicialização da superclasse. |
| bug06 | O preço de uma série era calculado como o preço base de `Conteudo`, não como `R$ 4,90` por temporada. | `model/Serie.java`: `calcularPrecoAluguel(double)` sobrecarregava o método sem argumentos, em vez de sobrescrevê-lo. | O método passou a ter a mesma assinatura e recebeu `@Override`. | Sobrescrita, sobrecarga e polimorfismo. |
| bug07 | O preço promocional aumentava em 20% para filmes. | `model/Filme.java`, `aplicarPromocao`: usava fator `1.2`. | O fator passou a ser `0.80`, aplicando 20% de desconto. | Interface, contrato e regra de negócio. |
| bug08 | A busca por categoria podia retornar lista vazia mesmo com categoria igual. | `controller/ConteudoController.java`: comparação de `String` com `==`. | A consulta passou a usar `ConteudoRepository.findByCategoria`, derivada pelo Spring Data JPA. | Igualdade de objetos e consultas derivadas. |
| bug09 | `GET /api/conteudos/999` devolvia resposta vazia/nula, sem informar o erro. | `controller/ConteudoController.java`: um `catch (Exception)` engolia a exceção e retornava `null`. | O método agora propaga `ConteudoNaoEncontradoException`, que o `GlobalExceptionHandler` converte em HTTP 404 com mensagem. | Exceções, tratamento centralizado e contrato HTTP. |
| bug10 | Um conteúdo já alugado podia ser alugado novamente. | `model/Usuario.java`, método `alugar`: não havia verificação de `isDisponivel()`. | O aluguel recusa conteúdos indisponíveis com `ConteudoIndisponivelException` e só marca como indisponível após o débito válido. | Regras de negócio e consistência de estado. |
| bug11 | Usuários sem saldo suficiente eram aceitos e o saldo podia ficar negativo. | `model/Usuario.java`: `temCreditosSuficientes` comparava `preco >= creditos`. | A comparação foi invertida para `creditos >= preco` e o débito também possui uma proteção defensiva. | Encapsulamento de regra e invariantes financeiras. |
| bug12 | Usuário abaixo da classificação recebia erro genérico do servidor, sem a mensagem da regra. | `exception/ClassificacaoIndicativaException.java` e `GlobalExceptionHandler.java`: a exceção era checked e não tinha handler. | A exceção virou unchecked e recebeu handler próprio, retornando HTTP 403 e a mensagem em `erro`. | Exceções checked/unchecked e API REST. |

## Parte 2 - Ajustes de Clean Code

| # | Onde estava | Princípio/boa prática violado | O que foi mudado |
|---|---|---|---|
| clean01 | Os três controllers usavam injeção por atributo com `@Autowired`. | Dependências explícitas, imutabilidade e facilidade de teste. | Foi adotada injeção por construtor; os repositories agora são `final`. |
| clean02 | `Conteudo.duracaoMinutos` era um campo público. | Encapsulamento e proteção das invariantes do modelo. | O campo ficou privado, com getter e setter que validam a duração. |
| clean03 | O endpoint de busca tinha `try/catch` genérico, vazio e um retorno `null`. | Não esconder erros; cada exceção deve ter tratamento significativo. | O controller ficou direto e o tratamento foi centralizado no `GlobalExceptionHandler`. |
| clean04 | Havia método de desconto antigo e bloco de código comentado sobre cupons. | Código morto e comentários de código geram ruído e aumentam a manutenção. | Os trechos obsoletos foram removidos. |
| clean05 | Preços e fatores promocionais estavam espalhados como números mágicos. | Nomes expressivos e uma única fonte para constantes de negócio. | Filmes, séries e documentários passaram a usar constantes nomeadas. |
| clean06 | Variáveis como `c`, `p`, `novo` e `nova` escondiam a intenção. | Nomes significativos e legibilidade. | Foram usados nomes como `conteudo`, `precoAluguel`, `novoFilme`, `novaSerie` e `novoDocumentario`. |

## Parte 3 - Perguntas de reflexão

### 1. Injeção de dependência (Aula 13)

O `ConteudoController` precisa de um `ConteudoRepository` que seja criado e configurado pelo Spring.
Esse repository não é uma classe comum para ser instanciada com `new`, porque o Spring Data cria sua implementação em tempo de execução.
Ao injetar o bean pelo construtor, o Spring encontra a dependência, cria o proxy do repository e entrega essa instância ao controller.
O proxy já recebe o contexto JPA, a conexão configurada e os métodos CRUD herdados de `JpaRepository`.
Um `new ConteudoRepository()` não funcionaria porque a interface não tem implementação concreta e também não teria o ciclo de vida do container.
Além disso, a injeção por construtor deixa a dependência obrigatória e facilita testes com um repository falso ou mock.

### 2. JDBC vs Spring Data JPA (Aulas 12 e 13)

No JDBC/DAO, o desenvolvedor controla manualmente `Connection`, `PreparedStatement`, `ResultSet`, SQL, mapeamento e fechamento de recursos.
Essa abordagem dá mais controle sobre SQL específico, relatórios muito otimizados e operações que não se encaixam bem no modelo de entidades.
No projeto, o `ConteudoRepository` herda de `JpaRepository`, então o Spring Data automatiza o CRUD e o ciclo de persistência da entidade.
O método `findByCategoria` funciona por convenção: o Spring interpreta o nome, identifica a propriedade `categoria` e gera a consulta equivalente.
Assim, o repository não precisa de uma implementação manual para essa busca.
JPA reduz código repetitivo, mas ainda exige modelagem correta, atenção ao SQL gerado e cuidado com transações e desempenho.

### 3. Exceções checked vs unchecked (Aula 11)

Uma exceção checked que estende `Exception` obriga os métodos a declararem `throws` ou a tratarem o erro localmente.
No projeto original, `ClassificacaoIndicativaException` era checked, mas não havia um handler global para transformá-la em resposta da API.
Por isso, a regra chegava ao servidor como erro genérico, em vez de chegar ao cliente com a mensagem de classificação.
Ela passou a estender `RuntimeException`, que é adequada para uma violação de regra de negócio durante uma requisição REST.
Também foi criado um `@ExceptionHandler` específico, que retorna HTTP 403 e o texto em `{ "erro": "..." }`.
O cliente agora recebe a causa real sem expor stack trace ou depender de um `try/catch` em cada controller.

### 4. Sobrescrita vs sobrecarga (Aula 7)

Sobrescrita acontece quando a subclasse implementa um método da superclasse com a mesma assinatura.
Sobrecarga acontece quando o nome é igual, mas os parâmetros mudam; nesse caso, são métodos diferentes para o compilador.
Na `Serie`, o método original recebia `double desconto`, enquanto `Conteudo.calcularPrecoAluguel()` não recebia parâmetros.
Assim, uma variável tipada como `Conteudo` chamava o método da superclasse e ignorava o preço por temporada.
Ao corrigir a assinatura e adicionar `@Override`, o compilador passa a verificar se a série realmente está sobrescrevendo o contrato.
Se alguém voltar a colocar parâmetros diferentes, o código deixa de compilar em vez de esconder o erro.

### 5. Onde blindar o objeto? (Aulas 3, 4 e 13)

Regras que definem se um objeto pode existir devem ser protegidas no construtor e também nos setters usados por frameworks e desserialização.
Por isso, `Conteudo` valida título, categoria, duração e classificação tanto na criação quanto na alteração.
`Usuario` protege nome, idade e créditos, impedindo que créditos negativos entrem no modelo.
Regras que dependem do estado de outros objetos ficam em métodos de negócio, como `Usuario.alugar`, que verifica disponibilidade, idade e saldo.
Validar somente no controller não basta, porque o objeto também pode ser criado por JPA, por outro endpoint ou diretamente em um teste.
Mesmo com a validação de saldo antes do aluguel, `debitarCreditos` mantém uma proteção defensiva para garantir que a invariante financeira não seja quebrada.

### 6. Abstração e interface (Aulas 8 e 9)

`Conteudo` é uma classe abstrata porque concentra identidade, dados comuns e o contrato de cálculo do preço para todas as subclasses.
`Promocionavel` é uma interface porque representa uma capacidade opcional: filmes e séries participam da promoção, documentários não.
Se documentários passassem a ter promoção, a classe `Documentario` implementaria `Promocionavel` e forneceria `aplicarPromocao`.
O método `calcularPrecoPromocional` de `Conteudo` já reconhece a interface, então não seria necessário alterar o controller.
Os repositories e o aluguel também permaneceriam intactos, pois trabalham com o tipo abstrato `Conteudo`.
Esse desenho reduz acoplamento e permite adicionar capacidades sem espalhar condicionais por toda a aplicação.

## Parte 4 - Roteiro de verificação

O código foi revisado estaticamente e os cenários abaixo foram preparados para execução com JDK 17 ou superior e Maven.
As credenciais Oracle não fazem parte da entrega: mantenha `SEU_RM` e `SUA_SENHA` no repositório e configure os valores reais somente na máquina do grupo.

1. Inicie a aplicação pela classe `StreamFiapApplication` ou com `mvn spring-boot:run`.
2. Cadastre filme, série, documentário e usuários usando os exemplos de `TESTES_MANUAIS.md`.
3. Confirme os preços `14.90` para filme de estreia, `24.50` para cinco temporadas e `0.00` para documentário.
4. Confirme desconto de 20% para filme e série e nenhum desconto para documentário.
5. Verifique duração inválida, conteúdo inexistente, categoria, usuário menor, conteúdo indisponível, saldo insuficiente e aluguel válido.
6. Após um aluguel válido, confirme que os créditos retornados foram debitados exatamente pelo preço correto e que o conteúdo não pode ser alugado novamente.

## Parte 5 - Entrega e Git

O histórico deve seguir a regra do checkpoint: primeiro um commit com o estado original recebido e depois um commit por correção.
Como o ZIP original não contém `.git`, a pasta foi preparada para receber um `git init` e os commits abaixo:

```text
chore: estado original do projeto recebido
fix: bug01 - corrige nome do usuário
fix: bug02 - gera id do usuário
fix: bug03 - valida dados de entrada
fix: bug04 - torna documentário gratuito
fix: bug05 - inicializa dados da série
fix: bug06 - corrige sobrescrita do preço da série
fix: bug07 - corrige desconto de filme
fix: bug08 - corrige busca por categoria
fix: bug09 - informa conteúdo inexistente
fix: bug10 - recusa conteúdo indisponível
fix: bug11 - corrige validação de créditos
fix: bug12 - trata classificação indicativa
refactor: clean01 - injeta dependências por construtor
refactor: clean02 - encapsula duração do conteúdo
refactor: clean03 - remove captura de exceção vazia
refactor: clean04 - remove código morto
refactor: clean05 - nomeia constantes de negócio
refactor: clean06 - melhora nomes das variáveis
```

Antes de publicar, preencha os integrantes e confirme que o remoto aponta para o repositório público do grupo.
