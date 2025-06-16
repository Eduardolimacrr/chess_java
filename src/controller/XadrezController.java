package controller;

import ai.JogadorAI;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import model.Cor;
import model.Game;
import model.Peao;
import model.Peca;
import model.Posicao;
import model.Ranking;
import model.Tabuleiro; // <-- IMPORT ADICIONADO AQUI
import network.GameSetupMessage;
import network.Move;
import network.NetworkManager;
import network.PromotionMove;
import view.JanelaXadrez;

public class XadrezController implements NetworkManager.NetworkListener {
    
    private Game gameModel;
    private JanelaXadrez gameView;
    private Posicao posicaoOrigem = null;
    
    private Ranking ranking;
    private final String ARQUIVO_RANKING = "ranking.dat";

    private boolean modoVsIA = false;
    private Cor corDoJogadorHumano;
    private JogadorAI jogadorAI;

    private Timer timerBrancas;
    private Timer timerPretas;
    
    private boolean modoRede = false;
    private Cor corDoJogadorRede;
    private NetworkManager networkManager;
    private final int PORTA_REDE = 12345;

    public XadrezController(Game model, JanelaXadrez view) {
        this.gameModel = model;
        this.gameView = view;
        
        carregarRanking();
        adicionarActionListeners();
        iniciarJogo();
    }

    private void iniciarJogo() {
        gameView.setVisible(true);
        pararFecharConexao();
        gameView.atualizarTimers(-1, -1);
        gameView.atualizarTabuleiro(new Tabuleiro());
        gameView.setStatus("Bem-vindo! Selecione uma opção no menu 'Jogo'.");
    }
    
    private void reiniciarJogo() {
        pararFecharConexao();
        this.modoVsIA = false;
        long tempo = selecionarTempoDeJogo();
        if (tempo == -2) return;
        
        gameModel = new Game(tempo);
        posicaoOrigem = null;
        configurarEIniciarTimers();
        atualizarView();
    }
    
