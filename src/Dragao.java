public class Dragao extends Personagem {
    public Dragao() {
        super("Dragão", 30, 30, 300);
    }

    @Override
    public int calcularAtaque() {
        return ataque;
    }

    @Override
    public int calcularDefesa() {
        return defesa;
    }
}


