package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.DadosInvalidosException;
import jakarta.persistence.*;

@Entity
@Table(name = "conteudos")
public abstract class Conteudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String categoria;

    public int duracaoMinutos;

    private int classificacaoEtaria;
    private boolean disponivel;

    protected Conteudo() {
    }

    protected Conteudo(String titulo, String categoria, int duracaoMinutos, int classificacaoEtaria, boolean disponivel) {
        validarTexto(titulo, "título");
        validarTexto(categoria, "categoria");
        validarDuracao(duracaoMinutos);
        validarClassificacao(classificacaoEtaria);

        this.titulo = titulo.trim();
        this.categoria = categoria.trim();
        this.duracaoMinutos = duracaoMinutos;
        this.classificacaoEtaria = classificacaoEtaria;
        this.disponivel = disponivel;
    }

    public double calcularPrecoAluguel() {
        return 9.90;
    }

    public double calcularPrecoPromocional() {
        if (this instanceof Promocionavel) {
            Promocionavel promocionavel = (Promocionavel) this;
            return promocionavel.aplicarPromocao(calcularPrecoAluguel());
        }
        return calcularPrecoAluguel();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        validarTexto(titulo, "título");
        this.titulo = titulo.trim();
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) {
        validarTexto(categoria, "categoria");
        this.categoria = categoria.trim();
    }

    public int getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(int duracaoMinutos) {
        validarDuracao(duracaoMinutos);
        this.duracaoMinutos = duracaoMinutos;
    }

    public int getClassificacaoEtaria() { return classificacaoEtaria; }
    public void setClassificacaoEtaria(int classificacaoEtaria) {
        validarClassificacao(classificacaoEtaria);
        this.classificacaoEtaria = classificacaoEtaria;
    }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    private static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DadosInvalidosException("O campo " + campo + " é obrigatório");
        }
    }

    private static void validarDuracao(int duracaoMinutos) {
        if (duracaoMinutos <= 0) {
            throw new DadosInvalidosException("A duração em minutos deve ser maior que zero");
        }
    }

    private static void validarClassificacao(int classificacaoEtaria) {
        if (classificacaoEtaria < 0) {
            throw new DadosInvalidosException("A classificação etária não pode ser negativa");
        }
    }
}
