package control;

import entidad.Ejercicio;
import entidad.Rutinas;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.PrintWriter;

public class ControlRutinas {

    private List<Rutinas> rutinas;

    public ControlRutinas() {
        rutinas = new ArrayList<>();
        precargarRutinas();
    }

    private void precargarRutinas() {
        rutinas.add(new Rutinas(1, "Rutina Inicial", "Rutina básica para principiantes"));
        rutinas.add(new Rutinas(2, "Cardio Avanzado", "Ejercicios intensivos de cardio"));
        rutinas.add(new Rutinas(3, "Fuerza Total", "Entrenamiento completo de fuerza"));
    }

    public List<Rutinas> getRutinas() {
        return rutinas;
    }

    public void listarRutinas() {
        System.out.println("=== Lista de Rutinas ===");
        for (Rutinas r : rutinas) {
            System.out.println(r.getIdRutina() + ". " + r.getNombre() + " - " + r.getDescripcion());
        }
    }

    public Rutinas buscarRutinaPorId(int id) {
        for (Rutinas r : rutinas) {
            if (r.getIdRutina() == id) return r;
        }
        return null;
    }

    public boolean exportarRutinaACSV(Rutinas r, String rutaArchivo) {
        try {
            File file = new File(rutaArchivo);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter pw = new PrintWriter(file)) {
                pw.println("Ejercicio,Series,Reps");
                for (Ejercicio e : r.getEjercicios()) {
                    String nombre = e.getNombre().replace(",", ";");
                    pw.println(nombre + "," + e.getSeries() + "," + e.getReps());
                }
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
