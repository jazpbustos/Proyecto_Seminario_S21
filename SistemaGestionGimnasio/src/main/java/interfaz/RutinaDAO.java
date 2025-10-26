package interfaz;

import entidad.Ejercicio;
import entidad.Rutinas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class RutinaDAO {

    // Insertar rutina
    public static void insertarRutina(Rutinas r) {
        String sqlRutina = "INSERT INTO Rutina (idRutina, nombre, descripcion, fechaInicio, fechaFin) VALUES (?, ?, ?, ?, ?)";
        String sqlEjercicio = "INSERT INTO Ejercicio (idRutina, nombre, series, reps, dia) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psRutina = conn.prepareStatement(sqlRutina);
             PreparedStatement psEj = conn.prepareStatement(sqlEjercicio)) {

            // Insertar rutina
            psRutina.setInt(1, r.getIdRutina());
            psRutina.setString(2, r.getNombre());
            psRutina.setString(3, r.getDescripcion());
            psRutina.setDate(4, r.getFechaInicio() != null ? Date.valueOf(r.getFechaInicio()) : null);
            psRutina.setDate(5, r.getFechaFin() != null ? Date.valueOf(r.getFechaFin()) : null);
            psRutina.executeUpdate();

            // Insertar ejercicios
            for (Ejercicio e : r.getEjercicios()) {
                psEj.setInt(1, r.getIdRutina());
                psEj.setString(2, e.getNombre());
                psEj.setInt(3, e.getSeries());
                psEj.setInt(4, e.getReps());
                psEj.setInt(5, e.getDia()); // NUEVO: guardar día
                psEj.executeUpdate();
            }

            System.out.println("Rutina insertada: " + r.getNombre());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Actualizar rutina
    public static void actualizarRutina(Rutinas r) {
        String sqlRutina = "UPDATE Rutina SET descripcion=?, fechaInicio=?, fechaFin=? WHERE idRutina=?";
        String sqlBorrarEj = "DELETE FROM Ejercicio WHERE idRutina=?";
        String sqlInsertarEj = "INSERT INTO Ejercicio (idRutina, nombre, series, reps, dia) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psRutina = conn.prepareStatement(sqlRutina);
             PreparedStatement psBorrarEj = conn.prepareStatement(sqlBorrarEj);
             PreparedStatement psInsertEj = conn.prepareStatement(sqlInsertarEj)) {

            // Actualizar datos generales
            psRutina.setString(1, r.getDescripcion());
            psRutina.setDate(2, r.getFechaInicio() != null ? Date.valueOf(r.getFechaInicio()) : null);
            psRutina.setDate(3, r.getFechaFin() != null ? Date.valueOf(r.getFechaFin()) : null);
            psRutina.setInt(4, r.getIdRutina());
            psRutina.executeUpdate();

            // Borrar ejercicios antiguos
            psBorrarEj.setInt(1, r.getIdRutina());
            psBorrarEj.executeUpdate();

            // Insertar ejercicios nuevos
            for (Ejercicio e : r.getEjercicios()) {
                psInsertEj.setInt(1, r.getIdRutina());
                psInsertEj.setString(2, e.getNombre());
                psInsertEj.setInt(3, e.getSeries());
                psInsertEj.setInt(4, e.getReps());
                psInsertEj.setInt(5, e.getDia()); // NUEVO: guardar día
                psInsertEj.executeUpdate();
            }

            System.out.println("Rutina actualizada: " + r.getNombre());
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Borrar rutina
    public static void borrarRutina(int idRutina) {
        String sqlEj = "DELETE FROM Ejercicio WHERE idRutina=?";
        String sqlRut = "DELETE FROM Rutina WHERE idRutina=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement psEj = conn.prepareStatement(sqlEj);
             PreparedStatement psRut = conn.prepareStatement(sqlRut)) {

            psEj.setInt(1, idRutina);
            psEj.executeUpdate();

            psRut.setInt(1, idRutina);
            psRut.executeUpdate();

            System.out.println("Rutina eliminada: " + idRutina);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // Listar rutinas
    public static ObservableList<Rutinas> listarRutinas() {
        ObservableList<Rutinas> lista = FXCollections.observableArrayList();
        String sqlRutina = "SELECT * FROM Rutina";
        String sqlEj = "SELECT nombre, series, reps, dia FROM Ejercicio WHERE idRutina=?";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rsRut = stmt.executeQuery(sqlRutina);
             PreparedStatement psEj = conn.prepareStatement(sqlEj)) {

            while (rsRut.next()) {
                int id = rsRut.getInt("idRutina");
                Rutinas r = new Rutinas(id,
                        rsRut.getString("nombre"),
                        rsRut.getString("descripcion"),
                        rsRut.getDate("fechaInicio") != null ? rsRut.getDate("fechaInicio").toLocalDate() : null,
                        rsRut.getDate("fechaFin") != null ? rsRut.getDate("fechaFin").toLocalDate() : null);

                // Obtener ejercicios
                psEj.setInt(1, id);
                try (ResultSet rsEj = psEj.executeQuery()) {
                    while (rsEj.next()) {
                        Ejercicio e = new Ejercicio(
                                rsEj.getString("nombre"),
                                rsEj.getInt("series"),
                                rsEj.getInt("reps"),
                                rsEj.getInt("dia"), // NUEVO: leer día
                                r.getSemanas()
                        );
                        r.agregarEjercicio(e);
                    }
                }

                lista.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}
