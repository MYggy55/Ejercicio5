import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Partida {
    public static final int MAX_TURNOS = 12;
    private final ArrayList<Carta> cartas;
    private final Jugador[] jugadores;
    private final Random azar;
    private int actual;
    private int turnosJugados;
    private boolean iniciada;

    public Partida() { this(new Random()); }

    public Partida(Random azar) {
        if (azar == null) throw new IllegalArgumentException("El generador no puede ser null.");
        this.azar = azar;
        cartas = new ArrayList<>();
        jugadores = new Jugador[2];
    }

    public void agregar(Carta carta) {
        if (iniciada) throw new IllegalStateException("El catálogo no se modifica después de iniciar.");
        if (carta == null || carta.getZona() != ZonaCarta.MAZO) throw new IllegalArgumentException("Carta inválida.");
        for (Carta existente : cartas) {
            if (existente.getId() == carta.getId()) throw new IllegalArgumentException("ID repetido.");
            if (existente.getNombre().equalsIgnoreCase(carta.getNombre())) throw new IllegalArgumentException("Nombre repetido.");
        }
        cartas.add(carta);
    }

    public Carta buscar(int id) {
        for (Carta carta : cartas) if (carta.getId() == id) return carta;
        throw new IllegalArgumentException("No existe una carta con ese ID.");
    }

    public Carta buscar(String nombre) {
        String buscado = Carta.validarTexto(nombre);
        for (Carta carta : cartas) if (carta.getNombre().equalsIgnoreCase(buscado)) return carta;
        throw new IllegalArgumentException("No existe una carta con ese nombre.");
    }

    public void ordenarPorEnergia() { Collections.sort(cartas); }
    public int getCantidadCartas() { return cartas.size(); }
    public Carta getCartaEn(int indice) { return cartas.get(indice); }

    public void iniciar(String nombre1, String nombre2) {
        if (iniciada) throw new IllegalStateException("La partida ya comenzó.");
        Jugador primero = new Jugador(nombre1);
        Jugador segundo = new Jugador(nombre2);
        if (primero.getNombre().equalsIgnoreCase(segundo.getNombre())) throw new IllegalArgumentException("Usa nombres distintos.");
        if (cartas.size() < 6) throw new IllegalStateException("Se requieren al menos seis cartas.");
        jugadores[0] = primero;
        jugadores[1] = segundo;
        for (int i = 0; i < 3; i++) {
            extraerCarta(0);
            extraerCarta(1);
        }
        iniciada = true;
    }

    private Carta extraerCarta(int jugador) {
        int disponibles = contarZona(ZonaCarta.MAZO);
        if (disponibles == 0) throw new IllegalStateException("El mazo está vacío; puedes jugar o pasar.");
        int objetivo = azar.nextInt(disponibles);
        Carta elegida = null;
        int indice = 0;
        for (Carta carta : cartas) {
            if (carta.getZona() == ZonaCarta.MAZO) {
                if (indice == objetivo) elegida = carta;
                indice++;
            }
        }
        elegida.moverA(jugador == 0 ? ZonaCarta.JUGADOR1 : ZonaCarta.JUGADOR2);
        return elegida;
    }

    private void exigirTurno() {
        if (!iniciada) throw new IllegalStateException("Debes iniciar una partida.");
        if (isFinalizada()) throw new IllegalStateException("La partida ya terminó.");
    }

    public String jugarCarta(int id) { return jugarCarta(buscar(id)); }

    public String jugarCarta(Carta carta) {
        exigirTurno();
        if (carta == null || buscar(carta.getId()) != carta) throw new IllegalArgumentException("La carta no pertenece al catálogo.");
        ZonaCarta mano = actual == 0 ? ZonaCarta.JUGADOR1 : ZonaCarta.JUGADOR2;
        if (carta.getZona() != mano) throw new IllegalStateException("La carta no está en tu mano.");
        Jugador jugador = jugadores[actual];
        jugador.pagar(carta.getCostoEnergia());
        String efecto = carta.jugarCarta(jugador, jugadores[1 - actual]);
        carta.moverA(ZonaCarta.DESCARTE);
        terminarTurno();
        return efecto;
    }

    public String tomarCarta() {
        exigirTurno();
        Carta carta = extraerCarta(actual);
        terminarTurno();
        return "Carta tomada: " + carta.getNombre() + " (ID " + carta.getId() + ").";
    }

    public String pasarTurno() {
        exigirTurno();
        terminarTurno();
        return "Turno pasado.";
    }

    private void terminarTurno() {
        turnosJugados++;
        if (!isFinalizada()) {
            actual = 1 - actual;
            if (turnosJugados >= 2) jugadores[actual].cambiarEnergia(2);
        }
    }

    public int contarZona(ZonaCarta zona) {
        int total = 0;
        for (Carta carta : cartas) if (carta.getZona() == zona) total++;
        return total;
    }

    public Jugador getJugador(int indice) {
        if (!iniciada) throw new IllegalStateException("La partida no ha iniciado.");
        if (indice < 0 || indice > 1) throw new IllegalArgumentException("Jugador inexistente.");
        return jugadores[indice];
    }

    public int getActual() { return actual; }
    public int getTurnosJugados() { return turnosJugados; }
    public boolean isIniciada() { return iniciada; }
    public boolean isFinalizada() { return iniciada && turnosJugados == MAX_TURNOS; }

    public String resultado() {
        if (!isFinalizada()) throw new IllegalStateException("Todavía hay turnos pendientes.");
        int puntos1 = jugadores[0].getPuntos();
        int puntos2 = jugadores[1].getPuntos();
        return puntos1 == puntos2 ? "Empate." : "Ganador: " + jugadores[puntos1 > puntos2 ? 0 : 1].getNombre();
    }
}
