package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkManager {
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final NetworkListener listener;

    public interface NetworkListener {
        void onConnectionEstablished(boolean isServer);
        void onObjectReceived(Object obj); // Alterado para receber um Object genérico
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
            while (socket != null && socket.isConnected()) {
                Object obj = in.readObject();
                if (obj != null) {
                    listener.onObjectReceived(obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!socket.isClosed()) {
               listener.onConnectionFailed("Conexão perdida: " + e.getMessage());
            }
        }
    }

    // Alterado para enviar um objeto Serializable genérico
    public void sendObject(Serializable obj) {
        if (out != null) {
            try {
                out.writeObject(obj);
                out.flush();
                out.reset(); // Importante para garantir que o objeto seja reenviado
            } catch (IOException e) {
                System.err.println("Erro ao enviar objeto: " + e.getMessage());
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