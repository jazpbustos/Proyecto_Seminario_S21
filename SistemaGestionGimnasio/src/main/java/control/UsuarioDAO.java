package control;

import entidad.Usuario;
import interfaz.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public static Usuario validarLogin(String nombreUsuario, String contrasena) {
        String sql = """
            SELECT u.idUsuario, u.nombreUsuario, u.nombreCompleto, r.nombre AS rol
            FROM usuario u
            JOIN rol r ON u.idRol = r.idRol
            WHERE u.nombreUsuario = ? 
              AND u.contrasena = ?
              AND u.activo = TRUE
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Usuario user = new Usuario();
                user.setIdUsuario(rs.getInt("idUsuario"));
                user.setNombreUsuario(rs.getString("nombreUsuario"));
                user.setNombreCompleto(rs.getString("nombreCompleto"));
                user.setRol(rs.getString("rol"));
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Error al validar login: " + e.getMessage());
        }

        return null;
    }
}
