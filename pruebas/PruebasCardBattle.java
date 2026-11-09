import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class PruebasCardBattle {
    private static final StringBuilder evidencia = new StringBuilder();
    private static int cantidad;

    private static void verificar(String caso, Object esperado, Object obtenido) {
        if (!esperado.equals(obtenido)) throw new AssertionError(caso + ": " + esperado + " != " + obtenido);
        cantidad++;
        evidencia.append("OK | ").append(caso).append(" | esperado=").append(esperado)
            .append(" | obtenido=").append(obtenido).append("\n");
    }

    private static void rechazar(String caso, Class<? extends RuntimeException> tipo, Runnable accion) {
        String obtenido = "Sin excepción";
        try { accion.run(); } catch (RuntimeException e) { obtenido = e.getClass().getSimpleName(); }
        verificar(caso, tipo.getSimpleName(), obtenido);
    }

    private static Partida preparar(int cartas, int costo) {
        Partida partida = new Partida(new Random(7));
        for (int id = 1; id <= cartas; id++) {
            partida.agregar(new CartaCurso(id,"Curso " + id,costo,"Prueba",4,2));
        }
        return partida;
    }

    private static Carta encontrar(Partida partida, ZonaCarta zona) {
        Carta encontrada = null;
        for (int i = 0; i < partida.getCantidadCartas(); i++) {
            if (partida.getCartaEn(i).getZona() == zona) encontrada = partida.getCartaEn(i);
        }
        if (encontrada == null) throw new AssertionError("No se encontró la carta de prueba.");
        return encontrada;
    }

    public static void main(String[] args) throws Exception {
        Partida catalogo = new Partida(new Random(2));
        Catalogo.cargar(catalogo,new Random(42));
        verificar("Carga inicial aleatoria",18,catalogo.getCantidadCartas());
        int profesores=0,cursos=0,eventos=0;
        for (int i=0;i<catalogo.getCantidadCartas();i++) {
            Carta carta=catalogo.getCartaEn(i);
            if (carta instanceof CartaCatedratico) profesores++;
            if (carta instanceof CartaCurso) cursos++;
            if (carta instanceof CartaEventoCampus) eventos++;
        }
        verificar("Catedráticos iniciales",6,profesores);
        verificar("Cursos iniciales",6,cursos);
        verificar("Eventos iniciales",6,eventos);
        verificar("Todas comienzan en mazo",18,catalogo.contarZona(ZonaCarta.MAZO));
        verificar("Búsqueda por ID",1,catalogo.buscar(1).getId());
        verificar("Búsqueda por nombre",1,catalogo.buscar(catalogo.buscar(1).getNombre().toUpperCase()).getId());
        catalogo.ordenarPorEnergia();
        boolean ordenado=true;
        for (int i=1;i<catalogo.getCantidadCartas();i++) {
            if (catalogo.getCartaEn(i-1).compareTo(catalogo.getCartaEn(i))>0) ordenado=false;
        }
        verificar("Orden ascendente por costo e ID",true,ordenado);
        rechazar("No jugar sin iniciar",IllegalStateException.class,() -> catalogo.jugarCarta(1));
        rechazar("No pasar sin iniciar",IllegalStateException.class,() -> catalogo.pasarTurno());
        rechazar("No robar sin iniciar",IllegalStateException.class,() -> catalogo.tomarCarta());
        catalogo.iniciar("Ana","Luis");
        verificar("Mano inicial 1",3,catalogo.contarZona(ZonaCarta.JUGADOR1));
        verificar("Mano inicial 2",3,catalogo.contarZona(ZonaCarta.JUGADOR2));
        verificar("Mazo tras reparto",12,catalogo.contarZona(ZonaCarta.MAZO));
        verificar("Energía inicial",5,catalogo.getJugador(0).getEnergia());
        rechazar("Reinicio rechazado",IllegalStateException.class,() -> catalogo.iniciar("A","B"));
        Carta ajena=encontrar(catalogo,ZonaCarta.JUGADOR2);
        rechazar("Carta de mano rival",IllegalStateException.class,() -> catalogo.jugarCarta(ajena));
        verificar("Rechazo conserva turno",0,catalogo.getTurnosJugados());
        verificar("Rechazo conserva energía",5,catalogo.getJugador(0).getEnergia());
        catalogo.tomarCarta();
        verificar("Tomar agrega a mano actual",4,catalogo.contarZona(ZonaCarta.JUGADOR1));
        verificar("Tomar reduce mazo",11,catalogo.contarZona(ZonaCarta.MAZO));
        verificar("Tomar termina turno",1,catalogo.getActual());
        verificar("Primer turno del segundo sin ventaja extra",5,catalogo.getJugador(1).getEnergia());
        catalogo.pasarTurno();
        verificar("Pasar termina turno",0,catalogo.getActual());
        verificar("Recuperación en segundo turno propio",7,catalogo.getJugador(0).getEnergia());
        Partida insuficiente=preparar(6,20);
        insuficiente.iniciar("A","B");
        Carta cara=encontrar(insuficiente,ZonaCarta.JUGADOR1);
        rechazar("Energía insuficiente",IllegalArgumentException.class,() -> insuficiente.jugarCarta(cara.getId()));
        verificar("Insuficiente conserva energía",5,insuficiente.getJugador(0).getEnergia());
        verificar("Insuficiente conserva mano",ZonaCarta.JUGADOR1,cara.getZona());
        verificar("Insuficiente conserva turno",0,insuficiente.getTurnosJugados());
        rechazar("Mazo vacío",IllegalStateException.class,() -> insuficiente.tomarCarta());
        verificar("Robo fallido conserva turno",0,insuficiente.getTurnosJugados());
        Partida partida=preparar(12,2);
        partida.iniciar("Ana","Luis");
        Carta curso=encontrar(partida,ZonaCarta.JUGADOR1);
        partida.jugarCarta(curso);
        verificar("Pago y recuperación de curso",5,partida.getJugador(0).getEnergia());
        verificar("Curso otorga créditos como puntos",4,partida.getJugador(0).getPuntos());
        verificar("Carta pasa a descarte",ZonaCarta.DESCARTE,curso.getZona());
        verificar("Jugar termina turno",1,partida.getActual());
        rechazar("No reutilizar carta",IllegalStateException.class,() -> partida.jugarCarta(curso));
        verificar("Reuso fallido conserva turnos",1,partida.getTurnosJugados());
        while (!partida.isFinalizada()) partida.pasarTurno();
        verificar("Partida finaliza a doce acciones",12,partida.getTurnosJugados());
        verificar("Ganador por puntuación","Ganador: Ana",partida.resultado());
        rechazar("No pasar después del final",IllegalStateException.class,() -> partida.pasarTurno());
        rechazar("No robar después del final",IllegalStateException.class,() -> partida.tomarCarta());
        Partida empate=preparar(6,1);empate.iniciar("A","B");
        while (!empate.isFinalizada()) empate.pasarTurno();
        verificar("Resultado empatado","Empate.",empate.resultado());
        Jugador a=new Jugador("A");Jugador b=new Jugador("B");
        Carta profesor=new CartaCatedratico(100,"Docente",1,"D","Computación",3,2);
        profesor.jugarCarta(a,b);
        verificar("Catedrático recupera energía",7,a.getEnergia());
        verificar("Catedrático resta energía rival",2,b.getEnergia());
        verificar("Catedrático otorga un punto",1,a.getPuntos());
        Carta evento=new CartaEventoCampus(101,"Parciales",1,"D",-3,-1);
        evento.jugarCarta(a,b);
        verificar("Evento afecta energía propia",4,a.getEnergia());
        verificar("Energía mínima cero",0,b.getEnergia());
        verificar("Evento afecta puntos",0,a.getPuntos());
        Carta feria=new CartaEventoCampus(102,"Feria",1,"D",3,1);feria.jugarCarta(a,b);
        verificar("Evento positivo en rival",3,b.getEnergia());
        verificar("Evento positivo da puntos",1,b.getPuntos());
        a.cambiarEnergia(Integer.MAX_VALUE);verificar("Tope de energía",20,a.getEnergia());
        a.cambiarPuntos(Integer.MIN_VALUE);verificar("Puntos no negativos",0,a.getPuntos());
        Partida valida=preparar(6,1);
        rechazar("ID repetido",IllegalArgumentException.class,() -> valida.agregar(new CartaCurso(1,"Otro",1,"D",1,1)));
        verificar("Rechazo conserva catálogo",6,valida.getCantidadCartas());
        rechazar("Nombre repetido",IllegalArgumentException.class,() -> valida.agregar(new CartaCurso(99,"CURSO 1",1,"D",1,1)));
        rechazar("Nombre vacío",IllegalArgumentException.class,() -> new Jugador(" "));
        rechazar("Costo negativo",IllegalArgumentException.class,() -> new CartaCurso(99,"X",-1,"D",1,1));
        rechazar("Créditos cero",IllegalArgumentException.class,() -> new CartaCurso(99,"X",1,"D",0,1));
        rechazar("ID inexistente",IllegalArgumentException.class,() -> valida.buscar(999));
        rechazar("Nombres iguales",IllegalArgumentException.class,() -> valida.iniciar("Ana","ANA"));
        verificar("Inicio fallido conserva zonas",6,valida.contarZona(ZonaCarta.MAZO));
        Controlador controlador=new Controlador(valida);
        verificar("Sobrecarga de listado",true,controlador.listarCartas(ZonaCarta.DESCARTE).contains("No hay cartas"));
        verificar("Controlador busca por nombre",true,controlador.buscar("Curso 1").contains("ID 1"));
        evidencia.append("Total de verificaciones: ").append(cantidad).append("\nTodas las pruebas pasaron.\n");
        Files.writeString(Path.of(args.length>0?args[0]:"Resultados_pruebas.txt"),evidencia,StandardCharsets.UTF_8);
    }
}
