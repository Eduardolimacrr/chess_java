package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final NetworkListener listener;

    public interface NetworkListener {
        void onConnectionEstablished(boolean isServer);
        void onMoveReceived(Move move);
        void onConnectionFailed(String errorMessage);
    }

    public NetworkManager(NetworkListener listener) {
        this.listener = listener;
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            socket = serverSocket.accept();
            setupStreams();
            listener.onConnectionEstablished(true);
            startListening();
        } catch (IOException e) {
            listener.onConnectionFailed("Erro ao criar o servidor: " + e.getMessage());
        }
    }

    public void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            setupStreams();
            listener.onConnectionEstablished(false);
            startListening();
        } catch (IOException e) {
            listener.onConnectionFailed("Não foi possível conectar ao servidor: " + e.getMessage());
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void startListening() {
        try {
            while (socket.isConnected()) {
                Move move = (Move) in.readObject();
                if (move != null) {
                    listener.onMoveReceived(move);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            listener.onConnectionFailed("Conexão perdida: " + e.getMessage());
        }
    }

    public void sendMove(Move move) {
        if (out != null) {
            try {
                out.writeObject(move);
                out.flush();
            } catch (IOException e) {
                System.err.println("Erro ao enviar movimento: " + e.getMessage());
            }
        }
    }

    public void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}