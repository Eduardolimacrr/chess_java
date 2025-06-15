package ai;

import model.Cor;
import model.Peca;
import model.Peao;
import model.Bispo;
import model.Cavalo;
import model.Dama;
import model.Rei;
import model.Torre;
import model.Tabuleiro;

/**
 * Classe responsável por avaliar a pontuação de um tabuleiro.
 * A pontuação é do ponto de vista das peças BRANCAS (positiva = vantagem branca).
 */
public class Avaliacao {

    private static final int VALOR_PEAO = 100;
    private static final int VALOR_CAVALO = 320;
    private static final int VALOR_BISPO = 330;
    private static final int VALOR_TORRE = 500;
    private static final int VALOR_DAMA = 900;
    private static final int VALOR_REI = 20000;

    // Piece-Square Tables (PSTs)
    // Tabelas que dão pontuações bônus/pênaltis para peças em certas casas.
    // As tabelas são definidas da perspectiva das BRANCAS. Para as pretas, invertemos a linha.
    private static final int[] PST_PEAO = {
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5,  5, 10, 25, 25, 10,  5,  5,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        5, 10, 10,-20,-20, 10, 10,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    };

    private static final int[] PST_CAVALO = {
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50,
    };

    private static final int[] PST_BISPO = {
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20,
    };

    private static final int[] PST_TORRE = {
         0,  0,  0,  0,  0,  0,  0,  0,
         5, 10, 10, 10, 10, 10, 10,  5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
         0,  0,  0,  5,  5,  0,  0,  0
    };

    private static final int[] PST_DAMA = {
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
         -5,  0,  5,  5,  5,  5,  0, -5,
          0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20
    };

    public int avaliar(Tabuleiro tabuleiro) {
        int pontuacao = 0;
        for (int linha = 0; linha < 8; linha++) {
            for (int col = 0; col < 8; col++) {
                Peca peca = tabuleiro.getPeca(linha, col);
                if (peca != null) {
                    pontuacao += getValorPeca(peca, linha, col);
                }
            }
        }
        return pontuacao;
    }

    private int getValorPeca(Peca peca, int linha, int col) {
        int valorBase = 0;
        int valorPosicional = 0;
        int indicePST = peca.getCor() == Cor.BRANCO ? (linha * 8 + col) : ((7 - linha) * 8 + col);

        if (peca instanceof Peao) {
            valorBase = VALOR_PEAO;
            valorPosicional = PST_PEAO[indicePST];
        } else if (peca instanceof Cavalo) {
            valorBase = VALOR_CAVALO;
            valorPosicional = PST_CAVALO[indicePST];
        } else if (peca instanceof Bispo) {
            valorBase = VALOR_BISPO;
            valorPosicional = PST_BISPO[indicePST];
        } else if (peca instanceof Torre) {
            valorBase = VALOR_TORRE;
            valorPosicional = PST_TORRE[indicePST];
        } else if (peca instanceof Dama) {
            valorBase = VALOR_DAMA;
            valorPosicional = PST_DAMA[indicePST];
        } else if (peca instanceof Rei) {
            valorBase = VALOR_REI;
            // PST do Rei será adicionado no futuro para fases do jogo (meio, fim)
        }

        int valorTotal = valorBase + valorPosicional;
        return peca.getCor() == Cor.BRANCO ? valorTotal : -valorTotal;
    }
}