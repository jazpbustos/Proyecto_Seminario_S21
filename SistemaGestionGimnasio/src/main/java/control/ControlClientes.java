package control;

import entidad.Clientes;
import java.util.ArrayList;
import java.util.List;

/**
 * ControlClientes: maneja la lista en memoria de clientes.
 */
public class ControlClientes {
    private List<Clientes> clientes;

    public ControlClientes() {
        clientes = new ArrayList<>();
    }

    public void agregarCliente(Clientes c) {
        clientes.add(c);
        System.out.println("Cliente agregado: " + c);
    }

    public void listarClientes() {
        System.out.println("=== Lista de clientes ===");
        for (Clientes c : clientes) System.out.println(c);
    }

    public Clientes buscarClientePorDni(String dni) {
        if (dni == null) return null;
        for (Clientes c : clientes) {
            if (dni.equals(c.getDni())) return c;
        }
        return null;
    }

    public List<Clientes> getClientes() { return clientes; }

    public int generarId() { return clientes.size() + 1; }
}
