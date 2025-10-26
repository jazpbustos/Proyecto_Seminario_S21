package entidad;

public class Actividades {
    private int idActividad;
    private String nombre;
    private double precio;
    private int duracion; // duración en días

    public Actividades(int idActividad, String nombre, double precio, int duracion) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.precio = precio;
        this.duracion = duracion;
    }

    public int getIdActividad() { return idActividad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    @Override
    public String toString() {
        return idActividad + " - " + nombre + " ($" + precio + ", " + duracion + " días)";
    }
}
