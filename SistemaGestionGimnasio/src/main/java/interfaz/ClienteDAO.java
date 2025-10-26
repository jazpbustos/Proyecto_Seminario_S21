package interfaz;

import interfaz.ClientesController;
import interfaz.ClientesController.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class ClienteDAO {

    public static void insertarCliente(ClientesController.Cliente c) {
        String sql = "INSERT INTO Cliente (nombre, apellido, dni, fechaNac, actividad, pago, rutina, precio, " +
                "edad) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setDate(4, c.getFechaNac() != null ? Date.valueOf(c.getFechaNac()) : null);
            ps.setString(5, c.getActividad());
            ps.setString(6, c.getPago());
            ps.setString(7, c.getRutina());
            ps.setString(8, c.getPrecio());
            ps.setInt(9, c.getEdad());

            ps.executeUpdate();
            System.out.println("Cliente insertado: " + c.getNombreCompleto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void actualizarCliente(ClientesController.Cliente c) {
        String sql = "UPDATE Cliente SET nombre=?, apellido=?, fechaNac=?, actividad=?, pago=?, rutina=?, precio=?, edad=? WHERE dni=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setDate(3, c.getFechaNac() != null ? Date.valueOf(c.getFechaNac()) : null);
            ps.setString(4, c.getActividad());
            ps.setString(5, c.getPago());
            ps.setString(6, c.getRutina());
            ps.setString(7, c.getPrecio());
            ps.setInt(8, c.getEdad());
            ps.setString(9, c.getDni());

            ps.executeUpdate();
            System.out.println("Cliente actualizado: " + c.getNombreCompleto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void borrarCliente(String dni) {
        String sql = "DELETE FROM Cliente WHERE dni=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.executeUpdate();
            System.out.println("Cliente eliminado: " + dni);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<ClientesController.Cliente> listarClientes() {
        ObservableList<ClientesController.Cliente> lista = FXCollections.observableArrayList();
        String sql = "SELECT nombre, apellido, dni, fechaNac, actividad, pago, rutina, precio, edad FROM Cliente";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ClientesController.Cliente c = new ClientesController.Cliente(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni")
                );
                c.setFechaNac(rs.getDate("fechaNac") != null ? rs.getDate("fechaNac").toLocalDate() : null);
                c.setActividad(rs.getString("actividad"));
                c.setPago(rs.getString("pago"));
                c.setRutina(rs.getString("rutina"));
                c.setPrecio(rs.getString("precio"));
                c.setEdad(rs.getInt("edad"));
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
