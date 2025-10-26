package entidad;

import java.time.LocalDate;
import java.time.Period;

public class Clientes {
    private int id;
    private String dni;
    private String nombre;
    private String apellido;
    private String celular;
    private String correo;
    private LocalDate fechaNacimiento;
    private int edad;

    private Actividades actividad;
    private Rutinas rutina;
    private Pagos pago;

    public Clientes(int id, String dni, String nombre, String apellido,
                    String celular, String correo, LocalDate fechaNacimiento,
                    Actividades actividad, Rutinas rutina, Pagos pago) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.celular = celular;
        this.correo = correo;
        this.fechaNacimiento = fechaNacimiento;
        this.edad = calcularEdad(fechaNacimiento);
        this.actividad = actividad;
        this.rutina = rutina;
        this.pago = pago;
    }

    private int calcularEdad(LocalDate fn) {
        if (fn == null) return 0;
        return Period.between(fn, LocalDate.now()).getYears();
    }

    public int getId() { return id; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCelular() { return celular; }
    public String getCorreo() { return correo; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public int getEdad() { return edad; }
    public Actividades getActividad() { return actividad; }
    public Rutinas getRutina() { return rutina; }
    public Pagos getPago() { return pago; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
        this.edad = calcularEdad(fechaNacimiento);
    }
    public void setActividad(Actividades actividad) { this.actividad = actividad; }
    public void setRutina(Rutinas rutina) { this.rutina = rutina; }
    public void setPago(Pagos pago) { this.pago = pago; }

    @Override
    public String toString() {
        return "Cliente[ID=" + id + ", DNI=" + dni + ", Nombre=" + nombre + " " + apellido
                + ", Edad=" + edad + ", Cel=" + celular + ", Correo=" + correo
                + ", Actividad=" + (actividad != null ? actividad.getNombre() : "No asignada")
                + ", Rutina=" + (rutina != null ? rutina.getNombre() : "No asignada")
                + ", Pago=" + (pago != null ? pago.getEstadoCuota() : "No registrado") + "]";
    }
}
