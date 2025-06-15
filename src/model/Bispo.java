package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bispo extends Peca implements Serializable {

    private static final long serialVersionUID = 1L;

    public Bispo(Cor cor) {
        super(cor, "/assets/" + (cor == Cor.BRANCO ? "bispo_branco.png" : "bispo_preto.png"));
    }

    @Override
    public String getSimbolo() {
        return getCor() == Cor.BRANCO ? "♗" : "♝";
    }

    @Override
    public List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual) {
        List<Posicao> movimentos = new ArrayList<>();
        Tabuleiro tabuleiro = game.getTabuleiro();

        // Movimento nas 4 diagonais
        int[][] direcoes = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] d : direcoes) {
            Posicao proxima = new Posicao(posicaoAtual.getLinha() + d[0], posicaoAtual.getColuna() + d[1]);
            while (tabuleiro.isPosicaoValida(proxima)) {
                Peca pecaNoCaminho = tabuleiro.getPeca(proxima);
                if (pecaNoCaminho == null) {
                    movimentos.add(new Posicao(proxima.getLinha(), proxima.getColuna()));
                } else {
                    if (pecaNoCaminho.getCor() != this.getCor()) {
                        movimentos.add(new Posicao(proxima.getLinha(), proxima.getColuna()));
                    }
                    break;
                }
                proxima = new Posicao(proxima.getLinha() + d[0], proxima.getColuna() + d[1]);
            }
        }
        return movimentos;
    }
}