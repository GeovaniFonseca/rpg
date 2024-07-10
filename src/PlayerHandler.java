// PlayerHandler.java
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Random;
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
    private static boolean jogoAtivo = true;

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
                    armaEscolhida = new Arma("Espada", 100, 15);
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

            GameLog.addLog("Jogador " + playerId + " escolheu " + personagens[playerId - 1].getNome() + " com " + armaEscolhida.getNome());
            output.println("Aguardando o outro jogador...");

            while (jogoAtivo) {
                gameLock.lock();
                try {
                    while (currentPlayerTurn != playerId) {
                        playerTurnCondition.await();
                    }

                    if (!personagens[playerId - 1].estaVivo()) {
                        output.println("Você foi derrotado.");
                        break;
                    }

                    output.println("Sua vez! Escolha sua ação: 1. Atacar 2. Defender 3. Ver histórico de batalhas");
                    String action = input.readLine();

                    if (action.equals("1")) {
                        atacar();
                    } else if (action.equals("2")) {
                        defender();
                    } else if (action.equals("3")) {
                        verHistorico();
                        continue;  // Permite o jogador escolher outra ação
                    }

                    if (pontos[playerId - 1] >= PONTOS_PARA_VENCER) {
                        output.println("Você venceu o jogo!");
                        output.println("Finalizando conexão...");
                        jogoAtivo = false;
                        break;
                    }

                    currentPlayerTurn = (currentPlayerTurn % 2) + 1;

                    // Dragão ataca um jogador aleatório após cada turno
                    if (dragao.estaVivo()) {
                        atacarJogadorAleatorio();
                    }

                    playerTurnCondition.signalAll();

                    if (!jogoAtivo) {
                        output.println("O dragão foi derrotado! O jogo acabou.");
                        break;
                    }
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
        String log = "Jogador " + playerId + " atacou o Dragão e causou " + dano + " de dano. O Dragão tem " + dragao.getPontosDeVida() + " pontos de vida.";
        GameLog.addLog(log);
        output.println(log);

        if (!dragao.estaVivo()) {
            String vitoriaLog = "Jogador " + playerId + " derrotou o dragão!";
            GameLog.addLog(vitoriaLog);
            output.println(vitoriaLog);
            pontos[playerId - 1]++;
            jogoAtivo = false;
        }
    }

    private void defender() {
        personagens[playerId - 1].defender();
        String log = "Jogador " + playerId + " escolheu defender.";
        GameLog.addLog(log);
        output.println(log);
    }

    private void atacarJogadorAleatorio() {
        Random random = new Random();
        int alvo = random.nextInt(2);  // Escolhe aleatoriamente entre 0 e 1

        Personagem jogador = personagens[alvo];
        int dano = calcularDano(dragao.calcularAtaque(), jogador.calcularDefesa());
        jogador.setPontosDeVida(jogador.getPontosDeVida() - dano);
        String log = "O Dragão atacou " + jogador.getNome() + " e causou " + dano + " de dano. " + jogador.getNome() + " tem " + jogador.getPontosDeVida() + " pontos de vida.";
        GameLog.addLog(log);
        output.println(log);

        if (!jogador.estaVivo()) {
            String derrotaLog = jogador.getNome() + " foi derrotado pelo Dragão.";
            GameLog.addLog(derrotaLog);
            output.println(derrotaLog);
        }
    }

    private void verHistorico() {
        List<String> logs = GameLog.getLogs();
        output.println("Histórico de Batalhas:");
        for (String log : logs) {
            output.println(log);
        }
    }

    private int calcularDano(int ataque, int defesa) {
        return Math.max(ataque - defesa, 1); // Garante que o dano seja pelo menos 1
    }
}
