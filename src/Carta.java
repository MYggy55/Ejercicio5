public abstract class Carta implements Comparable<Carta> {
    private final int id;
    private final String nombre;
    private final int costoEnergia;
    private final String descripcion;
    private ZonaCarta zona;

    protected Carta(int id, String nombre, int costoEnergia, String descripcion) {
        if (id <= 0 || costoEnergia < 0) throw new IllegalArgumentException("ID positivo y costo no negativo requeridos.");
        this.id = id;
        this.nombre = validarTexto(nombre);
        this.costoEnergia = costoEnergia;
        this.descripcion = validarTexto(descripcion);
        this.zona = ZonaCarta.MAZO;
    }

    protected static String validarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) throw new IllegalArgumentException("El texto no puede estar vacío.");
        return texto.trim();
    }

    public abstract String jugarCarta(Jugador actual, Jugador rival);
    protected abstract String caracteristicas();

    final void moverA(ZonaCarta destino) {
        boolean robar = zona == ZonaCarta.MAZO && (destino == ZonaCarta.JUGADOR1 || destino == ZonaCarta.JUGADOR2);
        boolean jugar = (zona == ZonaCarta.JUGADOR1 || zona == ZonaCarta.JUGADOR2) && destino == ZonaCarta.DESCARTE;
        if (!robar && !jugar) throw new IllegalStateException("Movimiento de carta inválido.");
        zona = destino;
    }

    @Override
    public final int compareTo(Carta otra) {
        if (otra == null) throw new IllegalArgumentException("La carta no puede ser null.");
        int resultado = Integer.compare(costoEnergia, otra.costoEnergia);
        return resultado == 0 ? Integer.compare(id, otra.id) : resultado;
    }

    @Override
    public final String toString() {
        return "ID " + id + " | " + nombre + " | " + getClass().getSimpleName()
            + " | Energía: " + costoEnergia + " | Zona: " + zona
            + " | " + descripcion + " | " + caracteristicas();
    }

    public final int getId() { return id; }
    public final String getNombre() { return nombre; }
    public final int getCostoEnergia() { return costoEnergia; }
    public final String getDescripcion() { return descripcion; }
    public final ZonaCarta getZona() { return zona; }
}
