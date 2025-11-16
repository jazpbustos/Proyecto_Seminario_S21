package control;

import interfaz.PagosController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;

public class PagoDAO {

    // ────────────────────────────────
    // INSERTAR / ACTUALIZAR PAGO (desde ventana Cliente)
    // ────────────────────────────────
    public static boolean insertarPagoYDetectar(PagosController.Pago p) {
        String sqlCliente = "SELECT idCliente FROM Cliente WHERE dni = ?";
        String sqlActividad = "SELECT idActividad FROM Actividad WHERE nombre = ?";
        String sqlExiste = """
            SELECT idPago 
            FROM Pago 
            WHERE idCliente = ? AND idActividad = ? 
            AND MONTH(fecha) = ? AND YEAR(fecha) = ?
        """;
        String sqlInsert = """
            INSERT INTO Pago (idCliente, idActividad, fecha, monto, estadoCuota)
            VALUES (?, ?, ?, ?, ?)
        """;
        String sqlUpdate = """
            UPDATE Pago SET monto = ?, estadoCuota = ?, fecha = ? WHERE idPago = ?
        """;

        try (Connection conn = DBConnection.getConnection()) {

            int idCliente = -1;
            int idActividad = -1;
            int idPagoExistente = -1;

            // Buscar idCliente por DNI
            try (PreparedStatement psCli = conn.prepareStatement(sqlCliente)) {
                psCli.setString(1, p.getDni());
                ResultSet rs = psCli.executeQuery();
                if (rs.next()) idCliente = rs.getInt("idCliente");
            }

            // Buscar idActividad por nombre
            try (PreparedStatement psAct = conn.prepareStatement(sqlActividad)) {
                psAct.setString(1, p.getActividad());
                ResultSet rs = psAct.executeQuery();
                if (rs.next()) idActividad = rs.getInt("idActividad");
            }

            if (idCliente == -1 || idActividad == -1) {
                System.err.println("⚠️ No se encontró cliente o actividad para el pago: "
                        + p.getApellido() + ", " + p.getNombre() + " - " + p.getActividad());
                return false;
            }

            // Verificar si ya existe un pago del mismo mes y año
            LocalDate fecha = p.getFecha();
            try (PreparedStatement psExiste = conn.prepareStatement(sqlExiste)) {
                psExiste.setInt(1, idCliente);
                psExiste.setInt(2, idActividad);
                psExiste.setInt(3, fecha.getMonthValue());
                psExiste.setInt(4, fecha.getYear());
                ResultSet rs = psExiste.executeQuery();
                if (rs.next()) idPagoExistente = rs.getInt("idPago");
            }

            if (idPagoExistente != -1) {
                // 🔁 Ya existe → se actualiza
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                    ps.setDouble(1, p.getMonto());
                    ps.setString(2, p.getEstado());
                    ps.setDate(3, Date.valueOf(p.getFecha()));
                    ps.setInt(4, idPagoExistente);
                    ps.executeUpdate();
                    System.out.println("🔁 Pago actualizado: " + p.getApellido() + ", " + p.getNombre());
                    return true; // actualizado
                }
            } else {
                // 🆕 No existe → se inserta
                insertarPago(p);
                return false; // nuevo
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ────────────────────────────────
    // INSERTAR PAGO (simple)
    // ────────────────────────────────
    public static void insertarPago(PagosController.Pago p) {
        String sqlCliente = "SELECT idCliente FROM Cliente WHERE dni = ?";
        String sqlActividad = "SELECT idActividad FROM Actividad WHERE nombre = ?";
        String sqlInsert = "INSERT INTO Pago (idCliente, idActividad, fecha, monto, estadoCuota) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            int idCliente = -1;
            int idActividad = -1;

            try (PreparedStatement psCli = conn.prepareStatement(sqlCliente)) {
                psCli.setString(1, p.getDni());
                ResultSet rs = psCli.executeQuery();
                if (rs.next()) idCliente = rs.getInt("idCliente");
            }

            try (PreparedStatement psAct = conn.prepareStatement(sqlActividad)) {
                psAct.setString(1, p.getActividad());
                ResultSet rs = psAct.executeQuery();
                if (rs.next()) idActividad = rs.getInt("idActividad");
            }

            if (idCliente == -1 || idActividad == -1) return;

            try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                ps.setInt(1, idCliente);
                ps.setInt(2, idActividad);
                ps.setDate(3, Date.valueOf(p.getFecha()));
                ps.setDouble(4, p.getMonto());
                ps.setString(5, p.getEstado());
                ps.executeUpdate();
                System.out.println("💰 Pago registrado: " + p.getApellido() + ", " + p.getNombre());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────
    // DETECTAR SI EXISTE PAGO EN UNA FECHA EXACTA
    // ────────────────────────────────
    public static boolean existePagoEnFecha(String dni, LocalDate fechaPago) {
        String sql = """
            SELECT COUNT(*) 
            FROM Pago p
            JOIN Cliente c ON p.idCliente = c.idCliente
            WHERE c.dni = ? AND p.fecha = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.setDate(2, Date.valueOf(fechaPago));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ────────────────────────────────
    // ACTUALIZAR UN PAGO EXISTENTE (misma fecha)
    // ────────────────────────────────
    public static void actualizarPagoExistente(PagosController.Pago p) {
        String sql = """
            UPDATE Pago
            SET monto = ?, estadoCuota = ?, idActividad = (
                SELECT idActividad FROM Actividad WHERE nombre = ?
            )
            WHERE idCliente = (SELECT idCliente FROM Cliente WHERE dni = ?)
            AND fecha = ?
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, p.getMonto());
            ps.setString(2, p.getEstado());
            ps.setString(3, p.getActividad());
            ps.setString(4, p.getDni());
            ps.setDate(5, Date.valueOf(p.getFecha()));
            ps.executeUpdate();
            System.out.println("📝 Pago existente actualizado (" + p.getApellido() + ", " + p.getNombre() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ────────────────────────────────
    // LISTAR PAGOS DESDE LA BASE
    // ────────────────────────────────
    public static ObservableList<PagosController.Pago> listarPagos() {
        ObservableList<PagosController.Pago> lista = FXCollections.observableArrayList();

        String sql = """
            SELECT c.nombre AS nombre,
                   c.apellido AS apellido,
                   c.dni,
                   a.nombre AS actividad,
                   p.fecha, p.monto, p.estadoCuota
            FROM Pago p
            JOIN Cliente c ON p.idCliente = c.idCliente
            JOIN Actividad a ON p.idActividad = a.idActividad
            ORDER BY p.fecha DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PagosController.Pago pago = new PagosController.Pago(
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("dni"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("actividad"),
                        rs.getDouble("monto"),
                        rs.getString("estadoCuota")
                );
                lista.add(pago);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}

