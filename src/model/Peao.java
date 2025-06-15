package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Peao extends Peca implements Serializable {

    private static final long serialVersionUID = 1L;

    public Peao(Cor cor) {
        super(cor, "/assets/" + (cor == Cor.BRANCO ? "peao_branco.png" : "peao_preto.png"));
    }

    @Override
    public String getSimbolo() {
        return getCor() == Cor.BRANCO ? "♙" : "♟";
    }

    @Override
    public List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual) {
        List<Posicao> movimentos = new ArrayList<>();
        Tabuleiro tabuleiro = game.getTabuleiro();
        int linha = posicaoAtual.getLinha();
        int coluna = posicaoAtual.getColuna();
        
        // Peças brancas movem para cima (linha diminui), pretas para baixo (linha aumenta)
        int direcao = (getCor() == Cor.BRANCO) ? -1 : 1;

        // --- LÓGICA CORRIGIDA PARA MOVIMENTOS PARA FRENTE ---

        // 1. Movimento de uma casa para frente
        Posicao umaCasaFrente = new Posicao(linha + direcao, coluna);
        if (tabuleiro.isPosicaoValida(umaCasaFrente) && tabuleiro.getPeca(umaCasaFrente) == null) {
            movimentos.add(umaCasaFrente);

            // 2. Movimento de duas casas para frente (só a partir da posição inicial)
            // Esta lógica agora está dentro da verificação da primeira casa.
            if (!jaMoveu()) {
                Posicao duasCasasFrente = new Posicao(linha + 2 * direcao, coluna);
                if (tabuleiro.isPosicaoValida(duasCasasFrente) && tabuleiro.getPeca(duasCasasFrente) == null) {
                    movimentos.add(duasCasasFrente);
                }
            }
        }

        // --- LÓGICA DE CAPTURA (SEM ALTERAÇÕES) ---

        // 3. Captura diagonal
        int[] colunasCaptura = {coluna - 1, coluna + 1};
        for (int c : colunasCaptura) {
            Posicao casaCaptura = new Posicao(linha + direcao, c);
            if (tabuleiro.isPosicaoValida(casaCaptura)) {
                Peca pecaNoDestino = tabuleiro.getPeca(casaCaptura);
                if (pecaNoDestino != null && pecaNoDestino.getCor() != this.getCor()) {
                    movimentos.add(casaCaptura);
                }
            }
        }

        // 4. Captura En Passant
        Posicao enPassantPos = game.getVulneravelEnPassant();
        if (enPassantPos != null) {
            if (enPassantPos.getLinha() == linha && Math.abs(enPassantPos.getColuna() - coluna) == 1) {
                 movimentos.add(new Posicao(linha + direcao, enPassantPos.getColuna()));
            }
        }

        return movimentos;
    }
}