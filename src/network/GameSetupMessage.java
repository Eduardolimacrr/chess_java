package network;

import java.io.Serializable;

public class GameSetupMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long tempoDeJogoMs;

    public GameSetupMessage(long tempoDeJogoMs) {
        this.tempoDeJogoMs = tempoDeJogoMs;
    }

    public long getTempoDeJogoMs() {
        return tempoDeJogoMs;
    }
}