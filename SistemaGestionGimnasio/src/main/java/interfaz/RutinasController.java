package interfaz;

import control.RutinaDAO;
import entidad.Ejercicio;
import entidad.Rutinas;
import entidad.Sesion;
import entidad.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.AlertUtils;
import utils.VentanaUtils;
import utils.PDFRutinaUtils;
import java.awt.Desktop;    // para abrir carpeta


import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class RutinasController {

    public static void mostrarRutinas(Stage stage) {

        ObservableList<Rutinas> rutinas =
                FXCollections.observableArrayList(RutinaDAO.listarRutinas());

        // ================== LISTADO ==================
        ListView<Rutinas> listaRutinas = new ListView<>(rutinas);
        listaRutinas.setPrefWidth(300);

        Label lblBuscar = new Label("Buscar:");
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre de rutina");
        tfBuscar.textProperty().addListener((obs, old, val) ->
                listaRutinas.setItems(
                        rutinas.filtered(r ->
                                r.getNombre().toLowerCase().contains(val.toLowerCase())
                        )
                )
        );

        // ================== BOTONES ==================
        Button btnNuevo = new Button();
        btnNuevo.getStyleClass().add("button-principal");
        btnNuevo.setGraphic(new ImageView(
                new Image(RutinasController.class.getResourceAsStream("/icons/plus.png"), 16, 16, true, true)
        ));

        Button btnEditar = new Button();
        btnEditar.getStyleClass().add("button-principal");
        btnEditar.setGraphic(new ImageView(
                new Image(RutinasController.class.getResourceAsStream("/icons/edit.png"), 16, 16, true, true)
        ));
        btnEditar.setDisable(true);

        Button btnVolver = new Button();
        btnVolver.getStyleClass().add("button-principal");
        btnVolver.setGraphic(new ImageView(
                new Image(RutinasController.class.getResourceAsStream("/icons/arrow-left.png"), 16, 16, true, true)
        ));

        btnVolver.setOnAction(e -> {
            try {
                Usuario usuario = Sesion.getUsuarioActual();
                Stage stageActual = (Stage) btnVolver.getScene().getWindow();
                GestionGimnasioFX menu = new GestionGimnasioFX();
                menu.mostrarVentanaPrincipal(stageActual, usuario);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // ================== FORMULARIO ==================
        GridPane ficha = new GridPane();
        ficha.setHgap(10);
        ficha.setVgap(10);
        ficha.setPadding(new Insets(10));
        ficha.setVisible(false);

        TextField tfNombre = new TextField();
        TextArea taDescripcion = new TextArea();
        taDescripcion.setPrefRowCount(3);

        DatePicker dpInicio = new DatePicker();
        DatePicker dpFin = new DatePicker();

        // ================== LISTA DE EJERCICIOS ==================
        ObservableList<Ejercicio> ejerciciosTodos = FXCollections.observableArrayList();
        ObservableList<String> ejerciciosMostrar = FXCollections.observableArrayList();

        ListView<String> listaEjercicios = new ListView<>(ejerciciosMostrar);
        listaEjercicios.setPrefHeight(200);

        Runnable actualizarListaEj = () -> {
            ejerciciosMostrar.clear();
            for (Ejercicio ej : ejerciciosTodos) {
                ejerciciosMostrar.add(
                        "Día " + ej.getDia() +
                                " | " + ej.getNombre() +
                                " | " + ej.getSeries() +
                                "x" + ej.getReps()
                );
            }
        };

        // ===== Botones ejercicios =====
        Button btnAgregarEj = new Button("Agregar");
        btnAgregarEj.getStyleClass().add("button-secundario");

        Button btnEditarEj = new Button("Editar");
        btnEditarEj.getStyleClass().add("button-secundario");
        btnEditarEj.setDisable(true);

        listaEjercicios.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnEditarEj.setDisable(sel == null)
        );

        btnAgregarEj.setOnAction(ev ->
                abrirDialogoEjercicio(dpInicio, dpFin, ejerciciosTodos, actualizarListaEj, null)
        );

        btnEditarEj.setOnAction(ev -> {
            int index = listaEjercicios.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                Ejercicio seleccionado = ejerciciosTodos.get(index);
                abrirDialogoEjercicio(dpInicio, dpFin, ejerciciosTodos, actualizarListaEj, seleccionado);
            }
        });

        HBox hEj = new HBox(5, btnAgregarEj, btnEditarEj);

        // ================== NOTAS SEMANALES ==================
        VBox notasBox = new VBox(5);
        List<TextField> notasSemanaFields = new ArrayList<>();

        Runnable actualizarNotasUI = () -> {
            notasBox.getChildren().clear();
            notasSemanaFields.clear();

            LocalDate ini = dpInicio.getValue();
            LocalDate fin = dpFin.getValue();
            if (ini == null || fin == null) return;

            int semanas = (int) Math.max(1, ChronoUnit.DAYS.between(ini, fin) / 7 + 1);

            for (int i = 1; i <= semanas; i++) {
                HBox h = new HBox(5);
                Label l = new Label("Semana " + i + ":");
                TextField tf = new TextField();
                notasSemanaFields.add(tf);
                h.getChildren().addAll(l, tf);
                notasBox.getChildren().add(h);
            }
        };

        dpInicio.valueProperty().addListener((obs, a, b) -> actualizarNotasUI.run());
        dpFin.valueProperty().addListener((obs, a, b) -> actualizarNotasUI.run());


        // ================== ARMAR FICHA ==================
        ficha.add(new Label("Nombre:"), 0, 0);
        ficha.add(tfNombre, 1, 0);

        ficha.add(new Label("Descripción:"), 0, 1);
        ficha.add(taDescripcion, 1, 1);

        ficha.add(new Label("Fecha inicio:"), 0, 2);
        ficha.add(dpInicio, 1, 2);

        ficha.add(new Label("Fecha fin:"), 0, 3);
        ficha.add(dpFin, 1, 3);

        ficha.add(new Label("Ejercicios:"), 0, 4);
        ficha.add(listaEjercicios, 1, 4);

        ficha.add(hEj, 1, 5);

        ficha.add(new Label("Notas semanales:"), 0, 6);
        ficha.add(notasBox, 1, 6);

        // ================== BOTONES GUARDAR / BORRAR ==================
        HBox hGuardar = new HBox(10);
        hGuardar.setAlignment(Pos.CENTER_LEFT);

        Button btnGuardar = new Button("Registrar Rutina");
        btnGuardar.getStyleClass().add("button-secundario-naranja");

        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");

        hGuardar.getChildren().addAll(btnGuardar, btnBorrar);

        ficha.add(hGuardar, 1, 7);


        // ================== LÓGICA ==================

        listaRutinas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) ->
                btnEditar.setDisable(sel == null)
        );

        // ==== NUEVO ====
        btnNuevo.setOnAction(event -> {
            listaRutinas.getSelectionModel().clearSelection();
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Rutina");

            tfNombre.clear();
            taDescripcion.clear();
            dpInicio.setValue(null);
            dpFin.setValue(null);

            ejerciciosTodos.clear();
            ejerciciosMostrar.clear();

            notasBox.getChildren().clear();
            notasSemanaFields.clear();
        });

        // ==== EDITAR ====
        btnEditar.setOnAction(event -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            ficha.setVisible(true);
            btnGuardar.setText("Guardar Cambios");

            tfNombre.setText(sel.getNombre());
            taDescripcion.setText(sel.getDescripcion());
            dpInicio.setValue(sel.getFechaInicio());
            dpFin.setValue(sel.getFechaFin());

            // ========== EJERCICIOS ==========
            ejerciciosTodos.clear();
            ejerciciosTodos.addAll(sel.getEjercicios());
            actualizarListaEj.run();

            // ========== NOTAS ==========
            notasBox.getChildren().clear();
            notasSemanaFields.clear();

            List<String> notas = sel.getNotasSemanales();
            for (int i = 0; i < notas.size(); i++) {
                HBox h = new HBox(5);
                Label lbl = new Label("Semana " + (i + 1) + ":");
                TextField tf = new TextField(notas.get(i));
                notasSemanaFields.add(tf);
                h.getChildren().addAll(lbl, tf);
                notasBox.getChildren().add(h);
            }
        });

        // ==== EXPORTAR ====
        Button btnExportar = new Button();
        btnExportar.getStyleClass().add("button-principal");
        btnExportar.setGraphic(new ImageView(
                new Image(RutinasController.class.getResourceAsStream("/icons/export.png"), 16, 16, true, true)
        ));
        btnExportar.setDisable(true);

        listaRutinas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            btnEditar.setDisable(sel == null);
            btnExportar.setDisable(sel == null);
        });

        btnExportar.setOnAction(e -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            File pdf = PDFRutinaUtils.exportarPDF(sel);

            if (pdf != null) {
                try {
                    Desktop.getDesktop().open(pdf.getParentFile());
                } catch (Exception ex) { ex.printStackTrace(); }

                AlertUtils.mostrar(Alert.AlertType.INFORMATION, "PDF generado",
                        "PDF generado correctamente:\n" + pdf.getAbsolutePath());
            } else {
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", "Error al generar el PDF.");
            }
        });

