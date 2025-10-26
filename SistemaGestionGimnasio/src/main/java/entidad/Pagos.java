package entidad;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Representa un pago ligado a un cliente.
 */
public class Pagos {
    private int idPago;
    private LocalDate fecha;
    private double monto;
    private String estadoCuota;
    private String nombreCliente;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Pagos(int idPago, LocalDate fecha, double monto, String estadoCuota, String nombreCliente) {
        this.idPago = idPago;
        this.fecha = fecha;
        this.monto = monto;
        this.estadoCuota = estadoCuota;
        this.nombreCliente = nombreCliente;
    }

    public int getIdPago() { return idPago; }
    public LocalDate getFecha() { return fecha; }
    public double getMonto() { return monto; }
    public String getEstadoCuota() { return estadoCuota; }
    public String getNombreCliente() { return nombreCliente; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setMonto(double monto) { this.monto = monto; }
    public void setEstadoCuota(String estadoCuota) { this.estadoCuota = estadoCuota; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    @Override
    public String toString() {
        return "Pago[ID=" + idPago + ", Fecha=" + fecha.format(FORMATO)
                + ", Monto=$" + monto + ", Estado=" + estadoCuota
                + ", Cliente=" + nombreCliente + "]";
    }
}
