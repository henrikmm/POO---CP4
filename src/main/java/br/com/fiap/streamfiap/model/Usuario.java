package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.ClassificacaoIndicativaException;
import br.com.fiap.streamfiap.exception.ConteudoIndisponivelException;
import br.com.fiap.streamfiap.exception.CreditosInsuficientesException;
import br.com.fiap.streamfiap.exception.DadosInvalidosException;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private int idade;
    private double creditos;

    public Usuario() {
    }

    public Usuario(String nome, int idade, double creditos) {
        setNome(nome);
        setIdade(idade);
        setCreditos(creditos);
    }

    public boolean temCreditosSuficientes(double preco) {
        return this.creditos >= preco;
    }

    public void debitarCreditos(double valor) {
        if (valor < 0) {
            throw new DadosInvalidosException("O valor do débito não pode ser negativo");
        }
        if (!temCreditosSuficientes(valor)) {
            throw new CreditosInsuficientesException("Créditos insuficientes para realizar o débito");
        }
        this.creditos = this.creditos - valor;
    }

    public Usuario alugar(Conteudo c) throws ClassificacaoIndicativaException {
        if (!c.isDisponivel()) {
            throw new ConteudoIndisponivelException("Conteúdo indisponível para aluguel: " + c.getTitulo());
        }

        if (this.idade < c.getClassificacaoEtaria()) {
            throw new ClassificacaoIndicativaException("Usuário de " + this.idade
                    + " anos não pode assistir a " + c.getTitulo()
                    + " (classificação " + c.getClassificacaoEtaria() + " anos)");
        }

        double p = c.calcularPrecoAluguel();

        if (!temCreditosSuficientes(p)) {
            throw new CreditosInsuficientesException("Créditos insuficientes para alugar " + c.getTitulo());
        }

        debitarCreditos(p);
        c.setDisponivel(false);

        System.out.println("==================================================");
        System.out.println("RECIBO STREAMFIAP");
        System.out.println("Usuario: " + this.nome);
        System.out.println("Conteudo: " + c.getTitulo());
        System.out.println("Valor pago: R$ " + p);
        System.out.println("Creditos restantes: R$ " + this.creditos);
        System.out.println("Obrigado por usar o StreamFIAP!");
        System.out.println("==================================================");

        return this;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new DadosInvalidosException("O nome do usuário é obrigatório");
        }
        this.nome = nome.trim();
    }

    public int getIdade() { return idade; }
    public void setIdade(int idade) {
        if (idade < 0) {
            throw new DadosInvalidosException("A idade não pode ser negativa");
        }
        this.idade = idade;
    }

    public double getCreditos() { return creditos; }
    public void setCreditos(double creditos) {
        if (creditos < 0) {
            throw new DadosInvalidosException("Os créditos não podem ser negativos");
        }
        this.creditos = creditos;
    }
}
