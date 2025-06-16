package network;

import model.Posicao;

public class PromotionMove extends Move {
    private static final long serialVersionUID = 1L;

    private final String tipoPecaPromovida;

    public PromotionMove(Posicao origem, Posicao destino, String tipoPecaPromovida) {
        super(origem, destino);
        this.tipoPecaPromovida = tipoPecaPromovida;
    }

    public String getTipoPecaPromovida() {
        return tipoPecaPromovida;
    }
}