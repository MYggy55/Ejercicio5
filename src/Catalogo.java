import java.util.Random;

public final class Catalogo {
    private Catalogo() { }

    public static void cargar(Partida partida, Random azar) {
        if (partida == null || azar == null) throw new IllegalArgumentException("Datos de carga inválidos.");
        String[] departamentos = {"Computación", "Matemática", "Física"};
        String[] cursos = {"POO", "Cálculo", "Algoritmos", "Estadística"};
        for (int id = 1; id <= 18; id++) {
            int costo = 1 + azar.nextInt(4);
            if (id % 3 == 1) {
                String departamento = departamentos[azar.nextInt(departamentos.length)];
                partida.agregar(new CartaCatedratico(id, "Catedrático " + departamento + " " + id, costo,
                    "Asesoría y llamadas de atención.", departamento, 1 + azar.nextInt(3), 1 + azar.nextInt(3)));
            } else if (id % 3 == 2) {
                String curso = cursos[azar.nextInt(cursos.length)];
                partida.agregar(new CartaCurso(id, curso + " " + id, costo,
                    "Completa el curso y obtiene créditos.", 2 + azar.nextInt(4), 1 + azar.nextInt(5)));
            } else {
                boolean feria = azar.nextBoolean();
                partida.agregar(new CartaEventoCampus(id, (feria ? "Feria de Clubes " : "Semana de Parciales ") + id,
                    costo, "El evento afecta a ambos jugadores.", feria ? 3 : -2, feria ? 1 : -1));
            }
        }
    }
}
