package interfaz;

import control.RutinaDAO;
import entidad.Ejercicio;
import entidad.Rutinas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RutinasController {

    public static void mostrarRutinas(Stage stage) {
        ObservableList<Rutinas> rutinas = FXCollections.observableArrayList(RutinaDAO.listarRutinas());

        // ================== LISTADO ==================
        ListView<Rutinas> listaRutinas = new ListView<>(rutinas);
        listaRutinas.setPrefWidth(300);

        Label lblBuscar = new Label("Buscar:");
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre de rutina");
        tfBuscar.textProperty().addListener((obs, old, val) ->
                listaRutinas.setItems(rutinas.filtered(r -> r.getNombre().toLowerCase().contains(val.toLowerCase()))));

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

        TextField tfNombre = new TextField();
        TextArea taDescripcion = new TextArea();
        taDescripcion.setPrefRowCount(3);
        DatePicker dpInicio = new DatePicker();
        DatePicker dpFin = new DatePicker();

        // ================== LISTA EJERCICIOS ==================
        ObservableList<Ejercicio> ejerciciosTodos = FXCollections.observableArrayList();
        ObservableList<String> ejerciciosMostrar = FXCollections.observableArrayList();
        ListView<String> listaEjercicios = new ListView<>(ejerciciosMostrar);
        listaEjercicios.setPrefHeight(200);

        // Método para actualizar la lista de ejercicios en formato "Día | Nombre | Series | Reps"
        Runnable actualizarLista = () -> {
            ejerciciosMostrar.clear();
            for(Ejercicio ej : ejerciciosTodos){
                ejerciciosMostrar.add("Día " + ej.getDia() + " | " + ej.getNombre() + " | " + ej.getSeries() + " | " + ej.getReps());
            }
        };

        // ================== BOTÓN AGREGAR EJERCICIO ==================
        Button btnAgregarEj = new Button("Agregar Ejercicio");
        btnAgregarEj.getStyleClass().add("button-secundario");
        btnAgregarEj.setOnAction(ev -> {
            Stage dialog = new Stage();
            GridPane gp = new GridPane();
            gp.setHgap(10); gp.setVgap(10); gp.setPadding(new Insets(10));

            TextField tfEjNombre = new TextField();
            TextField tfSeries = new TextField();
            TextField tfReps = new TextField();
            ComboBox<Integer> cbDia = new ComboBox<>();
            cbDia.getItems().addAll(1,2,3,4,5,6,7);
            cbDia.setValue(1);

            gp.add(new Label("Nombre:"),0,0); gp.add(tfEjNombre,1,0);
            gp.add(new Label("Series:"),0,1); gp.add(tfSeries,1,1);
            gp.add(new Label("Reps:"),0,2); gp.add(tfReps,1,2);
            gp.add(new Label("Día:"),0,3); gp.add(cbDia,1,3);

            Button btnOk = new Button("Agregar");
            btnOk.getStyleClass().add("button-secundario");
            gp.add(btnOk, 0, 4);

            btnOk.setOnAction(ev2 -> {
                try {
                    String nombre = tfEjNombre.getText();
                    int series = Integer.parseInt(tfSeries.getText());
                    int reps = Integer.parseInt(tfReps.getText());
                    int dia = cbDia.getValue();
                    int semanas = 1;
                    if(dpInicio.getValue()!=null && dpFin.getValue()!=null){
                        semanas = (int) Math.max(1, ChronoUnit.DAYS.between(dpInicio.getValue(), dpFin.getValue())/7 +1);
                    }
                    Ejercicio ejs = new Ejercicio(nombre, series, reps, dia, semanas);
                    ejerciciosTodos.add(ejs);
                    actualizarLista.run();
                    dialog.close();
                } catch(Exception ex){
                    new Alert(Alert.AlertType.ERROR,"Datos inválidos").showAndWait();
                }
            });

            Scene sc = new Scene(gp, 300, 250);
            sc.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());
            dialog.setScene(sc);
            dialog.show();
        });

        // ================== NOTAS SEMANALES ==================
        VBox notasBox = new VBox(5);
        List<TextField> notasSemanasFields = new ArrayList<>();
        dpFin.valueProperty().addListener((obs, oldV, newV) -> {
            notasBox.getChildren().clear();
            notasSemanasFields.clear();
            if(dpInicio.getValue()!=null && newV!=null){
                int semanas = (int) Math.max(1, ChronoUnit.DAYS.between(dpInicio.getValue(), newV)/7 +1);
                for(int i=1;i<=semanas;i++){
                    HBox h = new HBox(5);
                    Label lbl = new Label("Semana "+i+":");
                    TextField tfNota = new TextField();
                    notasSemanasFields.add(tfNota);
                    h.getChildren().addAll(lbl, tfNota);
                    notasBox.getChildren().add(h);
                }
            }
        });

        // ================== BOTONES GUARDAR/BORRAR ==================
        Button btnGuardar = new Button("Registrar Rutina");
        btnGuardar.getStyleClass().add("button-secundario-naranja");
        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");

        // ================== AGREGAR AL GRID ==================
        ficha.add(new Label("Nombre:"),0,0); ficha.add(tfNombre,1,0);
        ficha.add(new Label("Descripción:"),0,1); ficha.add(taDescripcion,1,1);
        ficha.add(new Label("Fecha Inicio:"),0,2); ficha.add(dpInicio,1,2);
        ficha.add(new Label("Fecha Fin:"),0,3); ficha.add(dpFin,1,3);
        ficha.add(new Label("Ejercicios:"),0,4); ficha.add(listaEjercicios,1,4);
        ficha.add(btnAgregarEj,1,5);
        ficha.add(new Label("Notas Semanales:"),0,6); ficha.add(notasBox,1,6);
        ficha.add(btnGuardar,0,7); ficha.add(btnBorrar,1,7);

        // ================== LOGICA ==================
        listaRutinas.getSelectionModel().selectedItemProperty().addListener((obs,o,s)-> btnEditar.setDisable(s==null));

        btnNuevo.setOnAction(event -> {
            listaRutinas.getSelectionModel().clearSelection();
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Rutina");
            tfNombre.clear(); taDescripcion.clear(); dpInicio.setValue(null); dpFin.setValue(null);
            ejerciciosTodos.clear(); ejerciciosMostrar.clear(); notasBox.getChildren().clear(); notasSemanasFields.clear();
        });

        btnEditar.setOnAction(event -> {
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
                actualizarLista.run();

                // Notas semanales
                notasBox.getChildren().clear();
                notasSemanasFields.clear();
                int semanas = (int) Math.max(1, ChronoUnit.DAYS.between(sel.getFechaInicio(), sel.getFechaFin())/7 +1);
                for(int i=0;i<semanas;i++){
                    HBox h = new HBox(5);
                    Label lbl = new Label("Semana "+(i+1)+":");
                    TextField tfNota = new TextField();
                    if(!sel.getEjercicios().isEmpty() && sel.getEjercicios().get(0).getNotasSemanales().size()>i) {
                        tfNota.setText(sel.getEjercicios().get(0).getNotasSemanales().get(i));
                    }
                    notasSemanasFields.add(tfNota);
                    h.getChildren().addAll(lbl, tfNota);
                    notasBox.getChildren().add(h);
                }
            }
        });

        btnGuardar.setOnAction(event -> {
            try {
                if(tfNombre.getText().isBlank() || taDescripcion.getText().isBlank())
                    throw new RuntimeException("Completar nombre y descripción");

                LocalDate inicio = dpInicio.getValue();
                LocalDate fin = dpFin.getValue();
                int semanas = (int) Math.max(1, ChronoUnit.DAYS.between(inicio, fin)/7 +1);

                Rutinas nueva;
                if("Registrar Rutina".equals(btnGuardar.getText())){
                    nueva = new Rutinas(0, tfNombre.getText(), taDescripcion.getText(), inicio, fin);
                    ejerciciosTodos.forEach(ej -> {
                        ej.setSemanas(semanas);
                        ej.setNotasSemanales(new ArrayList<>());
                        for(TextField tfNota : notasSemanasFields) ej.getNotasSemanales().add(tfNota.getText());
                        nueva.agregarEjercicio(ej);
                    });
                    RutinaDAO.insertarRutina(nueva);
                    rutinas.add(nueva);
                    new Alert(Alert.AlertType.INFORMATION,"Rutina registrada!").showAndWait();
                } else {
                    Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
                    sel.setNombre(tfNombre.getText());
                    sel.setDescripcion(taDescripcion.getText());
                    sel.setFechaInicio(inicio);
                    sel.setFechaFin(fin);
                    sel.getEjercicios().clear();
                    ejerciciosTodos.forEach(ej -> {
                        ej.setSemanas(semanas);
                        ej.setNotasSemanales(new ArrayList<>());
                        for(TextField tfNota : notasSemanasFields) ej.getNotasSemanales().add(tfNota.getText());
                        sel.agregarEjercicio(ej);
                    });
                    RutinaDAO.actualizarRutina(sel);
                    listaRutinas.refresh();
                    new Alert(Alert.AlertType.INFORMATION,"Cambios guardados!").showAndWait();
                }
                ficha.setVisible(false);
            } catch(RuntimeException ex){
                new Alert(Alert.AlertType.ERROR,ex.getMessage()).showAndWait();
            }
        });

        btnBorrar.setOnAction(event -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if(sel != null){
                RutinaDAO.borrarRutina(sel.getIdRutina());
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

        Scene scene = new Scene(main, 900, 600);
        scene.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }
}

