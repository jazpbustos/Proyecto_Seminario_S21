package entidad;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Rutinas {

    private int idRutina;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Ejercicio> ejercicios;
    private List<String> notasSemanales;
    private int semanas;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Rutinas(int idRutina, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        this.idRutina = idRutina;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.ejercicios = new ArrayList<>();
        this.semanas = calcularSemanas(fechaInicio, fechaFin);
        this.notasSemanales = new ArrayList<>();

        for (int i = 0; i < semanas; i++) {
            notasSemanales.add("");
        }
    }

    private static int calcularSemanas(LocalDate ini, LocalDate fin) {
        if (ini == null || fin == null || fin.isBefore(ini)) return 0;
        long dias = ChronoUnit.DAYS.between(ini, fin) + 1;
        return (int) Math.min(12, Math.max(1, (dias + 6) / 7));
    }

    public int getIdRutina() { return idRutina; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public List<Ejercicio> getEjercicios() { return ejercicios; }
    public int getSemanas() { return semanas; }
    public List<String> getNotasSemanales() { return notasSemanales; }

    public void setIdRutina(int idRutina) {
        this.idRutina = idRutina;
    }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public void setNotasSemanales(List<String> notas) {
        this.notasSemanales = notas;
    }

    public void agregarEjercicio(Ejercicio e) { ejercicios.add(e); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre).append(" (").append(semanas).append(" semanas)\n");
        sb.append("Ejercicios:\n");

        for (Ejercicio e : ejercicios) {
            sb.append(" • ").append(e.getDia())
                    .append(" - ").append(e.getNombre())
                    .append(" ").append(e.getSeries()).append("x").append(e.getReps()).append("\n");
        }

        sb.append("\nNotas semanales:\n");
        for (int i=0; i<notasSemanales.size(); i++) {
            sb.append("Semana ").append(i+1).append(": ").append(notasSemanales.get(i)).append("\n");
        }

        return sb.toString();
    }
}

