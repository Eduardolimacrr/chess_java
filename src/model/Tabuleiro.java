package model;

import java.io.Serializable; // 1. IMPORT ADICIONADO

// 2. "implements Serializable" ADICIONADO
public class Tabuleiro implements Serializable {
    
    // 3. serialVersionUID ADICIONADO
    private static final long serialVersionUID = 1L;

    private final Peca[][] pecas;

    public Tabuleiro() {
        this.pecas = new Peca[8][8];
        iniciarPecas();
    }

    public Peca getPeca(int linha, int coluna) {
        if (!isPosicaoValida(linha, coluna)) {
            return null;
        }
        return pecas[linha][coluna];
    }

    public Peca getPeca(Posicao p) {
        if (!isPosicaoValida(p)) {
            return null;
        }
        return pecas[p.getLinha()][p.getColuna()];
    }

    public void setPeca(Posicao p, Peca peca) {
        if (isPosicaoValida(p)) {
            pecas[p.getLinha()][p.getColuna()] = peca;
        }
    }

    public Peca moverPeca(Posicao origem, Posicao destino) {
        Peca pecaMovida = getPeca(origem);
        Peca pecaCapturada = getPeca(destino);
        
        if (pecaMovida != null) {
            pecaMovida.setJaMoveu(true);
            setPeca(destino, pecaMovida);
            setPeca(origem, null);
        }
        return pecaCapturada;
    }

    private boolean isPosicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8;
    }
    
    public boolean isPosicaoValida(Posicao p) {
        if (p == null) return false;
        return isPosicaoValida(p.getLinha(), p.getColuna());
    }

    private void iniciarPecas() {
        // Peças Pretas
        setPeca(new Posicao(0, 0), new Torre(Cor.PRETO));
        setPeca(new Posicao(0, 1), new Cavalo(Cor.PRETO));
        setPeca(new Posicao(0, 2), new Bispo(Cor.PRETO));
        setPeca(new Posicao(0, 3), new Dama(Cor.PRETO));
        setPeca(new Posicao(0, 4), new Rei(Cor.PRETO));
        setPeca(new Posicao(0, 5), new Bispo(Cor.PRETO));
        setPeca(new Posicao(0, 6), new Cavalo(Cor.PRETO));
        setPeca(new Posicao(0, 7), new Torre(Cor.PRETO));
        for (int i = 0; i < 8; i++) {
            setPeca(new Posicao(1, i), new Peao(Cor.PRETO));
        }

        // Peças Brancas
        setPeca(new Posicao(7, 0), new Torre(Cor.BRANCO));
        setPeca(new Posicao(7, 1), new Cavalo(Cor.BRANCO));
        setPeca(new Posicao(7, 2), new Bispo(Cor.BRANCO));
        setPeca(new Posicao(7, 3), new Dama(Cor.BRANCO));
        setPeca(new Posicao(7, 4), new Rei(Cor.BRANCO));
        setPeca(new Posicao(7, 5), new Bispo(Cor.BRANCO));
        setPeca(new Posicao(7, 6), new Cavalo(Cor.BRANCO));
        setPeca(new Posicao(7, 7), new Torre(Cor.BRANCO));
        for (int i = 0; i < 8; i++) {
            setPeca(new Posicao(6, i), new Peao(Cor.BRANCO));
        }
    }
}