package rpg;

public abstract class Personagem {
    protected String nome;
    protected int ataque;
    protected int defesa;
    protected int pontosDeVida;
    protected boolean defendendo;

    public Personagem(String nome, int ataque, int defesa, int pontosDeVida) {
        this.nome = nome;
        this.ataque = ataque;
        this.defesa = defesa;
        this.pontosDeVida = pontosDeVida;
        this.defendendo = false;
    }

    public String getNome() {
        return nome;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefesa() {
        return defesa;
    }

    public int getPontosDeVida() {
        return pontosDeVida;
    }

    public void setPontosDeVida(int pontosDeVida) {
        this.pontosDeVida = pontosDeVida;
    }

    public boolean estaVivo() {
        return pontosDeVida > 0;
    }

    public abstract int calcularAtaque();

    public abstract int calcularDefesa();

    public void defender() {
        this.defendendo = true;
    }

    public void pararDefesa() {
        this.defendendo = false;
    }
}



