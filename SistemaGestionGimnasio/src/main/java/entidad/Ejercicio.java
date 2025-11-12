package entidad;

import java.util.ArrayList;
import java.util.List;

public class Ejercicio {
    private String nombre;
    private int series;
    private int reps;
    private int dia; // NUEVO: día de la semana (1–7)
    private List<String> notasSemanales;

    public Ejercicio(String nombre, int series, int reps, int dia, int semanas) {
        this.nombre = nombre;
        this.series = series;
        this.reps = reps;
        this.dia = dia;
        this.notasSemanales = new ArrayList<>();
        for (int i = 0; i < semanas; i++) notasSemanales.add("");
    }

    public String getNombre() { return nombre; }
    public int getSeries() { return series; }
    public int getReps() { return reps; }
    public int getDia() { return dia; }
    public void setDia(int dia) { this.dia = dia; }
    public List<String> getNotasSemanales() { return notasSemanales; }

    public void setNotaSemana(int semanaIndex, String nota) {
        if (semanaIndex >= 0 && semanaIndex < notasSemanales.size()) {
            notasSemanales.set(semanaIndex, nota);
        }
    }

    @Override
    public String toString() {
        return "Día " + dia + ": " + nombre + " " + series + "x" + reps;
    }

    public void setNotasSemanales(ArrayList<Object> objects) {
    }

    public void setSemanas(int semanas) {
    }
}
