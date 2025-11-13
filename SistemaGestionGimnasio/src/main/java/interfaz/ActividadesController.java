package interfaz;

import control.ActividadDAO;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ActividadesController {

    public static class Actividad {
        private String nombre;
        private double precio;
        private int duracion; // duración en días

        public Actividad(String nombre, double precio, int duracion) {
            this.nombre = nombre;
            this.precio = precio;
            this.duracion = duracion;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public double getPrecio() { return precio; }
        public void setPrecio(double precio) { this.precio = precio; }

        public int getDuracion() { return duracion; }
        public void setDuracion(int duracion) { this.duracion = duracion; }

        @Override
        public String toString() {
            return nombre + " - $" + precio + " - " + duracion + " días";
        }
    }

    public static void mostrarActividades(Stage stage) {
        ObservableList<Actividad> actividades = ActividadDAO.listarActividades();

        ListView<Actividad> listaActividades = new ListView<>(actividades);
        listaActividades.setPrefWidth(250);

        // ================== BUSCAR ==================
        Label lblBuscar = new Label("Buscar:");
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre de actividad");
        tfBuscar.textProperty().addListener((obs, old, val) -> {
            String lower = val.toLowerCase();
            listaActividades.setItems(actividades.filtered(a ->
                    a.getNombre().toLowerCase().contains(lower)
            ));
        });

        // ================== BOTONES ==================
        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("button-principal");
        ImageView iconNuevo = new ImageView(new Image(ClientesController.class.getResourceAsStream("/icons/plus.png")));
        iconNuevo.setFitWidth(16); iconNuevo.setFitHeight(16);
        btnNuevo.setGraphic(iconNuevo);

        Button btnEditar = new Button();
        btnEditar.getStyleClass().add("button-principal");
        ImageView iconEditar = new ImageView(new Image(ClientesController.class.getResourceAsStream("/icons/edit.png")));
        iconEditar.setFitWidth(16); iconEditar.setFitHeight(16);
        btnEditar.setGraphic(iconEditar);
        btnEditar.setDisable(true);

        Button btnVolver = new Button();
        btnVolver.getStyleClass().add("button-principal");
        ImageView iconVolver = new ImageView(new Image(ClientesController.class.getResourceAsStream("/icons/arrow-left.png")));
        iconVolver.setFitWidth(16); iconVolver.setFitHeight(16);
        btnVolver.setGraphic(iconVolver);
        btnVolver.setOnAction(e -> {
            try {
                Stage stageMenu = new Stage();
                GestionGimnasioFX menu = new GestionGimnasioFX();
                menu.start(stageMenu);
                stage.close();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // ================== FORMULARIO ==================
        GridPane ficha = new GridPane();
        ficha.setHgap(10); ficha.setVgap(10); ficha.setPadding(new Insets(10));
        ficha.setVisible(false);

        Label lblNombre = new Label("Nombre de la actividad:");
        TextField tfNombre = new TextField();
        Label lblPrecio = new Label("Precio:");
        TextField tfPrecio = new TextField();
        Label lblDuracion = new Label("Duración (días):");
        TextField tfDuracion = new TextField();

        Button btnGuardar = new Button("Registrar Actividad");
        btnGuardar.getStyleClass().add("button-secundario-naranja");
        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");

        ficha.add(lblNombre, 0, 0); ficha.add(tfNombre, 1, 0);
        ficha.add(lblPrecio, 0, 1); ficha.add(tfPrecio, 1, 1);
        ficha.add(lblDuracion, 0, 2); ficha.add(tfDuracion, 1, 2);
        ficha.add(btnGuardar, 0, 3); ficha.add(btnBorrar, 1, 3);

        // ================== LOGICA ==================
        listaActividades.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> btnEditar.setDisable(s == null));

        btnNuevo.setOnAction(e -> {
            listaActividades.getSelectionModel().clearSelection();
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Actividad");
            tfNombre.clear(); tfPrecio.clear(); tfDuracion.clear();
        });

        btnEditar.setOnAction(e -> {
            Actividad sel = listaActividades.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ficha.setVisible(true);
                btnGuardar.setText("Guardar Cambios");
                tfNombre.setText(sel.getNombre());
                tfPrecio.setText(String.valueOf(sel.getPrecio()));
                tfDuracion.setText(String.valueOf(sel.getDuracion()));
            }
        });

        btnGuardar.setOnAction(e -> {
            try {
                if (tfNombre.getText().isBlank() || tfPrecio.getText().isBlank() || tfDuracion.getText().isBlank())
                    throw new RuntimeException("Debe completar todos los campos");

                double precio = Double.parseDouble(tfPrecio.getText());
                int duracion = Integer.parseInt(tfDuracion.getText());
                if (precio <= 0) throw new RuntimeException("El precio debe ser mayor a 0");
                if (duracion <= 0) throw new RuntimeException("La duración debe ser mayor a 0");

                if ("Registrar Actividad".equals(btnGuardar.getText())) {
                    Actividad nueva = new Actividad(tfNombre.getText(), precio, duracion);
                    ActividadDAO.insertarActividad(nueva);
                    actividades.add(nueva);
                    new Alert(Alert.AlertType.INFORMATION, "Actividad registrada!").showAndWait();
                } else {
                    Actividad sel = listaActividades.getSelectionModel().getSelectedItem();
                    sel.setNombre(tfNombre.getText());
                    sel.setPrecio(precio);
                    sel.setDuracion(duracion);
                    ActividadDAO.actualizarActividad(sel);
                    listaActividades.refresh();
                    new Alert(Alert.AlertType.INFORMATION, "Cambios guardados!").showAndWait();
                }
                ficha.setVisible(false);
            } catch (RuntimeException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        btnBorrar.setOnAction(e -> {
            Actividad sel = listaActividades.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ActividadDAO.borrarActividad(sel.getNombre());
                actividades.remove(sel);
                new Alert(Alert.AlertType.INFORMATION, "Actividad eliminada!").showAndWait();
                ficha.setVisible(false);
            }
        });

        // ================== LAYOUT ==================
        VBox listaVBox = new VBox(10, btnNuevo, btnEditar, lblBuscar, tfBuscar, listaActividades);
        listaVBox.setPadding(new Insets(10));

        ScrollPane spFicha = new ScrollPane(ficha);
        spFicha.setFitToWidth(true);

        HBox topBox = new HBox(10, btnVolver); topBox.setPadding(new Insets(10));
        HBox root = new HBox(10, listaVBox, spFicha);
        VBox main = new VBox(topBox, root);

        Scene scene = new Scene(main, 700, 400);
        scene.getStylesheets().add(ActividadesController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }
}
