public final class Controlador {
    private final Partida partida;

    public Controlador(Partida partida) {
        if (partida == null) throw new IllegalArgumentException("Partida inválida.");
        this.partida = partida;
    }

    public String listarCartas() { return listarCartas(null); }

    public String listarCartas(ZonaCarta zona) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < partida.getCantidadCartas(); i++) {
            Carta carta = partida.getCartaEn(i);
            if (zona == null || carta.getZona() == zona) texto.append(carta).append("\n");
        }
        return texto.length() == 0 ? "No hay cartas en esta zona." : texto.toString();
    }

    public String buscar(int id) { return partida.buscar(id).toString(); }
    public String buscar(String nombre) { return partida.buscar(nombre).toString(); }

    public String ordenar() {
        partida.ordenarPorEnergia();
        return "Catálogo ordenado por energía ascendente:\n" + listarCartas();
    }

    public String iniciar(String nombre1, String nombre2) {
        partida.iniciar(nombre1, nombre2);
        return "Partida iniciada. Tres cartas por jugador.\n" + estado();
    }

    public String manoActual() {
        if (!partida.isIniciada()) throw new IllegalStateException("Debes iniciar una partida.");
        return listarCartas(partida.getActual() == 0 ? ZonaCarta.JUGADOR1 : ZonaCarta.JUGADOR2);
    }

    public String jugar(int id) { return partida.jugarCarta(id) + "\n" + estado(); }
    public String tomar() { return partida.tomarCarta() + "\n" + estado(); }
    public String pasar() { return partida.pasarTurno() + "\n" + estado(); }

    public String estado() {
        if (!partida.isIniciada()) return "Todavía no hay una partida iniciada.";
        String texto = partida.getJugador(0) + "\n" + partida.getJugador(1)
            + "\nAcciones realizadas: " + partida.getTurnosJugados() + "/" + Partida.MAX_TURNOS
            + " | Mazo: " + partida.contarZona(ZonaCarta.MAZO)
            + " | Descarte: " + partida.contarZona(ZonaCarta.DESCARTE);
        return texto + (partida.isFinalizada() ? "\nFIN DE PARTIDA. " + partida.resultado()
            : "\nTurno de: " + partida.getJugador(partida.getActual()).getNombre());
    }
}
