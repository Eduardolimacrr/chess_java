package model;

import java.awt.Image;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public abstract class Peca implements Serializable {
    
    private static final long serialVersionUID = 1L;
    protected Cor cor;
    private ImageIcon imagem;
    private boolean jaMoveu;

    public Peca(Cor cor, String imagemPath) {
        this.cor = cor;
        this.jaMoveu = false;
        
        // --- INÍCIO DA LÓGICA DE CARREGAMENTO DE IMAGEM REVISADA ---
        try {
            // Usamos getResourceAsStream, que é mais robusto para encontrar recursos.
            InputStream stream = getClass().getResourceAsStream(imagemPath);
            
            if (stream != null) {
                // Se o recurso foi encontrado, lemos a imagem a partir dele.
                Image img = ImageIO.read(stream);
                this.imagem = new ImageIcon(img);
            } else {
                // Se o stream for nulo, o arquivo não foi encontrado no classpath.
                System.err.println("ERRO: Recurso não encontrado no classpath: " + imagemPath);
                this.imagem = null;
            }
        } catch (Exception e) {
            // Captura qualquer outro erro durante a leitura da imagem.
            System.err.println("Exceção ao carregar imagem: " + imagemPath);
            e.printStackTrace(); // Imprime o erro completo para mais detalhes.
            this.imagem = null;
        }
        // --- FIM DA LÓGICA DE CARREGAMENTO ---
    }

    public Cor getCor() {
        return cor;
    }

    public ImageIcon getImagem() {
        return imagem;
    }

    public boolean jaMoveu() {
        return jaMoveu;
    }
    
    public void setJaMoveu(boolean jaMoveu) {
        this.jaMoveu = jaMoveu;
    }

    public Posicao getPosicao(Tabuleiro tabuleiro) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tabuleiro.getPeca(i, j) == this) {
                    return new Posicao(i, j);
                }
            }
        }
        return null;
    }

    public abstract String getSimbolo();

    public abstract List<Posicao> getMovimentosPossiveis(Game game, Posicao posicaoAtual);
}