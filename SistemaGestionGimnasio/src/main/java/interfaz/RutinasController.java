package interfaz;

import control.ControlRutinas;
import entidad.Ejercicio;
import entidad.Rutinas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class RutinasController {

    private static ControlRutinas controlRutinas = new ControlRutinas();

    public static void mostrarRutinas(Stage stage) {
        ObservableList<Rutinas> rutinas = FXCollections.observableArrayList(controlRutinas.getRutinas());

        // ================== LISTADO ==================
        ListView<Rutinas> listaRutinas = new ListView<>(rutinas);
        listaRutinas.setPrefWidth(300);

        Label lblBuscar = new Label("Buscar:");
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre de rutina");
        tfBuscar.textProperty().addListener((obs, old, val) -> {
            String lower = val.toLowerCase();
            listaRutinas.setItems(rutinas.filtered(r -> r.getNombre().toLowerCase().contains(lower)));
        });

        // ================== BOTONES ==================
        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("button-principal");
        ImageView iconNuevo = new ImageView(new Image(RutinasController.class.getResourceAsStream("/icons/plus.png")));
        iconNuevo.setFitWidth(16); iconNuevo.setFitHeight(16);
        btnNuevo.setGraphic(iconNuevo);

        Button btnEditar = new Button();
        btnEditar.getStyleClass().add("button-principal");
        ImageView iconEditar = new ImageView(new Image(RutinasController.class.getResourceAsStream("/icons/edit.png")));
        iconEditar.setFitWidth(16); iconEditar.setFitHeight(16);
        btnEditar.setGraphic(iconEditar);
        btnEditar.setDisable(true);

        Button btnVolver = new Button();
        btnVolver.getStyleClass().add("button-principal");
        ImageView iconVolver = new ImageView(new Image(RutinasController.class.getResourceAsStream("/icons/arrow-left.png")));
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

        Label lblNombre = new Label("Nombre:");
        TextField tfNombre = new TextField();

        Label lblDescripcion = new Label("Descripción:");
        TextArea taDescripcion = new TextArea();
        taDescripcion.setPrefRowCount(3);

        Label lblInicio = new Label("Fecha Inicio:");
        DatePicker dpInicio = new DatePicker();

        Label lblFin = new Label("Fecha Fin:");
        DatePicker dpFin = new DatePicker();

        Label lblDia = new Label("Día:");
        ComboBox<Integer> cbDia = new ComboBox<>();
        cbDia.getItems().addAll(1,2,3,4,5,6,7);
        cbDia.setValue(1);

        // ================== LISTA EJERCICIOS ==================
        ObservableList<Ejercicio> ejerciciosTodos = FXCollections.observableArrayList();
        ListView<Ejercicio> listaEjercicios = new ListView<>();
        listaEjercicios.setPrefHeight(150);
        listaEjercicios.setItems(ejerciciosTodos.filtered(e -> e.getDia() == cbDia.getValue()));

        cbDia.setOnAction(e -> listaEjercicios.setItems(ejerciciosTodos.filtered(ex -> ex.getDia() == cbDia.getValue())));

        Button btnAgregarEj = new Button("Agregar Ejercicio");
        btnAgregarEj.getStyleClass().add("button-secundario");
        btnAgregarEj.setOnAction(e -> {
            Stage dialog = new Stage();
            GridPane gp = new GridPane();
            gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));

            TextField tfEjNombre = new TextField();
            TextField tfSeries = new TextField();
            TextField tfReps = new TextField();

            gp.add(new Label("Nombre:"),0,0); gp.add(tfEjNombre,1,0);
            gp.add(new Label("Series:"),0,1); gp.add(tfSeries,1,1);
            gp.add(new Label("Reps:"),0,2); gp.add(tfReps,1,2);

            Button btnOk = new Button("Agregar");
            btnOk.getStyleClass().add("button-secundario");
            gp.add(btnOk, 0, 3);

            btnOk.setOnAction(ev -> {
                try {
                    String nombre = tfEjNombre.getText();
                    int series = Integer.parseInt(tfSeries.getText());
                    int reps = Integer.parseInt(tfReps.getText());
                    int dia = cbDia.getValue();
                    int semanas = 1;
                    if(dpInicio.getValue()!=null && dpFin.getValue()!=null) {
                        semanas = (int) Math.max(1, dpInicio.getValue().until(dpFin.getValue()).getDays()/7 +1);
                    }
                    Ejercicio ejs = new Ejercicio(nombre, series, reps, dia, semanas);
                    ejerciciosTodos.add(ejs);
                    listaEjercicios.setItems(ejerciciosTodos.filtered(ex -> ex.getDia() == cbDia.getValue()));
                    dialog.close();
                } catch(Exception ex){ new Alert(Alert.AlertType.ERROR,"Datos inválidos").showAndWait(); }
            });

            Scene sc = new Scene(gp, 300, 200);
            sc.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());

            dialog.setScene(sc);
            dialog.show();

        });

        Button btnGuardar = new Button("Registrar Rutina");
        btnGuardar.getStyleClass().add("button-secundario-naranja");

        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");

        ficha.add(lblNombre,0,0); ficha.add(tfNombre,1,0);
        ficha.add(lblDescripcion,0,1); ficha.add(taDescripcion,1,1);
        ficha.add(lblInicio,0,2); ficha.add(dpInicio,1,2);
        ficha.add(lblFin,0,3); ficha.add(dpFin,1,3);
        ficha.add(lblDia,0,4); ficha.add(cbDia,1,4);
        ficha.add(new Label("Ejercicios:"),0,5); ficha.add(listaEjercicios,1,5);
        ficha.add(btnAgregarEj,1,6);
        ficha.add(btnGuardar,0,7); ficha.add(btnBorrar,1,7);

        // ================== LOGICA ==================
        listaRutinas.getSelectionModel().selectedItemProperty().addListener((obs,o,s)-> btnEditar.setDisable(s==null));

        btnNuevo.setOnAction(e -> {
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Rutina");
            tfNombre.clear(); taDescripcion.clear(); dpInicio.setValue(null); dpFin.setValue(null);
            ejerciciosTodos.clear();
        });

        btnEditar.setOnAction(e -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if(sel!=null){
                ficha.setVisible(true);
                btnGuardar.setText("Guardar Cambios");
                tfNombre.setText(sel.getNombre());
                taDescripcion.setText(sel.getDescripcion());
                dpInicio.setValue(sel.getFechaInicio());
                dpFin.setValue(sel.getFechaFin());
                ejerciciosTodos.clear();
                sel.getEjercicios().forEach(ej -> ejerciciosTodos.add(ej));
                listaEjercicios.setItems(ejerciciosTodos.filtered(ex -> ex.getDia() == cbDia.getValue()));
            }
        });

        btnGuardar.setOnAction(e -> {
            try {
                if(tfNombre.getText().isBlank() || taDescripcion.getText().isBlank())
                    throw new RuntimeException("Completar nombre y descripción");

                LocalDate inicio = dpInicio.getValue();
                LocalDate fin = dpFin.getValue();
                Rutinas nueva;
                if("Registrar Rutina".equals(btnGuardar.getText())){
                    nueva = new Rutinas(rutinas.size()+1, tfNombre.getText(), taDescripcion.getText(), inicio, fin);
                    ejerciciosTodos.forEach(nueva::agregarEjercicio);
                    controlRutinas.getRutinas().add(nueva);
                    rutinas.add(nueva);
                    new Alert(Alert.AlertType.INFORMATION,"Rutina registrada!").showAndWait();
                } else {
                    Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
                    sel.setFechaInicio(inicio);
                    sel.setFechaFin(fin);
                    sel.getEjercicios().clear();
                    ejerciciosTodos.forEach(sel::agregarEjercicio);
                    new Alert(Alert.AlertType.INFORMATION,"Cambios guardados!").showAndWait();
                    listaRutinas.refresh();
                }
                ficha.setVisible(false);
            } catch(RuntimeException ex){ new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait(); }
        });

        btnBorrar.setOnAction(e -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if(sel!=null){
                controlRutinas.getRutinas().remove(sel);
                rutinas.remove(sel);
                new Alert(Alert.AlertType.INFORMATION,"Rutina eliminada!").showAndWait();
                ficha.setVisible(false);
            }
        });

        // ================== LAYOUT ==================
        VBox listaVBox = new VBox(10, btnNuevo, btnEditar, lblBuscar, tfBuscar, listaRutinas);
        listaVBox.setPadding(new Insets(10));

        ScrollPane spFicha = new ScrollPane(ficha);
        spFicha.setFitToWidth(true);

        HBox topBox = new HBox(10, btnVolver); topBox.setPadding(new Insets(10));
        HBox root = new HBox(10, listaVBox, spFicha);
        VBox main = new VBox(topBox, root);

        Scene scene = new Scene(main,850,550);
        scene.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }
}