// ==== GUARDAR ====
        btnGuardar.setOnAction(event -> {
            try {
                if (tfNombre.getText().isBlank() || taDescripcion.getText().isBlank())
                    throw new RuntimeException("Completar nombre y descripción");

                LocalDate inicio = dpInicio.getValue();
                LocalDate fin = dpFin.getValue();

                if (inicio != null && fin != null && fin.isBefore(inicio)) {
                    AlertUtils.mostrar(Alert.AlertType.ERROR, "Error de fechas",
                            "La fecha de fin no puede ser anterior a la fecha de inicio");
                    return;
                }

                int semanas = 1;
                if (inicio != null && fin != null)
                    semanas = (int) Math.max(1, ChronoUnit.DAYS.between(inicio, fin) / 7 + 1);

                List<String> notas = new ArrayList<>();
                for (TextField tf : notasSemanaFields) notas.add(tf.getText());

                if (btnGuardar.getText().equals("Registrar Rutina")) {
                    Rutinas nueva = new Rutinas(0, tfNombre.getText(), taDescripcion.getText(), inicio, fin);
                    nueva.setNotasSemanales(notas);
                    ejerciciosTodos.forEach(nueva::agregarEjercicio);
                    RutinaDAO.insertarRutina(nueva);
                    rutinas.add(nueva);

                    AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Rutina registrada", "Rutina registrada!");
                } else {
                    Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
                    sel.setNombre(tfNombre.getText());
                    sel.setDescripcion(taDescripcion.getText());
                    sel.setFechaInicio(inicio);
                    sel.setFechaFin(fin);
                    sel.setNotasSemanales(notas);

                    sel.getEjercicios().clear();
                    sel.getEjercicios().addAll(ejerciciosTodos);

                    RutinaDAO.actualizarRutina(sel);
                    listaRutinas.refresh();

                    AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Cambios guardados", "Cambios guardados!");
                }

                ficha.setVisible(false);

            } catch (RuntimeException ex) {
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", ex.getMessage());
            }
        });

