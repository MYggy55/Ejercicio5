import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Partida partida = new Partida();
        Catalogo.cargar(partida, new Random());
        Controlador controlador = new Controlador(partida);
        try (Scanner entrada = new Scanner(System.in)) {
            System.out.println("UVG CARD BATTLE | HERENCIA Y MVC");
            System.out.println("18 cartas aleatorias. Dos jugadores; 12 turnos en total.");
            System.out.println("Una acción por turno: jugar, tomar una carta o pasar.");
            boolean continuar = true;
            while (continuar) {
                mostrarMenu();
                int opcion = leerEntero(entrada, "Opción: ");
                try {
                    switch (opcion) {
                        case 1 -> System.out.println(controlador.listarCartas());
                        case 2 -> System.out.println(controlador.buscar(leerEntero(entrada, "ID: ")));
                        case 3 -> System.out.println(controlador.buscar(leerTexto(entrada, "Nombre exacto: ")));
                        case 4 -> System.out.println(controlador.ordenar());
                        case 5 -> {
                            String nombre1 = leerTexto(entrada, "Jugador 1: ");
                            String nombre2 = leerTexto(entrada, "Jugador 2: ");
                            System.out.println(controlador.iniciar(nombre1, nombre2));
                        }
                        case 6 -> System.out.println(controlador.manoActual());
                        case 7 -> System.out.println(controlador.jugar(leerEntero(entrada, "ID de tu carta: ")));
                        case 8 -> System.out.println(controlador.tomar());
                        case 9 -> System.out.println(controlador.pasar());
                        case 10 -> System.out.println(controlador.estado());
                        case 11 -> continuar = false;
                        default -> System.out.println("Selecciona una opción entre 1 y 11.");
                    }
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Acción rechazada: " + e.getMessage());
                }
            }
            System.out.println("Programa finalizado.");
        } catch (NoSuchElementException e) {
            System.out.println("Entrada finalizada. Programa cerrado.");
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n1. Listar catálogo completo\n2. Buscar por ID\n3. Buscar por nombre"
            + "\n4. Ordenar por energía\n5. Iniciar partida\n6. Ver mano actual\n7. Jugar carta"
            + "\n8. Tomar carta\n9. Pasar turno\n10. Ver tablero y resultado\n11. Salir");
    }

    private static String leerTexto(Scanner entrada, String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private static int leerEntero(Scanner entrada, String mensaje) {
        Integer valor = null;
        while (valor == null) {
            try {
                valor = Integer.valueOf(leerTexto(entrada, mensaje));
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: escribe un entero.");
            }
        }
        return valor;
    }
}
