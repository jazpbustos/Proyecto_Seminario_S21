package control;

import entidad.Actividades;
import java.util.ArrayList;
import java.util.List;

public class ControlActividades {
    private List<Actividades> actividades;

    public ControlActividades() {
        actividades = new ArrayList<>();
        precargarActividades();
    }

    private void precargarActividades() {
        actividades.add(new Actividades(1, "Musculación", 5000.0, 30));
        actividades.add(new Actividades(2, "Musculación + Aparatos (Combo)", 6500.0, 30));
        actividades.add(new Actividades(3, "Yoga", 3500.0, 15));
        actividades.add(new Actividades(4, "Pilates", 4000.0, 15));
        actividades.add(new Actividades(5, "Zumba", 3000.0, 15));
    }

    public List<Actividades> getActividades() { return actividades; }

    public Actividades buscarActividadPorId(int id) {
        for (Actividades a : actividades)
            if (a.getIdActividad() == id)
                return a;
        return null;
    }

    public void listarActividades() {
        System.out.println("=== Actividades ===");
        for (Actividades a : actividades)
            System.out.println(a);
    }

    public void agregarActividad(Actividades a) {
        actividades.add(a);
        System.out.println("✅ Actividad agregada: " + a.getNombre());
    }
}
