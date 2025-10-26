package interfaz;

import interfaz.PagosController;
import interfaz.PagosController.Pago;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;

public class PagoDAO {

    public static void insertarPago(PagosController.Pago p) {
        String sql = "INSERT INTO Pago (nombreCliente, dni, fecha, actividad, monto, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getClienteNombre());
            ps.setString(2, p.getDni());
            ps.setDate(3, p.getFecha() != null ? Date.valueOf(p.getFecha()) : null);
            ps.setString(4, p.getActividad());
            ps.setDouble(5, p.getMonto());
            ps.setString(6, p.getEstado());

            ps.executeUpdate();
            System.out.println("Pago registrado: " + p.getClienteNombre() + " - " + p.getEstado());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<PagosController.Pago> listarPagos() {
        // 🔹 Datos simulados para probar sin base de datos
        ObservableList<PagosController.Pago> lista = FXCollections.observableArrayList(
                new PagosController.Pago("Juan Pérez", "45612378", LocalDate.of(2025,10,1), "Musculación", 26000, "Pagado"),
                new PagosController.Pago("María Gómez", "47896532", LocalDate.of(2025,9,15), "Crossfit x3", 28000, "Adeuda"),
                new PagosController.Pago("Lucía Díaz", "48952147", LocalDate.of(2025,10,20), "Combo Musculación+Cross x5", 32000, "Pagado"),
                new PagosController.Pago("Pedro Ruiz", "40123456", LocalDate.of(2025,10,5), "Musculación", 26000, "Adeuda")
        );
        return lista;

    }

   /* public static ObservableList<PagosController.Pago> listarPagos() {
        ObservableList<PagosController.Pago> lista = FXCollections.observableArrayList();

        String sql = "SELECT nombreCliente, dni, fecha, monto, estado FROM Pago";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PagosController.Pago p = new PagosController.Pago(
                        rs.getString("nombreCliente"),
                        rs.getString("dni"),
                        rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : LocalDate.now(),
                        rs.getDouble("monto"),
                        rs.getString("estado")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }*/
}
