package interfaz;

import control.UsuarioDAO;
import entidad.Sesion;
import entidad.Usuario;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import utils.VentanaUtils;

public class LoginFX extends Application {

    @Override
    public void start(Stage stage) {

        // 🔹 Icono en barra de título
        stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));

        // 🔹 Logo principal
        ImageView logo = new ImageView(new Image("file:src/main/resources/img/logo.png"));
        logo.getStyleClass().add("logo-login");
        logo.setFitWidth(130);
        logo.setPreserveRatio(true);

        // 🔹 Título
        Label lblTitulo = new Label("Inicio de Sesión");
        lblTitulo.getStyleClass().add("titulo-login");

        // --- HEADER
        VBox header = new VBox(25, logo, lblTitulo);
        header.setAlignment(Pos.CENTER);
        header.getStyleClass().add("header-login");

        // --- CAMPOS DE FORMULARIO
        TextField tfUsuario = new TextField();
        tfUsuario.setPromptText("Usuario");
        tfUsuario.setMaxWidth(220);

        PasswordField tfContrasena = new PasswordField();
        tfContrasena.setPromptText("Contraseña");
        tfContrasena.setMaxWidth(220);

        Button btnIngresar = new Button("Ingresar");
        btnIngresar.getStyleClass().add("button-secundario-naranja");
        btnIngresar.setMaxWidth(120);

        // 👉 Esto hace que ENTER ejecute el botón automáticamente
        btnIngresar.setDefaultButton(true);

        // --- FORMULARIO
        VBox form = new VBox(20, tfUsuario, tfContrasena, btnIngresar);
        form.setAlignment(Pos.CENTER);
        form.getStyleClass().add("form-login");

        // --- CONTENEDOR PRINCIPAL
        VBox layout = new VBox(20, header, form);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("vbox-login");

        // --- ACCIÓN DEL BOTÓN INGRESAR
        btnIngresar.setOnAction(e -> {
            String usuario = tfUsuario.getText().trim();
            String contrasena = tfContrasena.getText().trim();

            // 🚫 Primero validamos campos vacíos
            if (usuario.isEmpty() || contrasena.isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Ingrese usuario y contraseña.");
                return;
            }

            // 🔍 Validación de usuario/contraseña
            Usuario user = UsuarioDAO.validarLogin(usuario, contrasena);

            if (user != null) {
                Sesion.setUsuarioActual(user);
                GestionGimnasioFX home = new GestionGimnasioFX();
                home.mostrarVentanaPrincipal(stage, user);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación", "Usuario o contraseña incorrectos.");
                tfContrasena.clear();
                tfUsuario.requestFocus();
            }
        });

        // --- ESCENA Y CONFIGURACIÓN
        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add("estilos.css");

        stage.setTitle("Login - Sistema de Gestión");
        stage.setResizable(false);
        stage.setScene(scene);
        VentanaUtils.centrar(stage);
        stage.show();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.getDialogPane().getStylesheets().add(
                ClientesController.class.getResource("/estilos.css").toExternalForm()
        );

        alerta.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

