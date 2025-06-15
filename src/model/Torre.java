package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Torre extends Peca implements Serializable {

    private static final long serialVersionUID = 1L;

    public Torre(Cor cor) {
        super(cor, "/assets/" + (cor == Cor.BRANCO ? "torre_branca.png" : "torre_preta.png"));
    }

    @Override
    public String getSimbolo() {
        return getCor() == Cor.BRANCO ? "♖" : "♜";
    }

    @Override
    public List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual) {
        List<Posicao> movimentos = new ArrayList<>();
        Tabuleiro tabuleiro = game.getTabuleiro();

        // Movimento em linha reta nas 4 direções (vertical e horizontal)
        int[][] direcoes = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};

        for (int[] d : direcoes) {
            Posicao proxima = new Posicao(posicaoAtual.getLinha() + d[0], posicaoAtual.getColuna() + d[1]);
            while (tabuleiro.isPosicaoValida(proxima)) {
                Peca pecaNoCaminho = tabuleiro.getPeca(proxima);
                if (pecaNoCaminho == null) {
                    // Casa vazia, pode mover
                    movimentos.add(new Posicao(proxima.getLinha(), proxima.getColuna()));
                } else {
                    // Casa ocupada
                    if (pecaNoCaminho.getCor() != this.getCor()) {
                        // Peça inimiga, pode capturar
                        movimentos.add(new Posicao(proxima.getLinha(), proxima.getColuna()));
                    }
                    // Bloqueado, para de procurar nessa direção
                    break;
                }
                proxima = new Posicao(proxima.getLinha() + d[0], proxima.getColuna() + d[1]);
            }
        }
        return movimentos;
    }
}