    private void iniciarJogoVsIA() {
        pararFecharConexao();
        this.modoRede = false;
        long tempo = selecionarTempoDeJogo();
        if (tempo == -2) return;

        Object[] opcoes = {"Brancas", "Pretas"};
        int escolha = JOptionPane.showOptionDialog(gameView, "Você quer jogar de:", "Jogar contra IA",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == -1) return;

        this.modoVsIA = true;
        this.corDoJogadorHumano = (escolha == 0) ? Cor.BRANCO : Cor.PRETO;
        Cor corIA = (corDoJogadorHumano == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
        this.jogadorAI = new JogadorAI(corIA);
        
        gameModel = new Game(tempo);
        posicaoOrigem = null;
        configurarEIniciarTimers();
        atualizarView();
        
        if (gameModel.getTurnoAtual() != corDoJogadorHumano) {
            fazerJogadaDaIA();
        }
    }

    private void iniciarJogoEmRede() {
        pararFecharConexao();
        this.modoVsIA = false;

        Object[] opcoes = {"Criar Jogo (Servidor)", "Entrar em Jogo (Cliente)"};
        int escolha = JOptionPane.showOptionDialog(gameView, "Como você deseja jogar em rede?", "Jogo em Rede",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == -1) return;

        networkManager = new NetworkManager(this);
        
        if (escolha == 0) { // Servidor
            try {
                String ip = InetAddress.getLocalHost().getHostAddress();
                gameView.setStatus("Aguardando oponente em " + ip + "...");
                new Thread(() -> networkManager.startServer(PORTA_REDE)).start();
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(gameView, "Não foi possível obter o IP local.", "Erro de Rede", JOptionPane.ERROR_MESSAGE);
            }
        } else { // Cliente
            String ip = JOptionPane.showInputDialog(gameView, "Digite o endereço de IP do servidor:");
            if (ip != null && !ip.trim().isEmpty()) {
                gameView.setStatus("Conectando ao servidor " + ip + "...");
                new Thread(() -> networkManager.connectToServer(ip.trim(), PORTA_REDE)).start();
            }
        }
    }

    private void adicionarActionListeners() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                final int linha = i;
                final int coluna = j;
                gameView.adicionarListenerBotao(linha, coluna, e -> cliqueNaCasa(new Posicao(linha, coluna)));
            }
        }
        
        gameView.adicionarListenerMenu(
            e -> reiniciarJogo(), 
            e -> iniciarJogoVsIA(),
            e -> iniciarJogoEmRede(),
            e -> salvarJogo(), 
            e -> carregarJogo(),
            e -> mostrarRanking()
        );
    }

    private void cliqueNaCasa(Posicao posClicada) {
        if (gameModel == null || gameModel.isFimDeJogo()) return;
        if (modoVsIA && gameModel.getTurnoAtual() != corDoJogadorHumano) return;
        if (modoRede && gameModel.getTurnoAtual() != corDoJogadorRede) return;

        if (posicaoOrigem == null) {
            Peca pecaNoLocal = gameModel.getTabuleiro().getPeca(posClicada);
            if (pecaNoLocal != null && pecaNoLocal.getCor() == gameModel.getTurnoAtual()) {
                posicaoOrigem = posClicada;
                gameView.destacarCasa(posClicada.getLinha(), posClicada.getColuna(), true);
            }
        } else {
            gameView.destacarCasa(posicaoOrigem.getLinha(), posicaoOrigem.getColuna(), false);
            if (posicaoOrigem.equals(posClicada)) {
                posicaoOrigem = null;
                return;
            }

            Peca pecaMovidaAntesDoMovimento = gameModel.getTabuleiro().getPeca(posicaoOrigem);
            boolean ehPromocao = (pecaMovidaAntesDoMovimento instanceof Peao && 
                                  (posClicada.getLinha() == 0 || posClicada.getLinha() == 7));

            boolean movimentoValido = gameModel.moverPeca(posicaoOrigem, posClicada);
            
            if (movimentoValido) {
                iniciarTimerDoTurno();

                if (ehPromocao) {
                    verificarPromocao(posClicada);
                } else if (modoRede) {
                    networkManager.sendObject(new Move(posicaoOrigem, posClicada));
                }
                
                posicaoOrigem = null;
                atualizarView();
                
                if (verificarFimDeJogo()) return;

                if (modoVsIA) {
                    fazerJogadaDaIA();
                }
            } else {
                posicaoOrigem = null;
            }
        }
    }

    private void fazerJogadaDaIA() {
        pararTimers();
        gameView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Posicao[], Void> worker = new SwingWorker<>() {
            @Override
            protected Posicao[] doInBackground() throws Exception {
                return jogadorAI.encontrarMelhorJogada(gameModel, 3);
            }

            @Override
            protected void done() {
                try {
                    Posicao[] melhorJogada = get();
                    if (melhorJogada != null) {
                        Peca pecaMovidaAntesDoMovimento = gameModel.getTabuleiro().getPeca(melhorJogada[0]);
                        boolean ehPromocao = (pecaMovidaAntesDoMovimento instanceof Peao &&
                                              (melhorJogada[1].getLinha() == 0 || melhorJogada[1].getLinha() == 7));

                        gameModel.moverPeca(melhorJogada[0], melhorJogada[1]);
                        
                        if (ehPromocao) {
                            gameModel.promoverPeao(melhorJogada[1], "DAMA");
                        }
                        
                        atualizarView();
                        if (!verificarFimDeJogo()) {
                           iniciarTimerDoTurno();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    gameView.setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void verificarPromocao(Posicao pos) {
        // A condição foi corrigida para verificar a cor do jogador que acabou de mover.
        if (!modoRede || gameModel.getTurnoAtual() != corDoJogadorRede) {
            Object[] opcoes = {"Dama", "Torre", "Bispo", "Cavalo"};
            String escolha = (String) JOptionPane.showInputDialog(
                gameView, "Escolha uma peça para a promoção:", "Promoção de Peão",
                JOptionPane.PLAIN_MESSAGE, null, opcoes, "Dama");
            
            String pecaEscolhida = (escolha != null) ? escolha : "Dama";
            gameModel.promoverPeao(pos, pecaEscolhida);

            if (modoRede) {
                networkManager.sendObject(new PromotionMove(posicaoOrigem, pos, pecaEscolhida));
            }
        }
    }

    private void atualizarView() {
        if (gameModel == null) return;
        gameView.atualizarTabuleiro(gameModel.getTabuleiro());
        gameView.atualizarTimers(gameModel.getTempoRestanteBrancasMs(), gameModel.getTempoRestantePretasMs());
        
        String status = "Turno das " + (gameModel.getTurnoAtual() == Cor.BRANCO ? "Brancas" : "Pretas");
        if (gameModel.isEmXeque()) {
            status += " (XEQUE!)";
        }
        gameView.setStatus(status);
    }
    
    private boolean verificarFimDeJogo() {
        if (gameModel.isFimDeJogo()) {
            pararFecharConexao();
            String msgFinal;
            
            if (gameModel.isXequeMate()) {
                String vencedorCor = (gameModel.getTurnoAtual() == Cor.BRANCO) ? "Pretas" : "Brancas";
                msgFinal = "XEQUE-MATE! As " + vencedorCor + " venceram!";
                gameView.setStatus(msgFinal);
                
                String nomeVencedor = JOptionPane.showInputDialog(gameView, "Parabéns, " + vencedorCor + "! Digite seu nome:", "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
                if (nomeVencedor != null && !nomeVencedor.trim().isEmpty()) {
                    ranking.adicionarVitoria(nomeVencedor.trim());
                    salvarRanking();
                }
            } else if (gameModel.isEmpate()) {
                msgFinal = "EMPATE! O jogo acabou.";
                gameView.setStatus(msgFinal);
                JOptionPane.showMessageDialog(gameView, "Empate por Afogamento!", "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
            } else { 
                Cor vencedor = gameModel.getTempoRestanteBrancasMs() <= 0 ? Cor.PRETO : Cor.BRANCO;
                String vencedorCor = vencedor == Cor.BRANCO ? "Brancas" : "Pretas";
                msgFinal = "TEMPO ESGOTADO! As " + vencedorCor + " venceram!";
                gameView.setStatus(msgFinal);
                
                String nomeVencedor = JOptionPane.showInputDialog(gameView, "Parabéns, " + vencedorCor + "! Digite seu nome:", "Fim de Jogo", JOptionPane.INFORMATION_MESSAGE);
                if (nomeVencedor != null && !nomeVencedor.trim().isEmpty()) {
                    ranking.adicionarVitoria(nomeVencedor.trim());
                    salvarRanking();
                }
            }
            return true;
        }
        return false;
    }

    private void salvarJogo() {
        if(modoRede) {
            JOptionPane.showMessageDialog(gameView, "Não é possível salvar um jogo em rede.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        pararTimers();
        JFileChooser seletorArquivo = new JFileChooser();
        seletorArquivo.setDialogTitle("Salvar Jogo");
        int userSelection = seletorArquivo.showSaveDialog(gameView);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File arquivoParaSalvar = seletorArquivo.getSelectedFile();
            try {
                Game.salvar(gameModel, arquivoParaSalvar.getAbsolutePath());
                JOptionPane.showMessageDialog(gameView, "Jogo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(gameView, "Erro ao salvar o jogo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        if(gameModel != null) iniciarTimerDoTurno();
    }

    private void carregarJogo() {
        pararFecharConexao();
        JFileChooser seletorArquivo = new JFileChooser();
        seletorArquivo.setDialogTitle("Carregar Jogo");
        int userSelection = seletorArquivo.showOpenDialog(gameView);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File arquivoParaCarregar = seletorArquivo.getSelectedFile();
            try {
                gameModel = Game.carregar(arquivoParaCarregar.getAbsolutePath());
                posicaoOrigem = null;
                modoVsIA = false;
                modoRede = false;
                configurarEIniciarTimers();
                atualizarView();
                JOptionPane.showMessageDialog(gameView, "Jogo carregado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(gameView, "Erro ao carregar o jogo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarRanking() {
        try {
            ranking = Ranking.carregar(ARQUIVO_RANKING);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Não foi possível carregar o ranking, um novo será criado.");
            ranking = new Ranking();
        }
    }

    private void salvarRanking() {
        try {
            Ranking.salvar(ranking, ARQUIVO_RANKING);
        } catch (IOException e) {
            System.err.println("Erro ao salvar o ranking: " + e.getMessage());
        }
    }

    private void mostrarRanking() {
        boolean timersEstavamRodando = (timerBrancas != null && timerBrancas.isRunning()) || (timerPretas != null && timerPretas.isRunning());
        pararTimers();
        Map<String, Integer> rankingOrdenado = ranking.getRankingOrdenado();
        if (rankingOrdenado.isEmpty()) {
            JOptionPane.showMessageDialog(gameView, "Nenhuma vitória registrada ainda.", "Ranking", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("----- RANKING DE VITÓRIAS -----\n\n");
            int pos = 1;
            for (Map.Entry<String, Integer> entry : rankingOrdenado.entrySet()) {
                sb.append(String.format("%d. %s - %d vitórias\n", pos++, entry.getKey(), entry.getValue()));
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(300, 200));
            JOptionPane.showMessageDialog(gameView, scrollPane, "Ranking", JOptionPane.PLAIN_MESSAGE);
        }
        if (timersEstavamRodando) {
            iniciarTimerDoTurno();
        }
    }

    private long selecionarTempoDeJogo() {
        Object[] opcoes = {"Bullet (1 min)", "Blitz (5 min)", "Rápida (10 min)", "Clássica (30 min)", "Sem tempo"};
        String escolha = (String) JOptionPane.showInputDialog(
                gameView, "Selecione o modo de tempo:", "Modo de Jogo",
                JOptionPane.PLAIN_MESSAGE, null, opcoes, "Rápida (10 min)");
        if (escolha == null) return -2; 
        switch (escolha) {
            case "Bullet (1 min)": return 60 * 1000;
            case "Blitz (5 min)": return 5 * 60 * 1000;
            case "Rápida (10 min)": return 10 * 60 * 1000;
            case "Clássica (30 min)": return 30 * 60 * 1000;
            default: return -1;
        }
    }

    private void configurarEIniciarTimers() {
        pararTimers();
        if (gameModel == null || !gameModel.isComTempo()) {
            gameView.atualizarTimers(-1, -1);
            return;
        }
        ActionListener listenerBrancas = e -> {
            gameModel.decrementarTempo(Cor.BRANCO, 1000);
            gameView.atualizarTimers(gameModel.getTempoRestanteBrancasMs(), gameModel.getTempoRestantePretasMs());
            if (gameModel.getTempoRestanteBrancasMs() <= 0) {
                verificarFimDeJogo();
            }
        };
        timerBrancas = new Timer(1000, listenerBrancas);
        ActionListener listenerPretas = e -> {
            gameModel.decrementarTempo(Cor.PRETO, 1000);
            gameView.atualizarTimers(gameModel.getTempoRestanteBrancasMs(), gameModel.getTempoRestantePretasMs());
            if (gameModel.getTempoRestantePretasMs() <= 0) {
                verificarFimDeJogo();
            }
        };
        timerPretas = new Timer(1000, listenerPretas);
        iniciarTimerDoTurno();
    }
    
    private void pararTimers() {
        if (timerBrancas != null) timerBrancas.stop();
        if (timerPretas != null) timerPretas.stop();
    }
    
    private void pararFecharConexao() {
        pararTimers();
        if (networkManager != null) {
            networkManager.close();
            networkManager = null;
        }
        modoRede = false;
    }
    
    private void iniciarTimerDoTurno() {
        if (gameModel == null || !gameModel.isComTempo() || gameModel.isFimDeJogo()) {
            pararTimers();
            return;
        }
        if (gameModel.getTurnoAtual() == Cor.BRANCO) {
            if (timerPretas != null) timerPretas.stop();
            if (timerBrancas != null) timerBrancas.start();
        } else {
            if (timerBrancas != null) timerBrancas.stop();
            if (timerPretas != null) timerPretas.start();
        }
    }

    @Override
    public void onConnectionEstablished(boolean isServer) {
        SwingUtilities.invokeLater(() -> {
            this.modoRede = true;
            this.modoVsIA = false;
            this.corDoJogadorRede = isServer ? Cor.BRANCO : Cor.PRETO;
            
            if (isServer) {
                long tempo = selecionarTempoDeJogo();
                if (tempo == -2) {
                    pararFecharConexao();
                    return;
                }
                networkManager.sendObject(new GameSetupMessage(tempo));
                iniciarJogoDeRede(tempo);
            } else {
                gameView.setStatus("Conectado! Aguardando configuração do servidor...");
            }
        });
    }

    private void iniciarJogoDeRede(long tempoDeJogoMs) {
        gameModel = new Game(tempoDeJogoMs);
        posicaoOrigem = null;
        configurarEIniciarTimers();
        atualizarView();
        
        if (gameModel.getTurnoAtual() != corDoJogadorRede) {
            gameView.setStatus("Jogo em rede iniciado! Aguardando jogada do oponente.");
        } else {
            gameView.setStatus("Jogo em rede iniciado! É a sua vez.");
        }
    }

    @Override
    public void onObjectReceived(Object obj) {
        SwingUtilities.invokeLater(() -> {
            boolean movimentoFeito = false;
            
            if (obj instanceof GameSetupMessage) {
                GameSetupMessage setupMsg = (GameSetupMessage) obj;
                iniciarJogoDeRede(setupMsg.getTempoDeJogoMs());
                return;
            }
            
            if (obj instanceof PromotionMove) {
                PromotionMove pMove = (PromotionMove) obj;
                gameModel.moverPeca(pMove.getOrigem(), pMove.getDestino());
                gameModel.promoverPeao(pMove.getDestino(), pMove.getTipoPecaPromovida());
                movimentoFeito = true;
            } else if (obj instanceof Move) {
                Move move = (Move) obj;
                gameModel.moverPeca(move.getOrigem(), move.getDestino());
                movimentoFeito = true;
            }
            
            if (movimentoFeito) {
                iniciarTimerDoTurno();
                atualizarView();
                verificarFimDeJogo();
            }
        });
    }

    @Override
    public void onConnectionFailed(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(gameView, errorMessage, "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            gameView.setStatus("Falha na conexão.");
            pararFecharConexao();
        });
    }
}