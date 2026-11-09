public final class CartaCurso extends Carta {
    private final int creditos;
    private final int dificultad;

    public CartaCurso(int id, String nombre, int costo, String descripcion, int creditos, int dificultad) {
        super(id, nombre, costo, descripcion);
        if (creditos <= 0 || dificultad <= 0) throw new IllegalArgumentException("Créditos y dificultad deben ser positivos.");
        this.creditos = creditos;
        this.dificultad = dificultad;
    }

    @Override
    public String jugarCarta(Jugador actual, Jugador rival) {
        int recuperacion = Math.max(0, creditos - dificultad);
        actual.cambiarPuntos(creditos);
        actual.cambiarEnergia(recuperacion);
        return "Curso aprobado: ganas " + creditos + " puntos y recuperas hasta " + recuperacion + " de energía.";
    }

    @Override
    protected String caracteristicas() { return "Créditos: " + creditos + " | Dificultad: " + dificultad; }
}
