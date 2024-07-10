// RPGClient.java
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RPGClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = input.readLine()) != null) {
                        System.out.println("Servidor: " + serverMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            System.out.println("Conectado ao servidor. Digite suas mensagens:");
            while (true) {
                String clientMessage = scanner.nextLine();
                output.println(clientMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

