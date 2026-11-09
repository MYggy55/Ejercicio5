public final class CartaCatedratico extends Carta {
    private final String departamento;
    private final int llamadasAtencion;
    private final int tiempoAtencion;

    public CartaCatedratico(int id, String nombre, int costo, String descripcion, String departamento, int llamadasAtencion, int tiempoAtencion) {
        super(id, nombre, costo, descripcion);
        this.departamento = validarTexto(departamento);
        if (llamadasAtencion < 0 || tiempoAtencion < 0) throw new IllegalArgumentException("Los valores de atención no pueden ser negativos.");
        this.llamadasAtencion = llamadasAtencion;
        this.tiempoAtencion = tiempoAtencion;
    }

    @Override
    public String jugarCarta(Jugador actual, Jugador rival) {
        rival.cambiarEnergia(-llamadasAtencion);
        actual.cambiarEnergia(tiempoAtencion);
        actual.cambiarPuntos(1);
        return "Catedrático: rival pierde hasta " + llamadasAtencion + " de energía; recuperas hasta "
            + tiempoAtencion + " de energía y ganas 1 punto.";
    }

    @Override
    protected String caracteristicas() {
        return "Departamento: " + departamento + " | Llamadas: " + llamadasAtencion + " | Tiempo: " + tiempoAtencion;
    }
}