// ==== BORRAR ====
        btnBorrar.setOnAction(event -> {
            Rutinas sel = listaRutinas.getSelectionModel().getSelectedItem();
            if (sel != null) {
                RutinaDAO.borrarRutina(sel.getIdRutina());
                rutinas.remove(sel);
                AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Rutina eliminada", "Rutina eliminada!");
                ficha.setVisible(false);
            }
        });

        // ================== LAYOUT ==================
        VBox listaVBox = new VBox(10, btnNuevo, btnEditar, btnExportar, lblBuscar, tfBuscar, listaRutinas);
        listaVBox.setPadding(new Insets(10));

        ScrollPane spFicha = new ScrollPane(ficha);
        spFicha.setFitToWidth(true);

        HBox topBox = new HBox(10, btnVolver);
        topBox.setPadding(new Insets(10));

        HBox root = new HBox(10, listaVBox, spFicha);
        VBox main = new VBox(topBox, root);

        Scene scene = new Scene(main, 900, 600);
        scene.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
        VentanaUtils.centrar(stage);

    }

    // ============================================================
    // ============= DIALOGO AGREGAR / EDITAR EJERCICIO ===========
    // ============================================================
    private static void abrirDialogoEjercicio(DatePicker dpInicio, DatePicker dpFin,
                                              ObservableList<Ejercicio> ejerciciosTodos,
                                              Runnable actualizarLista,
                                              Ejercicio editar) {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);
        dialog.centerOnScreen();

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));
        gp.setAlignment(Pos.CENTER);


        TextField tfNombre = new TextField(editar != null ? editar.getNombre() : "");
        TextField tfSeries = new TextField(editar != null ? String.valueOf(editar.getSeries()) : "");
        TextField tfReps = new TextField(editar != null ? String.valueOf(editar.getReps()) : "");

        ComboBox<Integer> cbDia = new ComboBox<>();
        cbDia.getItems().addAll(1, 2, 3, 4, 5, 6, 7);
        cbDia.setValue(editar != null ? editar.getDia() : 1);

        gp.add(new Label("Nombre:"), 0, 0);  gp.add(tfNombre, 1, 0);
        gp.add(new Label("Series:"), 0, 1);  gp.add(tfSeries, 1, 1);
        gp.add(new Label("Reps:"), 0, 2);    gp.add(tfReps, 1, 2);
        gp.add(new Label("Día:"), 0, 3);     gp.add(cbDia, 1, 3);

        Button btnOk = new Button(editar != null ? "Guardar cambios" : "Agregar");
        btnOk.getStyleClass().add("button-secundario");
        gp.add(btnOk, 1, 4);

        btnOk.setOnAction(e -> {
            try {
                String nombre = tfNombre.getText();
                int series = Integer.parseInt(tfSeries.getText());
                int reps = Integer.parseInt(tfReps.getText());
                int dia = cbDia.getValue();

                Ejercicio nuevo = new Ejercicio(nombre, series, reps, dia);

                if (editar == null) {
                    ejerciciosTodos.add(nuevo);
                } else {
                    int index = ejerciciosTodos.indexOf(editar);
                    ejerciciosTodos.set(index, nuevo);
                }

                actualizarLista.run();
                dialog.close();

            } catch (Exception ex) {
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Datos inválidos", "Datos inválidos");
            }
        });

        Scene sc = new Scene(gp, 300, 220);
        sc.getStylesheets().add(RutinasController.class.getResource("/estilos.css").toExternalForm());
        dialog.setScene(sc);
        dialog.show();
        dialog.setOnShown(ev -> dialog.centerOnScreen());

    }
}
