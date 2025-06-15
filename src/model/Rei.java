package model;

import java.util.ArrayList;
import java.util.List;

public class Rei extends Peca {

    private static final long serialVersionUID = 1L;

    public Rei(Cor cor) {
        super(cor, "/assets/" + (cor == Cor.BRANCO ? "rei_branco.png" : "rei_preto.png"));
    }

    @Override
    public String getSimbolo() {
        return getCor() == Cor.BRANCO ? "♔" : "♚";
    }

    @Override
    public List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual) {
        List<Posicao> movimentos = new ArrayList<>();
        int linha = posicaoAtual.getLinha();
        int coluna = posicaoAtual.getColuna();

        // Movimentos normais de 1 casa
        int[][] movimentosRei = {
            {-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}
        };
        for (int[] m : movimentosRei) {
            Posicao destino = new Posicao(linha + m[0], coluna + m[1]);
            if (game.getTabuleiro().isPosicaoValida(destino)) {
                Peca pecaDestino = game.getTabuleiro().getPeca(destino);
                if (pecaDestino == null || pecaDestino.getCor() != this.getCor()) {
                    movimentos.add(destino);
                }
            }
        }

        // LÓGICA DO ROQUE
        if (!this.jaMoveu() && !game.isEmXeque()) {
            // Roque menor (lado do rei)
            verificarRoque(game, posicaoAtual, 1, movimentos);
            // Roque maior (lado da dama)
            verificarRoque(game, posicaoAtual, -1, movimentos);
        }
        
        return movimentos;
    }

    private void verificarRoque(Game game, Posicao posRei, int direcao, List<Posicao> movimentos) {
        Tabuleiro tab = game.getTabuleiro();
        int linha = posRei.getLinha();
        
        // Verifica se a torre está na posição inicial e não se moveu
        Posicao posTorre = new Posicao(linha, direcao == 1 ? 7 : 0);
        Peca torre = tab.getPeca(posTorre);
        if (torre instanceof Torre && !torre.jaMoveu()) {
            
            // Verifica se o caminho está livre
            boolean caminhoLivre = true;
            for (int col = posRei.getColuna() + direcao; col != posTorre.getColuna(); col += direcao) {
                if (tab.getPeca(new Posicao(linha, col)) != null) {
                    caminhoLivre = false;
                    break;
                }
            }
            
            if (caminhoLivre) {
                // Verifica se o rei não passa por uma casa atacada
                Posicao posPassagem = new Posicao(linha, posRei.getColuna() + direcao);
                Posicao posDestino = new Posicao(linha, posRei.getColuna() + 2 * direcao);
                
                if (!game.isCasaAtacada(posPassagem, this.getCor().equals(Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO) &&
                    !game.isCasaAtacada(posDestino, this.getCor().equals(Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO)) {
                    movimentos.add(posDestino);
                }
            }
        }
    }
}