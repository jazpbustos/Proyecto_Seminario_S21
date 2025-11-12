package interfaz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GestionGimnasioFX extends Application {

    private String nombreUsuario = "Admin";
    private Stage stageClientes; // <-- variable de clase

    @Override
    public void start(Stage stage) {
        Label bienvenida = new Label("Bienvenido, " + nombreUsuario + "!");
        bienvenida.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button btnClientes = new Button("Clientes");
        btnClientes.getStyleClass().add("button-home");
        btnClientes.setOnAction(e -> ClientesController.mostrarClientes(stage));


        Button btnPagos = new Button("Pagos");
        btnPagos.getStyleClass().add("button-home");
        btnPagos.setOnAction(e -> PagosController.mostrarPagos(stage));

        Button btnActividades = new Button("Actividades");
        btnActividades.getStyleClass().add("button-home");
        btnActividades.setOnAction(e -> ActividadesController.mostrarActividades(stage));

        Button btnRutinas = new Button("Rutinas");
        btnRutinas.getStyleClass().add("button-home");
        btnRutinas.setOnAction(e -> RutinasController.mostrarRutinas(stage));

        Button btnSalir = new Button("Salir");
        btnSalir.getStyleClass().add("button-home");
        btnSalir.setOnAction(e -> stage.close());

        VBox menu = new VBox(10, bienvenida, btnClientes, btnPagos, btnActividades, btnRutinas, btnSalir);
        menu.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(menu, 400, 300);
        scene.getStylesheets().add("estilos.css");

        stage.setTitle("Sistema de Gestión");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
