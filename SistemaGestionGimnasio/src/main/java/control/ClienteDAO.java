package control;

import interfaz.ClientesController;
import interfaz.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ClienteDAO {

    // ────────────────────────────────
    // INSERTAR CLIENTE
    // ────────────────────────────────
    public static void insertarCliente(ClientesController.Cliente c) {
        String sql = """
            INSERT INTO Cliente 
            (nombre, apellido, DNI, telefono, correo, fechaNacimiento, actividad, idRutina, estadoPago) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getCelular());
            ps.setString(5, c.getCorreo());
            ps.setDate(6, Date.valueOf(c.getFechaNac()));
            ps.setString(7, c.getActividad());
            ps.setObject(8, c.getRutina() != null ? Integer.parseInt(c.getRutina()) : null); // idRutina
            ps.setString(9, c.getPago()); // estadoPago

            ps.executeUpdate();
            System.out.println("✅ Cliente insertado: " + c.getNombreCompleto());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────
    // ACTUALIZAR CLIENTE
    // ────────────────────────────────
    public static void actualizarCliente(ClientesController.Cliente c) {
        String sql = """
            UPDATE Cliente 
            SET nombre=?, apellido=?, telefono=?, correo=?, fechaNacimiento=?, actividad=?, idRutina=?, estadoPago=? 
            WHERE DNI=?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getCelular());
            ps.setString(4, c.getCorreo());
            ps.setDate(5, Date.valueOf(c.getFechaNac()));
            ps.setString(6, c.getActividad());
            ps.setObject(7, c.getRutina() != null ? Integer.parseInt(c.getRutina()) : null); // idRutina
            ps.setString(8, c.getPago());
            ps.setString(9, c.getDni());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("📝 Cliente actualizado correctamente: " + c.getNombreCompleto());
            } else {
                System.err.println("⚠️ No se encontró el cliente para actualizar: " + c.getDni());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────
    // BORRAR CLIENTE
    // ────────────────────────────────
    public static void borrarCliente(String dni) {
        String sql = "DELETE FROM Cliente WHERE DNI=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.executeUpdate();
            System.out.println("🗑️ Cliente eliminado: " + dni);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────
    // LISTAR CLIENTES
    // ────────────────────────────────
    public static ObservableList<ClientesController.Cliente> listarClientes() {
        ObservableList<ClientesController.Cliente> lista = FXCollections.observableArrayList();

        String sqlClientes = """
        SELECT idCliente, DNI, nombre, apellido, telefono, correo, fechaNacimiento, actividad, idRutina, estadoPago
        FROM Cliente
    """;

        String sqlUltimoPago = """
        SELECT p.fecha AS fechaPago, p.estadoCuota
        FROM Pago p
        WHERE p.idCliente = ?
        ORDER BY p.fecha DESC
        LIMIT 1
    """;

        String sqlActividad = """
        SELECT precio, duracion
        FROM Actividad
        WHERE nombre = ?
    """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlClientes);
             PreparedStatement psPago = conn.prepareStatement(sqlUltimoPago);
             PreparedStatement psActividad = conn.prepareStatement(sqlActividad)) {

            while (rs.next()) {
                int idCliente = rs.getInt("idCliente");
                String dni = rs.getString("DNI");
                ClientesController.Cliente c = new ClientesController.Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        dni
                );

                // Datos del cliente
                c.setCelular(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                c.setFechaNac(rs.getDate("fechaNacimiento") != null ? rs.getDate("fechaNacimiento").toLocalDate() : null);
                c.setActividad(rs.getString("actividad"));
                c.setRutina(rs.getString("idRutina"));
                c.setPago(rs.getString("estadoPago"));
                c.setDiasActivos("Sin cuota activa");

                // Traer precio y duración desde Actividad
                if (c.getActividad() != null) {
                    psActividad.setString(1, c.getActividad());
                    try (ResultSet ra = psActividad.executeQuery()) {
                        if (ra.next()) {
                            c.setPrecio(String.valueOf(ra.getDouble("precio")));
                            c.setDuracion(ra.getInt("duracion"));
                        }
                    }
                }

// Último pago solo para fecha y días activos
                psPago.setInt(1, idCliente);
                try (ResultSet rp = psPago.executeQuery()) {
                    if (rp.next()) {
                        LocalDate fechaPago = rp.getDate("fechaPago") != null ? rp.getDate("fechaPago").toLocalDate() : null;
                        c.setFechaPago(fechaPago);

                        if (fechaPago != null) {
                            long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaPago.plusDays(c.getDuracion()));
                            c.setDiasActivos(diasRestantes + " días restantes");
                        }
                    }
                }


                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
