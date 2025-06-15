package network;

import java.io.Serializable;
import model.Posicao;

public class Move implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Posicao origem;
    private final Posicao destino;

    public Move(Posicao origem, Posicao destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public Posicao getOrigem() {
        return origem;
    }

    public Posicao getDestino() {
        return destino;
    }
}