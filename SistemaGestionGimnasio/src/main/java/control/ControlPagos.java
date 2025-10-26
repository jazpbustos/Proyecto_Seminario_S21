package control;

import entidad.Pagos;
import java.util.ArrayList;
import java.util.List;

/**
 * ControlPagos: historial de pagos en memoria.
 */
public class ControlPagos {
    private List<Pagos> pagos;

    public ControlPagos() {
        pagos = new ArrayList<>();
    }

    public void agregarPago(Pagos p) {
        pagos.add(p);
        System.out.println("Pago registrado: " + p);
    }

    public List<Pagos> getPagos() { return pagos; }

    public void listarPagos() {
        System.out.println("=== Historial de pagos ===");
        for (Pagos p : pagos) System.out.println(p);
    }
}
