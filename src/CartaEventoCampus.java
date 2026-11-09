public final class CartaEventoCampus extends Carta {
    private final int cambioEnergia;
    private final int cambioPuntos;

    public CartaEventoCampus(int id, String nombre, int costo, String descripcion, int cambioEnergia, int cambioPuntos) {
        super(id, nombre, costo, descripcion);
        this.cambioEnergia = cambioEnergia;
        this.cambioPuntos = cambioPuntos;
    }

    @Override
    public String jugarCarta(Jugador actual, Jugador rival) {
        actual.cambiarEnergia(cambioEnergia);
        rival.cambiarEnergia(cambioEnergia);
        actual.cambiarPuntos(cambioPuntos);
        rival.cambiarPuntos(cambioPuntos);
        return "Evento global: ambos reciben cambio de energía " + cambioEnergia + " y de puntos " + cambioPuntos + ".";
    }

    @Override
    protected String caracteristicas() { return "Cambio de energía: " + cambioEnergia + " | Cambio de puntos: " + cambioPuntos; }
}
