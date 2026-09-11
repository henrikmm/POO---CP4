# Testes manuais da API StreamFIAP

## Preparação

Use JDK 17 ou superior. Configure as credenciais do Oracle somente na máquina local, em
`src/main/resources/application.properties`, sem substituir `SEU_RM` e `SUA_SENHA` no Git.
Inicie a aplicação na porta 8080.

Os exemplos usam `curl.exe`, que funciona no PowerShell e no Prompt de Comando.

## Cadastro de conteúdo

```bash
curl.exe -X POST http://localhost:8080/api/conteudos/filme -H "Content-Type: application/json" -d "{\"titulo\":\"Matrix\",\"categoria\":\"FICCAO\",\"duracaoMinutos\":136,\"classificacaoEtaria\":14,\"disponivel\":true,\"estreia\":true}"
```

Esperado: HTTP 201, preço de aluguel `14.90` e id gerado.

```bash
curl.exe -X POST http://localhost:8080/api/conteudos/serie -H "Content-Type: application/json" -d "{\"titulo\":\"The Bear\",\"categoria\":\"DRAMA\",\"duracaoMinutos\":35,\"classificacaoEtaria\":14,\"numeroTemporadas\":5}"
```

Esperado: HTTP 201 e preço de aluguel `24.50`.

```bash
curl.exe -X POST http://localhost:8080/api/conteudos/documentario -H "Content-Type: application/json" -d "{\"titulo\":\"Planeta Terra\",\"categoria\":\"NATUREZA\",\"duracaoMinutos\":50,\"classificacaoEtaria\":0,\"disponivel\":true,\"tema\":\"Natureza\"}"
```

Esperado: HTTP 201 e preço de aluguel `0.00`.

## Regras de consulta e promoção

```bash
curl.exe http://localhost:8080/api/conteudos
curl.exe http://localhost:8080/api/conteudos/1
curl.exe http://localhost:8080/api/conteudos/999
curl.exe http://localhost:8080/api/conteudos/categoria/FICCAO
curl.exe http://localhost:8080/api/conteudos/1/preco-promocional
```

O id `999` deve responder HTTP 404 com uma propriedade `erro`.
A consulta por categoria deve retornar somente os conteúdos da categoria informada.
Filme e série devem aplicar 20% de desconto; documentário deve continuar em `0.00`.

## Cadastro de usuário e aluguel

```bash
curl.exe -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d "{\"nome\":\"Ana\",\"idade\":18,\"creditos\":100}"
curl.exe -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d "{\"nome\":\"Bruno\",\"idade\":18,\"creditos\":0}"
curl.exe -X POST http://localhost:8080/api/usuarios -H "Content-Type: application/json" -d "{\"nome\":\"Caio\",\"idade\":12,\"creditos\":100}"
```

Cada resposta deve trazer um id diferente, nome preenchido e créditos não negativos.
Para alugar o filme de estreia pelo usuário Ana, use o id real retornado pela API:

```bash
curl.exe -X POST "http://localhost:8080/api/alugueis?usuarioId=1&conteudoId=1"
```

O aluguel válido deve debitar `14.90`, marcar o conteúdo como indisponível e retornar os créditos atualizados.
Repita a chamada: a segunda tentativa deve responder HTTP 409 com mensagem de indisponibilidade.
Use Bruno para testar créditos insuficientes e Caio para testar a classificação indicativa.
Nenhuma dessas falhas pode persistir alteração parcial ou deixar créditos negativos.

## Limpeza após os testes

Os dados são persistidos no schema Oracle do grupo. Para recomeçar do zero, remova as tabelas/linhas do schema conforme as orientações do checkpoint, sem apagar as credenciais do arquivo versionado.
