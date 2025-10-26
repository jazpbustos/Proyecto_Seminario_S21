package interfaz;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Conexión a la base de datos exitosa!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Error al conectar a la base de datos");
        }
    }
}
