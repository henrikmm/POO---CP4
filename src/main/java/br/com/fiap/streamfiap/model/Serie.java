package br.com.fiap.streamfiap.model;

import br.com.fiap.streamfiap.exception.DadosInvalidosException;
import jakarta.persistence.Entity;

@Entity
public class Serie extends Conteudo implements Promocionavel {

    private static final double PRECO_POR_TEMPORADA = 4.90;
    private static final double FATOR_PROMOCAO = 0.80;

    private int numeroTemporadas;

    public Serie() {
    }

    // cria a série com os dados recebidos
    public Serie(String titulo, String categoria, int duracaoMinutos, int classificacaoEtaria, int numeroTemporadas) {
        super(titulo, categoria, duracaoMinutos, classificacaoEtaria, true);
        setNumeroTemporadas(numeroTemporadas);
    }

    // preço da série: 4.90 por temporada
    @Override
    public double calcularPrecoAluguel() {
        return PRECO_POR_TEMPORADA * numeroTemporadas;
    }

    @Override
    public double aplicarPromocao(double preco) {
        return preco * FATOR_PROMOCAO;
    }

    public int getNumeroTemporadas() { return numeroTemporadas; }
    public void setNumeroTemporadas(int numeroTemporadas) {
        if (numeroTemporadas <= 0) {
            throw new DadosInvalidosException("O número de temporadas deve ser maior que zero");
        }
        this.numeroTemporadas = numeroTemporadas;
    }
}
