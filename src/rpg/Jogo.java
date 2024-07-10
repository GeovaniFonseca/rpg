package rpg;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Jogo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Personagem> personagens = new ArrayList<>();
        Dragao dragao = new Dragao();

        System.out.println("Bem-vindo ao Heroes of OOP!");
        for (int i = 0; i < 3; i++) {
            System.out.println("Escolha seu personagem (ou digite 0 para parar): 1. Guerreiro 2. Mago 3. Arqueiro");
            int escolhaPersonagem = scanner.nextInt();
            if (escolhaPersonagem == 0) {
                break;

            }

            System.out.println("Escolha sua arma:");
            Arma armaEscolhida = null;

            if (escolhaPersonagem == 1) {
                System.out.println("1. Espada (+10 ATQ, +15 DEF) 2. Machado (+17 ATQ, +9 DEF)");
                int escolhaArma = scanner.nextInt();
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Espada", 10, 15);
                } else if (escolhaArma == 2) {
                    armaEscolhida = new Arma("Machado", 17, 9);
                }
                personagens.add(new Guerreiro("Guerreiro", armaEscolhida));
            } else if (escolhaPersonagem == 2) {
                System.out.println("1. Varinha (+8 ATQ, +9 DEF) 2. Cajado (+13 ATQ, +12 DEF)");
                int escolhaArma = scanner.nextInt();
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Varinha", 8, 9);
                } else if (escolhaArma == 2) {
                    armaEscolhida = new Arma("Cajado", 13, 12);
                }
                personagens.add(new Mago("Mago", armaEscolhida));
            } else if (escolhaPersonagem == 3) {
                System.out.println("1. Arco Longo (+12 ATQ, +13 DEF) 2. Balestra (+15 ATQ, +10 DEF)");
                int escolhaArma = scanner.nextInt();
                if (escolhaArma == 1) {
                    armaEscolhida = new Arma("Arco Longo", 12, 13);
                } else if (escolhaArma == 2) {
                    armaEscolhida = new Arma("Balestra", 15, 10);
                }
                personagens.add(new Arqueiro("Arqueiro", armaEscolhida));
            }
        }

        System.out.println("Personagens escolhidos. Vamos começar a batalha!");
        iniciarBatalha(personagens, dragao);

        scanner.close();
    }

    public static void iniciarBatalha(List<Personagem> personagens, Dragao dragao) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        boolean dragaoVivo = true;

        while (dragaoVivo && personagens.stream().anyMatch(Personagem::estaVivo)) {
            for (Personagem personagem : personagens) {
                if (personagem.estaVivo()) {
                    System.out.println(personagem.getNome() + ": Escolha sua ação: 1. Atacar 2. Defender");
                    int escolhaAcao = scanner.nextInt();
                    if (escolhaAcao == 1) {
                        personagem.pararDefesa();
                        int danoPersonagem = calcularDano(personagem.calcularAtaque(), dragao.getDefesa());
                        dragao.setPontosDeVida(dragao.getPontosDeVida() - danoPersonagem);
                        System.out.println(personagem.getNome() + " ataca o Dragão e causa " + danoPersonagem + " de dano. Dragão agora tem " + dragao.getPontosDeVida() + " pontos de vida.");
                    } else if (escolhaAcao == 2) {
                        personagem.defender();
                        System.out.println(personagem.getNome() + " escolheu defender.");
                    }

                    if (!dragao.estaVivo()) {
                        dragaoVivo = false;
                        System.out.println("O Dragão foi derrotado!");
                        break;
                    }
                }
            }

            if (dragaoVivo) {
                List<Personagem> personagensVivos = new ArrayList<>();
                for (Personagem personagem : personagens) {
                    if (personagem.estaVivo()) {
                        personagensVivos.add(personagem);
                    }
                }

                if (!personagensVivos.isEmpty()) {
                    Personagem alvo = personagensVivos.get(random.nextInt(personagensVivos.size()));
                    int danoDragao = calcularDano(dragao.calcularAtaque(), alvo.calcularDefesa());
                    alvo.setPontosDeVida(alvo.getPontosDeVida() - danoDragao);
                    System.out.println("Dragão ataca " + alvo.getNome() + " e causa " + danoDragao + " de dano. " + alvo.getNome() + " agora tem " + alvo.getPontosDeVida() + " pontos de vida.");

                    if (!alvo.estaVivo()) {
                        System.out.println(alvo.getNome() + " foi derrotado!");
                    }
                }
            }
        }

        if (!dragao.estaVivo()) {
            System.out.println("Parabéns! Você derrotou o Dragão!");
        } else {
            System.out.println("Todos os seus personagens foram derrotados. O Dragão venceu.");
        }
        scanner.close();
    }

    public static int calcularDano(int ataque, int defesa) {
        return Math.max(ataque - defesa, 1); // Garante que o dano seja pelo menos 1
    }
}
