package model;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Ranking implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, Integer> pontuacoes;

    public Ranking() {
        this.pontuacoes = new HashMap<>();
    }

    public void adicionarVitoria(String nomeJogador) {
        // Usa compute para incrementar o valor ou iniciar em 1 se não existir
        pontuacoes.compute(nomeJogador, (nome, vitorias) -> (vitorias == null) ? 1 : vitorias + 1);
    }
    
    /**
     * Retorna o ranking ordenado por número de vitórias (do maior para o menor).
     */
    public Map<String, Integer> getRankingOrdenado() {
        return pontuacoes.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                    Map.Entry::getKey, 
                    Map.Entry::getValue, 
                    (e1, e2) -> e1, 
                    LinkedHashMap::new
                ));
    }

    public static void salvar(Ranking ranking, String caminhoArquivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(caminhoArquivo))) {
            oos.writeObject(ranking);
        }
    }

    public static Ranking carregar(String caminhoArquivo) throws IOException, ClassNotFoundException {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.exists()) {
            return new Ranking(); // Se o arquivo não existe, cria um novo ranking
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(caminhoArquivo))) {
            return (Ranking) ois.readObject();
        }
    }
}