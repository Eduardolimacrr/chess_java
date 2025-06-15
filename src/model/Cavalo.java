package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cavalo extends Peca implements Serializable {

    private static final long serialVersionUID = 1L;

    public Cavalo(Cor cor) {
        super(cor, "/assets/" + (cor == Cor.BRANCO ? "cavalo_branco.png" : "cavalo_preto.png"));
    }

    @Override
    public String getSimbolo() {
        return getCor() == Cor.BRANCO ? "♘" : "♞";
    }

    @Override
    public List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual) {
        List<Posicao> movimentos = new ArrayList<>();
        Tabuleiro tabuleiro = game.getTabuleiro();
        int linha = posicaoAtual.getLinha();
        int coluna = posicaoAtual.getColuna();

        // Todos os 8 movimentos em "L" possíveis
        int[][] movimentosL = {
            {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
            {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };

        for (int[] m : movimentosL) {
            Posicao destino = new Posicao(linha + m[0], coluna + m[1]);
            if (tabuleiro.isPosicaoValida(destino)) {
                Peca pecaDestino = tabuleiro.getPeca(destino);
                // Pode mover se a casa estiver vazia ou tiver uma peça inimiga
                if (pecaDestino == null || pecaDestino.getCor() != this.getCor()) {
                    movimentos.add(destino);
                }
            }
        }
        return movimentos;
    }
}