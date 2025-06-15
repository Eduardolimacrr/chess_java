package model;

import java.io.Serializable;
import java.util.Objects;

public class Posicao implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int linha;
    private int coluna;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Posicao posicao = (Posicao) o;
        return linha == posicao.linha && coluna == posicao.coluna;
    }

    @Override
    public int hashCode() {
        return Objects.hash(linha, coluna);
    }
}