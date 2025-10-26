package interfaz;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ActividadDAO {

    public static void insertarActividad(ActividadesController.Actividad a) {
        String sql = "INSERT INTO Actividad (nombre, precio, duracion) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getNombre());
            ps.setDouble(2, a.getPrecio());
            ps.setInt(3, a.getDuracion());
            ps.executeUpdate();
            System.out.println("Actividad insertada: " + a.getNombre());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void actualizarActividad(ActividadesController.Actividad a) {
        String sql = "UPDATE Actividad SET precio=?, duracion=? WHERE nombre=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, a.getPrecio());
            ps.setInt(2, a.getDuracion());
            ps.setString(3, a.getNombre());
            ps.executeUpdate();
            System.out.println("Actividad actualizada: " + a.getNombre());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void borrarActividad(String nombre) {
        String sql = "DELETE FROM Actividad WHERE nombre=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.executeUpdate();
            System.out.println("Actividad eliminada: " + nombre);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static ObservableList<ActividadesController.Actividad> listarActividades() {
        ObservableList<ActividadesController.Actividad> lista = FXCollections.observableArrayList();
        String sql = "SELECT nombre, precio, duracion FROM Actividad";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ActividadesController.Actividad a = new ActividadesController.Actividad(
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("duracion")
                );
                lista.add(a);
            }

        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
