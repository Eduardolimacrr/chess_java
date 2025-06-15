package model;

/**
 * Representa o tabuleiro 8x8 do jogo. Gerencia a localização de todas as peças.
 */
public class Tabuleiro {
    private final Peca[][] pecas;

    public Tabuleiro() {
        this.pecas = new Peca[8][8];
        iniciarPecas();
    }

    /**
     * Retorna a peça em uma determinada posição (linha e coluna).
     * @param linha A linha (0-7).
     * @param coluna A coluna (0-7).
     * @return A Peça na posição, ou null se a casa estiver vazia.
     */
    public Peca getPeca(int linha, int coluna) {
        if (!isPosicaoValida(linha, coluna)) {
            return null;
        }
        return pecas[linha][coluna];
    }

    /**
     * Retorna a peça em uma determinada Posição.
     * @param p O objeto Posição.
     * @return A Peça na posição, ou null se a casa estiver vazia.
     */
    public Peca getPeca(Posicao p) {
        if (!isPosicaoValida(p)) {
            return null;
        }
        return pecas[p.getLinha()][p.getColuna()];
    }

    /**
     * Coloca uma peça diretamente em uma posição do tabuleiro.
     * Usado para configurar o tabuleiro, para simular jogadas e para desfazer movimentos.
     * @param p A Posição onde a peça será colocada.
     * @param peca A Peça a ser colocada.
     */
    public void setPeca(Posicao p, Peca peca) {
        if (isPosicaoValida(p)) {
            pecas[p.getLinha()][p.getColuna()] = peca;
        }
    }

    /**
     * Move uma peça de uma posição de origem para uma de destino.
     * Este método executa o movimento, marca a peça como "movida" e retorna
     * a peça que foi capturada (se houver), para permitir que o movimento seja desfeito.
     * @param origem A Posição de origem da peça.
     * @param destino A Posição de destino da peça.
     * @return A peça que foi capturada, ou null se a casa de destino estava vazia.
     */
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

    /**
     * Verifica se as coordenadas (linha, coluna) estão dentro dos limites do tabuleiro.
     */
    private boolean isPosicaoValida(int linha, int coluna) {
        return linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8;
    }
    
    /**
     * Verifica se um objeto Posição corresponde a uma casa válida no tabuleiro.
     */
    public boolean isPosicaoValida(Posicao p) {
        if (p == null) return false;
        return isPosicaoValida(p.getLinha(), p.getColuna());
    }

    /**
     * Configura o tabuleiro com a posição inicial padrão de todas as peças.
     */
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