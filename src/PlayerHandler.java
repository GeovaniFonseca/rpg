// PlayerHandler.java
import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerHandler implements Runnable {
    private static final Lock gameLock = new ReentrantLock();
    private static final Condition playerTurnCondition = gameLock.newCondition();
    private static int currentPlayerTurn = 1;
    private static Personagem[] personagens = new Personagem[2];
    private static Personagem dragao = new Dragao();  // Estado compartilhado do dragão
    private static int[] pontos = {0, 0};  // Pontuação dos jogadores
    private static final int PONTOS_PARA_VENCER = 5;

    private Socket playerSocket;
    private int playerId;
    private BufferedReader input;
    private PrintWriter output;

    public PlayerHandler(Socket socket, int id) {
        this.playerSocket = socket;
        this.playerId = id;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(playerSocket.getInputStream()));
            output = new PrintWriter(playerSocket.getOutputStream(), true);

            output.println("Você é o Jogador " + playerId);
            output.println("Escolha seu personagem: 1. Guerreiro 2. Mago 3. Arqueiro");
            int escolhaPersonagem = Integer.parseInt(input.readLine());

            Arma armaEscolhida = null;
            output.println("Escolha sua arma:");
            if (escolhaPersonagem == 1) {
                output.println("1. Espada (+10 ATQ, +15 DEF) 2. Machado (+17 ATQ, +9 DEF)");
                int escolhaArma = Integer.parseInt(input.readLine());
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Espada", 10, 15);
                } else {
                    armaEscolhida = new Arma("Machado", 17, 9);
                }
                personagens[playerId - 1] = new Guerreiro("Guerreiro", armaEscolhida);
            } else if (escolhaPersonagem == 2) {
                output.println("1. Varinha (+8 ATQ, +9 DEF) 2. Cajado (+13 ATQ, +12 DEF)");
                int escolhaArma = Integer.parseInt(input.readLine());
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Varinha", 8, 9);
                } else {
                    armaEscolhida = new Arma("Cajado", 13, 12);
                }
                personagens[playerId - 1] = new Mago("Mago", armaEscolhida);
            } else if (escolhaPersonagem == 3) {
                output.println("1. Arco Longo (+12 ATQ, +13 DEF) 2. Balestra (+15 ATQ, +10 DEF)");
                int escolhaArma = Integer.parseInt(input.readLine());
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Arco Longo", 12, 13);
                } else {
                    armaEscolhida = new Arma("Balestra", 15, 10);
                }
                personagens[playerId - 1] = new Arqueiro("Arqueiro", armaEscolhida);
            }

            output.println("Aguardando o outro jogador...");

            while (true) {
                gameLock.lock();
                try {
                    while (currentPlayerTurn != playerId) {
                        playerTurnCondition.await();
                    }

                    if (!personagens[playerId - 1].estaVivo()) {
                        output.println("Você foi derrotado.");
                        break;
                    }

                    output.println("Sua vez! Escolha sua ação: 1. Atacar 2. Defender");
                    String action = input.readLine();

                    if (action.equals("1")) {
                        atacar();
                    } else if (action.equals("2")) {
                        defender();
                    }

                    if (pontos[playerId - 1] >= PONTOS_PARA_VENCER) {
                        output.println("Você venceu o jogo!");
                        output.println("Finalizando conexão...");
                        break;
                    }

                    currentPlayerTurn = (currentPlayerTurn % 2) + 1;
                    playerTurnCondition.signalAll();
                } finally {
                    gameLock.unlock();
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                playerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void atacar() {
        Personagem atacante = personagens[playerId - 1];

        int dano = calcularDano(atacante.calcularAtaque(), dragao.calcularDefesa());
        dragao.setPontosDeVida(dragao.getPontosDeVida() - dano);
        output.println("Você atacou e causou " + dano + " de dano. O Dragão tem " + dragao.getPontosDeVida() + " pontos de vida.");

        if (!dragao.estaVivo()) {
            output.println("Você derrotou o dragão!");
            pontos[playerId - 1]++;
            dragao = new Dragao();  // Restabelece os pontos de vida do dragão
        }
    }

    private void defender() {
        personagens[playerId - 1].defender();
        output.println("Você escolheu defender.");
    }

    private int calcularDano(int ataque, int defesa) {
        return Math.max(ataque - defesa, 1); // Garante que o dano seja pelo menos 1
    }
}
