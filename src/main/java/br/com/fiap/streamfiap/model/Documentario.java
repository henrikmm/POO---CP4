package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.DadosInvalidosException;
import jakarta.persistence.Entity;

@Entity
public class Documentario extends Conteudo {

    private static final double PRECO_ALUGUEL = 0.0;

    private String tema;

    public Documentario() {
    }

    public Documentario(String titulo, String categoria, int duracaoMinutos, int classificacaoEtaria, boolean disponivel, String tema) {
        super(titulo, categoria, duracaoMinutos, classificacaoEtaria, disponivel);
        setTema(tema);
    }

    @Override
    public double calcularPrecoAluguel() {
        return PRECO_ALUGUEL;
    }

    public String getTema() { return tema; }
    public void setTema(String tema) {
        if (tema == null || tema.isBlank()) {
            throw new DadosInvalidosException("O tema do documentário é obrigatório");
        }
        this.tema = tema.trim();
    }
}
