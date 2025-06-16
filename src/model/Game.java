package model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private Tabuleiro tabuleiro;
    private Cor turnoAtual;
    private boolean emXeque;
    private boolean xequeMate;
    private boolean empate;
    private Posicao vulneravelEnPassant;
    
    private long tempoRestanteBrancasMs;
    private long tempoRestantePretasMs;
    private boolean tempoEsgotado = false;
    private final boolean comTempo;

    public Game(long tempoInicialMs) {
        this.tabuleiro = new Tabuleiro();
        this.turnoAtual = Cor.BRANCO;
        this.vulneravelEnPassant = null;
        this.tempoRestanteBrancasMs = tempoInicialMs;
        this.tempoRestantePretasMs = tempoInicialMs;
        this.comTempo = tempoInicialMs > 0;
    }
    
    public Game() {
        this(-1); 
    }

    public Tabuleiro getTabuleiro() { return tabuleiro; }
    public Cor getTurnoAtual() { return turnoAtual; }
    public boolean isEmXeque() { return emXeque; }
    public boolean isXequeMate() { return xequeMate; }
    public boolean isEmpate() { return empate; }
    public Posicao getVulneravelEnPassant() { return vulneravelEnPassant; }
    public long getTempoRestanteBrancasMs() { return tempoRestanteBrancasMs; }
    public long getTempoRestantePretasMs() { return tempoRestantePretasMs; }
    public boolean isComTempo() { return comTempo; }
    public void setVulneravelEnPassant(Posicao p) { this.vulneravelEnPassant = p; }

    public void decrementarTempo(Cor cor, long ms) {
        if (cor == Cor.BRANCO) {
            this.tempoRestanteBrancasMs -= ms;
            if (this.tempoRestanteBrancasMs <= 0) {
                this.tempoRestanteBrancasMs = 0;
                this.tempoEsgotado = true;
            }
        } else {
            this.tempoRestantePretasMs -= ms;
            if (this.tempoRestantePretasMs <= 0) {
                this.tempoRestantePretasMs = 0;
                this.tempoEsgotado = true;
            }
        }
    }
    
    public boolean isFimDeJogo() {
        return xequeMate || empate || tempoEsgotado;
    }

    public boolean isFimDeJogo(Cor corDoJogador) {
        if (tempoEsgotado) return true;
        if (getTodosMovimentosLegais(corDoJogador).isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean moverPeca(Posicao origem, Posicao destino) {
        if (isFimDeJogo()) return false;
        
        Peca pecaMovida = tabuleiro.getPeca(origem);
        if (pecaMovida == null || pecaMovida.getCor() != turnoAtual) return false;

        List<Posicao> movimentosPossiveis = pecaMovida.getMovimentosPossiveis(this, origem);
        if (!movimentosPossiveis.contains(destino)) return false;
        
        // --- INÍCIO DA LÓGICA DE MOVIMENTAÇÃO REVISADA ---
        
        // 1. Salva o estado atual para um possível "desfazer"
        boolean jaMoveuBKP = pecaMovida.jaMoveu();
        Posicao enPassantBKP = vulneravelEnPassant;
        
        // 2. Identifica se é um En Passant e processa a captura especial
        Peca pecaCapturada = null;
        boolean foiEnPassant = (pecaMovida instanceof Peao && 
                                origem.getColuna() != destino.getColuna() && 
                                tabuleiro.getPeca(destino) == null);

        if (foiEnPassant) {
            Posicao posPeaoCapturado = new Posicao(origem.getLinha(), destino.getColuna());
            pecaCapturada = tabuleiro.getPeca(posPeaoCapturado);
            tabuleiro.setPeca(posPeaoCapturado, null);
        } else {
            pecaCapturada = tabuleiro.getPeca(destino);
        }

        // 3. Executa o movimento principal
        this.vulneravelEnPassant = null; // Limpa a flag para o novo turno
        tabuleiro.moverPeca(origem, destino);
        
        // Lógica de Roque
        if (pecaMovida instanceof Rei && Math.abs(origem.getColuna() - destino.getColuna()) == 2) {
            if (destino.getColuna() > origem.getColuna()) {
                tabuleiro.moverPeca(new Posicao(origem.getLinha(), 7), new Posicao(origem.getLinha(), 5));
            } else {
                tabuleiro.moverPeca(new Posicao(origem.getLinha(), 0), new Posicao(origem.getLinha(), 3));
            }
        }
        
        // 4. Verifica se o movimento foi legal (não deixou o próprio rei em xeque)
        if (isReiEmCheque(turnoAtual)) {
            // O movimento é ilegal, desfaz tudo.
            tabuleiro.setPeca(origem, pecaMovida);
            pecaMovida.setJaMoveu(jaMoveuBKP);
            this.vulneravelEnPassant = enPassantBKP;
            
            if (foiEnPassant) {
                tabuleiro.setPeca(destino, null); // Esvazia o destino
                Posicao posPeaoCapturado = new Posicao(origem.getLinha(), destino.getColuna());
                tabuleiro.setPeca(posPeaoCapturado, pecaCapturada); // Retorna o peão capturado
            } else {
                tabuleiro.setPeca(destino, pecaCapturada); // Retorna a peça capturada normalmente
            }
            return false;
        }

        // 5. O movimento foi legal, confirma as alterações de estado.
        if (pecaMovida instanceof Peao && Math.abs(origem.getLinha() - destino.getLinha()) == 2) {
            this.vulneravelEnPassant = destino;
        }

        trocarTurno();
        atualizarStatusDoJogo();
        return true;
    }

    public void promoverPeao(Posicao pos, String tipoPeca) {
        Peca peao = tabuleiro.getPeca(pos);
        if (peao == null || !(peao instanceof Peao)) return;
        Peca novaPeca;
        switch (tipoPeca.toUpperCase()) {
            case "DAMA": novaPeca = new Dama(peao.getCor()); break;
            case "TORRE": novaPeca = new Torre(peao.getCor()); break;
            case "BISPO": novaPeca = new Bispo(peao.getCor()); break;
            default: novaPeca = new Cavalo(peao.getCor()); break;
        }
        tabuleiro.setPeca(pos, novaPeca);
        atualizarStatusDoJogo();
    }
    
    public void trocarTurno() {
        turnoAtual = (turnoAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    private void atualizarStatusDoJogo() {
        this.emXeque = isReiEmCheque(turnoAtual);
        if (getTodosMovimentosLegais(turnoAtual).isEmpty()) {
            if (this.emXeque) this.xequeMate = true;
            else this.empate = true;
        } else {
            this.xequeMate = false;
            this.empate = false;
        }
    }

    public boolean isCasaAtacada(Posicao pos, Cor corAtacante) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Posicao posAtual = new Posicao(i, j);
                Peca peca = tabuleiro.getPeca(posAtual);
                if (peca != null && peca.getCor() == corAtacante) {
                    if (peca instanceof Rei) {
                        int distanciaLinha = Math.abs(peca.getPosicao(tabuleiro).getLinha() - pos.getLinha());
                        int distanciaColuna = Math.abs(peca.getPosicao(tabuleiro).getColuna() - pos.getColuna());
                        if(distanciaLinha <= 1 && distanciaColuna <= 1) {
                            return true;
                        }
                    } else if (peca instanceof Peao) {
                        int direcao = (peca.getCor() == Cor.BRANCO) ? -1 : 1;
                        if ((pos.getLinha() == i + direcao) && (Math.abs(pos.getColuna() - j) == 1)) return true;
                    } else if (peca.getMovimentosPossiveis(this, posAtual).contains(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isReiEmCheque(Cor corDoRei) {
        Posicao posicaoDoRei = encontrarRei(corDoRei);
        if (posicaoDoRei == null) return true;
        return isCasaAtacada(posicaoDoRei, (corDoRei == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO);
    }
    
    private Posicao encontrarRei(Cor cor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Posicao p = new Posicao(i,j);
                Peca peca = tabuleiro.getPeca(p);
                if (peca instanceof Rei && peca.getCor() == cor) return p;
            }
        }
        return null;
    }
    
    public List<Posicao[]> getTodosMovimentosLegais(Cor corDoJogador) {
        List<Posicao[]> movimentosLegais = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Posicao origem = new Posicao(i, j);
                Peca peca = tabuleiro.getPeca(origem);
                if (peca != null && peca.getCor() == corDoJogador) {
                    List<Posicao> movimentosPossiveis = peca.getMovimentosPossiveis(this, origem);
                    for (Posicao destino : movimentosPossiveis) {
                        Posicao enPassantBKP = this.vulneravelEnPassant;
                        boolean originalJaMoveu = peca.jaMoveu();
                        
                        Peca pecaCapturada = null;
                        boolean foiEnPassant = (peca instanceof Peao && destino.equals(enPassantBKP) && tabuleiro.getPeca(destino) == null);

                        if (foiEnPassant) {
                            Posicao posPeaoCapturado = new Posicao(origem.getLinha(), destino.getColuna());
                            pecaCapturada = tabuleiro.getPeca(posPeaoCapturado);
                            tabuleiro.setPeca(posPeaoCapturado, null);
                        } else {
                            pecaCapturada = tabuleiro.getPeca(destino);
                        }
                        
                        tabuleiro.moverPeca(origem, destino);
                        
                        if (!isReiEmCheque(corDoJogador)) {
                            movimentosLegais.add(new Posicao[]{origem, destino});
                        }
                        
                        tabuleiro.moverPeca(destino, origem);
                        peca.setJaMoveu(originalJaMoveu);
                        this.vulneravelEnPassant = enPassantBKP;
                        
                        if (foiEnPassant) {
                            tabuleiro.setPeca(destino, null);
                            Posicao posPeaoCapturado = new Posicao(origem.getLinha(), destino.getColuna());
                            tabuleiro.setPeca(posPeaoCapturado, pecaCapturada);
                        } else {
                            tabuleiro.setPeca(destino, pecaCapturada);
                        }
                    }
                }
            }
        }
        return movimentosLegais;
    }

    public static void salvar(Game game, String caminhoArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(game);
        }
    }

    public static Game carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            return (Game) ois.readObject();
        }
    }
}