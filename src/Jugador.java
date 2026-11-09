public final class Jugador {
    public static final int ENERGIA_MAXIMA = 20;
    private final String nombre;
    private int energia;
    private int puntos;

    public Jugador(String nombre) {
        this.nombre = Carta.validarTexto(nombre);
        this.energia = 5;
    }

    void pagar(int costo) {
        if (costo < 0 || costo > energia) throw new IllegalArgumentException("Energía insuficiente o costo inválido.");
        energia -= costo;
    }

    void cambiarEnergia(int cambio) {
        energia = (int) Math.max(0L, Math.min(ENERGIA_MAXIMA, (long) energia + cambio));
    }

    void cambiarPuntos(int cambio) {
        puntos = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, (long) puntos + cambio));
    }

    public String getNombre() { return nombre; }
    public int getEnergia() { return energia; }
    public int getPuntos() { return puntos; }

    @Override
    public String toString() { return nombre + " | Energía: " + energia + " | Puntos: " + puntos; }
}
