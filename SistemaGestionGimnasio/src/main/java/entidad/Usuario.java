package entidad;

public class Usuario {
    private int idUsuario;
    private String nombreUsuario;
    private String contrasena;
    private String nombreCompleto;
    private String rol;
    private boolean activo;

    // ──────────── Constructores ────────────
    public Usuario() {}

    public Usuario(int idUsuario, String nombreUsuario, String contrasena,
                   String nombreCompleto, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
    }

    // ──────────── Getters y Setters ────────────
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // ──────────── toString (opcional para depurar) ────────────
    @Override
    public String toString() {
        return nombreUsuario + " (" + rol + ")";
    }
}
