package rpg;

public class Guerreiro extends Personagem {
    private Arma arma;

    public Guerreiro(String nome, Arma arma) {
        super(nome, 30, 20, 180);
        this.arma = arma;
    }

    @Override
    public int calcularAtaque() {
        return ataque + arma.getIncrementoAtaque();
    }

    @Override
    public int calcularDefesa() {
        int defesaTotal = defesa + arma.getIncrementoDefesa();
        if (defendendo) {
            defesaTotal += 10;  // Aumenta a defesa temporariamente quando está defendendo
        }
        return defesaTotal;
    }
}

