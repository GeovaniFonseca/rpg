// RPGServer.java
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class RPGServer {
    private static final int PORT = 12345;
    private static final int MAX_PLAYERS = 2;
    private static final ExecutorService playerThreads = Executors.newFixedThreadPool(MAX_PLAYERS);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName("192.168.1.2"))) {
            System.out.println("Servidor do RPG iniciado...");
            int playerCount = 0;

            while (playerCount < MAX_PLAYERS) {
                Socket playerSocket = serverSocket.accept();
                playerCount++;
                System.out.println("Jogador " + playerCount + " conectado.");
                playerThreads.execute(new PlayerHandler(playerSocket, playerCount));
            }
            playerThreads.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
