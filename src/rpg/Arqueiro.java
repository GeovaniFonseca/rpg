package rpg;

public class Arqueiro extends Personagem {
    private Arma arma;

    public Arqueiro(String nome, Arma arma) {
        super(nome, 20, 10, 200);
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
