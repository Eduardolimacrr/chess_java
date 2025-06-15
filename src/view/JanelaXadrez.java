package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import model.Peca;
import model.Tabuleiro;

public class JanelaXadrez extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel painelTabuleiro;
    private JButton[][] botoes;
    private JLabel statusLabel;
    
    private JLabel timerBrancasLabel;
    private JLabel timerPretasLabel;

    private JMenuItem itemNovoJogo;
    private JMenuItem itemJogarVsIA;
    private JMenuItem itemJogoEmRede; // NOVO ITEM
    private JMenuItem itemSalvarJogo;
    private JMenuItem itemCarregarJogo;
    private JMenuItem itemVerRanking;

    public JanelaXadrez() {
        setTitle("Jogo de Xadrez");
        setSize(600, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JMenuBar menuBar = new JMenuBar();
        JMenu menuJogo = new JMenu("Jogo");
        
        itemNovoJogo = new JMenuItem("Novo Jogo (Jogador vs Jogador)");
        itemJogarVsIA = new JMenuItem("Jogar contra IA");
        itemJogoEmRede = new JMenuItem("Jogo em Rede..."); // INICIALIZADO
        itemSalvarJogo = new JMenuItem("Salvar Jogo");
        itemCarregarJogo = new JMenuItem("Carregar Jogo...");
        itemVerRanking = new JMenuItem("Ver Ranking");

        menuJogo.add(itemNovoJogo);
        menuJogo.add(itemJogarVsIA);
        menuJogo.add(itemJogoEmRede); // ADICIONADO AO MENU
        menuJogo.addSeparator();
        menuJogo.add(itemSalvarJogo);
        menuJogo.add(itemCarregarJogo);
        menuJogo.addSeparator();
        menuJogo.add(itemVerRanking);
        menuBar.add(menuJogo);
        setJMenuBar(menuBar);

        statusLabel = new JLabel("Bem-vindo! Selecione uma opção no menu 'Jogo'.", SwingConstants.CENTER);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        add(statusLabel, BorderLayout.NORTH);

        painelTabuleiro = new JPanel(new GridLayout(8, 8));
        add(painelTabuleiro, BorderLayout.CENTER);

        botoes = new JButton[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                botoes[i][j] = new JButton();
                botoes[i][j].setFocusPainted(false);
                if ((i + j) % 2 == 0) {
                    botoes[i][j].setBackground(new Color(240, 217, 181));
                } else {
                    botoes[i][j].setBackground(new Color(181, 136, 99));
                }
                painelTabuleiro.add(botoes[i][j]);
            }
        }
        
        JPanel painelTimers = new JPanel(new GridLayout(1, 2));
        Font timerFont = new Font("Arial", Font.BOLD, 24);
        
        timerPretasLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerPretasLabel.setFont(timerFont);
        timerPretasLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        timerBrancasLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerBrancasLabel.setFont(timerFont);
        timerBrancasLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        painelTimers.add(timerPretasLabel);
        painelTimers.add(timerBrancasLabel);
        add(painelTimers, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    public void atualizarTimers(long tempoBrancasMs, long tempoPretasMs) {
        timerBrancasLabel.setText(formatarTempo(tempoBrancasMs));
        timerPretasLabel.setText(formatarTempo(tempoPretasMs));
    }
    
    private String formatarTempo(long millis) {
        if (millis < 0) return "--:--";
        long minutos = TimeUnit.MILLISECONDS.toMinutes(millis);
        long segundos = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutos);
        return String.format("%02d:%02d", minutos, segundos);
    }
    
    public void adicionarListenerMenu(ActionListener listenerNovo, ActionListener listenerJogarVsIA, ActionListener listenerRede, 
                                      ActionListener listenerSalvar, ActionListener listenerCarregar, ActionListener listenerRanking) {
        itemNovoJogo.addActionListener(listenerNovo);
        itemJogarVsIA.addActionListener(listenerJogarVsIA);
        itemJogoEmRede.addActionListener(listenerRede);
        itemSalvarJogo.addActionListener(listenerSalvar);
        itemCarregarJogo.addActionListener(listenerCarregar);
        itemVerRanking.addActionListener(listenerRanking);
    }

    public void atualizarTabuleiro(Tabuleiro tabuleiro) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Peca peca = tabuleiro.getPeca(i, j);
                if (peca != null) {
                    if (peca.getImagem() != null) {
                        botoes[i][j].setIcon(peca.getImagem());
                        botoes[i][j].setText("");
                    } else {
                        botoes[i][j].setIcon(null);
                        botoes[i][j].setText(peca.getSimbolo());
                        botoes[i][j].setFont(botoes[i][j].getFont().deriveFont(30f));
                    }
                } else {
                    botoes[i][j].setIcon(null);
                    botoes[i][j].setText("");
                }
            }
        }
    }

    public void adicionarListenerBotao(int linha, int coluna, ActionListener listener) {
        botoes[linha][coluna].addActionListener(listener);
    }
    
    public void setStatus(String texto) {
        statusLabel.setText(texto);
    }

    public void destacarCasa(int linha, int coluna, boolean destacar) {
        if (destacar) {
            botoes[linha][coluna].setBorder(new LineBorder(Color.YELLOW, 3));
        } else {
            botoes[linha][coluna].setBorder(null);
        }
    }
}