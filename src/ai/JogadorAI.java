package ai;

import model.Cor;
import model.Game;
import model.Peca;
import model.Posicao;

public class JogadorAI {

    private Avaliacao avaliador;
    private Cor corDaIA;

    public JogadorAI(Cor corDaIA) {
        this.avaliador = new Avaliacao();
        this.corDaIA = corDaIA;
    }

    public Posicao[] encontrarMelhorJogada(Game game, int profundidade) {
        Posicao[] melhorJogada = null;
        int melhorValor = (corDaIA == Cor.BRANCO) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        Cor turnoOriginal = game.getTurnoAtual();

        for (Posicao[] jogada : game.getTodosMovimentosLegais(turnoOriginal)) {
            
            Peca pecaMovida = game.getTabuleiro().getPeca(jogada[0]);
            boolean originalJaMoveu = pecaMovida.jaMoveu();
            Posicao enPassantBKP = game.getVulneravelEnPassant();

            // Simula o movimento
            Peca pecaCapturada = game.getTabuleiro().moverPeca(jogada[0], jogada[1]);
            
            // Avalia o resultado da jogada
            // O oponente é quem vai jogar, então isMaximizador é o oposto da cor da IA
            int valorDaJogada = minimax(game, profundidade - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, (corDaIA == Cor.PRETO));
            
            // Desfaz o movimento e restaura o estado perfeitamente
            game.getTabuleiro().moverPeca(jogada[1], jogada[0]);
            game.getTabuleiro().setPeca(jogada[1], pecaCapturada);
            pecaMovida.setJaMoveu(originalJaMoveu);
            game.setVulneravelEnPassant(enPassantBKP);

            if (corDaIA == Cor.BRANCO) {
                if (valorDaJogada >= melhorValor) {
                    melhorValor = valorDaJogada;
                    melhorJogada = jogada;
                }
            } else {
                if (valorDaJogada <= melhorValor) {
                    melhorValor = valorDaJogada;
                    melhorJogada = jogada;
                }
            }
        }
        return melhorJogada;
    }

    private int minimax(Game game, int profundidade, int alfa, int beta, boolean isMaximizador) {
        // Verifica a condição de parada da recursão
        if (profundidade == 0 || game.isFimDeJogo(isMaximizador ? Cor.BRANCO : Cor.PRETO)) {
            return avaliador.avaliar(game.getTabuleiro());
        }

        int melhorEval = isMaximizador ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Cor corDoTurnoAtualSimulado = isMaximizador ? Cor.BRANCO : Cor.PRETO;

        for (Posicao[] jogada : game.getTodosMovimentosLegais(corDoTurnoAtualSimulado)) {
            
            // Salva o estado antes de simular
            Peca pecaMovida = game.getTabuleiro().getPeca(jogada[0]);
            boolean originalJaMoveu = pecaMovida.jaMoveu();
            Posicao enPassantBKP = game.getVulneravelEnPassant();

            // Simula o movimento
            Peca pecaCapturada = game.getTabuleiro().moverPeca(jogada[0], jogada[1]);

            // Chama a recursão para o próximo nível (invertendo o jogador)
            int eval = minimax(game, profundidade - 1, alfa, beta, !isMaximizador);
            
            // Restaura o estado perfeitamente
            game.getTabuleiro().moverPeca(jogada[1], jogada[0]);
            game.getTabuleiro().setPeca(jogada[1], pecaCapturada);
            pecaMovida.setJaMoveu(originalJaMoveu);
            game.setVulneravelEnPassant(enPassantBKP);

            // Lógica da poda Alfa-Beta
            if (isMaximizador) {
                melhorEval = Math.max(melhorEval, eval);
                alfa = Math.max(alfa, eval);
            } else {
                melhorEval = Math.min(melhorEval, eval);
                beta = Math.min(beta, eval);
            }
            if (beta <= alfa) {
                break; // Poda
            }
        }
        return melhorEval;
    }
}