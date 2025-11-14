package entidad;

public class Ejercicio {

    private String nombre;
    private int series;
    private int reps;
    private int dia; // 1–7

    public Ejercicio(String nombre, int series, int reps, int dia) {
        this.nombre = nombre;
        this.series = series;
        this.reps = reps;
        this.dia = dia;
    }

    public String getNombre() { return nombre; }
    public int getSeries() { return series; }
    public int getReps() { return reps; }
    public int getDia() { return dia; }
    public void setDia(int dia) { this.dia = dia; }

    @Override
    public String toString() {
        return "Día " + dia + ": " + nombre + " " + series + "x" + reps;
    }
}
