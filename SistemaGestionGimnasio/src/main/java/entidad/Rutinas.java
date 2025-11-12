package entidad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Rutinas {
    private int idRutina;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Ejercicio> ejercicios;
    private int semanas;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Rutinas(int idRutina, String nombre, String descripcion) {
        this(idRutina, nombre, descripcion, null, null, 0);
    }

    public Rutinas(int idRutina, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin) {
        this(idRutina, nombre, descripcion, fechaInicio, fechaFin, calcularSemanas(fechaInicio, fechaFin));
    }

    public Rutinas(int idRutina, String nombre, String descripcion,
                   LocalDate fechaInicio, LocalDate fechaFin, int semanas) {
        this.idRutina = idRutina;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.semanas = semanas;
        this.ejercicios = new ArrayList<>();
    }

    private static int calcularSemanas(LocalDate ini, LocalDate fin) {
        if (ini == null || fin == null || fin.isBefore(ini)) return 0;
        long dias = ChronoUnit.DAYS.between(ini, fin) + 1;
        return (int) Math.min(8, Math.max(1, (dias + 6) / 7));
    }

    public int getIdRutina() { return idRutina; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public int getSemanas() { return semanas; }
    public List<Ejercicio> getEjercicios() { return ejercicios; }

    public void setIdRutina(int idRutina) { this.idRutina = idRutina; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public void agregarEjercicio(Ejercicio e) { ejercicios.add(e); }

    @Override
    public String toString() {
        String dur = (fechaInicio != null && fechaFin != null) ?
                calcularDuracionDias() + " días (" + semanas + " sem.)" : "Sin fechas";
        StringBuilder sb = new StringBuilder();
        sb.append("Rutina[ID=").append(idRutina)
                .append(", Nombre=").append(nombre)
                .append(", Descripción=").append(descripcion)
                .append(", ").append(dur)
                .append("]\nEjercicios:\nEjercicio | Series | Reps\n");
        for (Ejercicio e : ejercicios) {
            sb.append(e.getNombre()).append(" | ").append(e.getSeries()).append(" | ").append(e.getReps()).append("\n");
            for (int i = 0; i < e.getNotasSemanales().size(); i++) {
                String nota = e.getNotasSemanales().get(i);
                if (!nota.isEmpty()) sb.append("  Sem").append(i + 1).append(": ").append(nota).append("\n");
            }
        }
        return sb.toString();
    }

    public long calcularDuracionDias() {
        if (fechaInicio == null || fechaFin == null) return 0;
        return ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }
}
