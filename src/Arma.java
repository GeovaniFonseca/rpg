public class Arma {
    private String nome;
    private int incrementoAtaque;
    private int incrementoDefesa;

    public Arma(String nome, int incrementoAtaque, int incrementoDefesa) {
        this.nome = nome;
        this.incrementoAtaque = incrementoAtaque;
        this.incrementoDefesa = incrementoDefesa;
    }

    public String getNome() {
        return nome;
    }

    public int getIncrementoAtaque() {
        return incrementoAtaque;
    }

    public int getIncrementoDefesa() {
        return incrementoDefesa;
    }
}

