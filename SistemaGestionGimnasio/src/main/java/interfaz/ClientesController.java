package interfaz;

import control.ClienteDAO;
import control.PagoDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDate;
import java.time.Period;

public class ClientesController {

    public static class Cliente {
        private String nombre;
        private String apellido;
        private String dni;
        private String celular;
        private String correo;
        private LocalDate fechaNac;
        private String actividad;
        private String pago;
        private String rutina;
        private String precio;
        private int edad;
        private String diasActivos;
        private LocalDate fechaPago;
        private int duracion; // antes diasCuota

        public Cliente(String nombre, String apellido, String dni) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.dni = dni;
        }

        public String getNombreCompleto() {
            return nombre + " " + apellido + " (" + dni + ")";
        }

        // Getters y setters
        public String getDiasActivos() { return diasActivos; }
        public void setDiasActivos(String diasActivos) { this.diasActivos = diasActivos; }
        public LocalDate getFechaPago() { return fechaPago; }
        public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
        public int getDuracion() { return duracion; }
        public void setDuracion(int duracion) { this.duracion = duracion; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        public String getDni() { return dni; }
        public void setDni(String dni) { this.dni = dni; }
        public String getCelular() { return celular; }
        public void setCelular(String celular) { this.celular = celular; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public LocalDate getFechaNac() { return fechaNac; }
        public void setFechaNac(LocalDate fechaNac) { this.fechaNac = fechaNac; }
        public String getActividad() { return actividad; }
        public void setActividad(String actividad) { this.actividad = actividad; }
        public String getPago() { return pago; }
        public void setPago(String pago) { this.pago = pago; }
        public String getRutina() { return rutina; }
        public void setRutina(String rutina) { this.rutina = rutina; }
        public String getPrecio() { return precio; }
        public void setPrecio(String precio) { this.precio = precio; }
        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }
    }

    public static void mostrarClientes(Stage stage) {
        ObservableList<Cliente> clientes = ClienteDAO.listarClientes();

        ListView<Cliente> listaClientes = new ListView<>(clientes);
        listaClientes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text nombreText = new Text(item.getNombreCompleto() + " ");
                    nombreText.setFill(Color.WHITE);
                    Text diasText = new Text(item.getDiasActivos() != null ? item.getDiasActivos() : "");
                    String estado = item.getPago();
                    if ("Pagó".equalsIgnoreCase(estado)) {
                        if (diasText.getText().startsWith("-")) {
                            diasText.setStyle("-fx-fill: red; -fx-font-weight: bold;");
                        } else {
                            diasText.setStyle("-fx-fill: green; -fx-font-weight: bold;");
                        }
                    } else {
                        diasText.setStyle("-fx-fill: gray; -fx-font-weight: bold;");
                    }
                    TextFlow flow = new TextFlow(nombreText, diasText);
                    setGraphic(flow);
                }
            }
        });
        listaClientes.setPrefWidth(250);

        // BUSCAR
        Label lblBuscar = new Label("Buscar:");
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Nombre, Apellido o DNI");
        tfBuscar.textProperty().addListener((obs, oldValue, newValue) -> {
            String lower = newValue.toLowerCase();
            listaClientes.setItems(clientes.filtered(c ->
                    c.getNombre().toLowerCase().contains(lower) ||
                            c.getApellido().toLowerCase().contains(lower) ||
                            c.getDni().toLowerCase().contains(lower)
            ));
        });

        // BOTONES
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

        // FICHA
        GridPane ficha = new GridPane();
        ficha.setHgap(10); ficha.setVgap(10); ficha.setPadding(new Insets(10));
        ficha.setVisible(false);

        Label lblNombre = new Label("Nombre:");
        TextField tfNombre = new TextField();
        Label lblApellido = new Label("Apellido:");
        TextField tfApellido = new TextField();
        Label lblDNI = new Label("DNI:");
        TextField tfDNI = new TextField();
        Label lblCelular = new Label("Celular:");
        TextField tfCelular = new TextField();
        Label lblCorreo = new Label("Correo:");
        TextField tfCorreo = new TextField();
        Label lblFechaNac = new Label("Fecha de Nacimiento:");
        DatePicker dpFechaNac = new DatePicker();
        Label lblEdad = new Label("Edad:");
        TextField tfEdad = new TextField();
        tfEdad.setEditable(false);
        Label lblActividad = new Label("Actividad:");
        ComboBox<String> cbActividad = new ComboBox<>(FXCollections.observableArrayList(
                "Musculación", "Musculación medio mes", "Crossfit x3", "Combo Musculación+Cross x5"
        ));
        Label lblPrecio = new Label("Precio:");
        TextField tfPrecio = new TextField();
        tfPrecio.setEditable(false);
        Label lblPago = new Label("Estado de pago:");
        ComboBox<String> cbPago = new ComboBox<>(FXCollections.observableArrayList("Pagó", "Adeuda"));
        Label lblFechaPago = new Label("Fecha de pago:");
        DatePicker dpFechaPago = new DatePicker(LocalDate.now());
        Label lblDuracion = new Label("Duración (días):");
        TextField tfDuracion = new TextField();
        tfDuracion.setEditable(false);
        Label lblDiasActivos = new Label("Días de cuota activa:");
        TextField tfDiasActivos = new TextField();
        tfDiasActivos.setEditable(false);
        Label lblRutina = new Label("Rutina:");
        ComboBox<String> cbRutina = new ComboBox<>(FXCollections.observableArrayList(
                "Rutina A", "Rutina B", "Rutina C"
        ));

        Button btnEnviarRutina = new Button("Enviar Rutina");
        btnEnviarRutina.getStyleClass().add("button-secundario");
        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("button-secundario-naranja");
        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");

        // AGREGAR AL GRID
        ficha.add(lblNombre, 0, 0); ficha.add(tfNombre, 1, 0);
        ficha.add(lblApellido, 0, 1); ficha.add(tfApellido, 1, 1);
        ficha.add(lblDNI, 0, 2); ficha.add(tfDNI, 1, 2);
        ficha.add(lblCelular, 0, 3); ficha.add(tfCelular, 1, 3);
        ficha.add(lblCorreo, 0, 4); ficha.add(tfCorreo, 1, 4);
        ficha.add(lblFechaNac, 0, 5); ficha.add(dpFechaNac, 1, 5);
        ficha.add(lblEdad, 0, 6); ficha.add(tfEdad, 1, 6);
        ficha.add(lblActividad, 0, 7); ficha.add(cbActividad, 1, 7);
        ficha.add(lblPrecio, 0, 8); ficha.add(tfPrecio, 1, 8);
        ficha.add(lblPago, 0, 9); ficha.add(cbPago, 1, 9);
        ficha.add(lblFechaPago, 2, 9); ficha.add(dpFechaPago, 3, 9);
        ficha.add(lblDuracion, 0, 10); ficha.add(tfDuracion, 1, 10);
        ficha.add(lblDiasActivos, 0, 11); ficha.add(tfDiasActivos, 1, 11);
        ficha.add(lblRutina, 0, 12); ficha.add(cbRutina, 1, 12);
        ficha.add(btnEnviarRutina, 1, 13); ficha.add(btnGuardar, 0, 14); ficha.add(btnBorrar, 1, 14);

        // EDAD AUTOMÁTICA
        dpFechaNac.setOnAction(e -> {
            if (dpFechaNac.getValue() != null) {
                if (dpFechaNac.getValue().isAfter(LocalDate.now())) {
                    new Alert(Alert.AlertType.ERROR, "La fecha de nacimiento no puede ser futura").showAndWait();
                    dpFechaNac.setValue(null); tfEdad.clear(); return;
                }
                tfEdad.setText(String.valueOf(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears()));
            }
        });

        cbActividad.setOnAction(e -> {
            String act = cbActividad.getValue(); // devuelve String
            if ("Musculación".equals(act)) {
                tfPrecio.setText("26000");
                tfDuracion.setText("30");
            } else if ("Musculación medio mes".equals(act)) {
                tfPrecio.setText("14000");
                tfDuracion.setText("15");
            } else if ("Crossfit x3".equals(act)) {
                tfPrecio.setText("28000");
                tfDuracion.setText("30");
            } else if ("Combo Musculación+Cross x5".equals(act)) {
                tfPrecio.setText("32000");
                tfDuracion.setText("30");
            } else {
                tfPrecio.clear();
                tfDuracion.clear();
            }
        });





        // LOGICA DE DIAS ACTIVOS
        Runnable actualizarDiasActivos = () -> {
            if ("Pagó".equals(cbPago.getValue()) && dpFechaPago.getValue() != null) {
                int duracion = 30;
                try { duracion = Integer.parseInt(tfDuracion.getText().replaceAll("\\D", "")); } catch (NumberFormatException ex) {}
                LocalDate fechaPago = dpFechaPago.getValue();
                long diasRestantes = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaPago.plusDays(duracion));
                tfDiasActivos.setText(diasRestantes + " días restantes");
            } else if ("Adeuda".equals(cbPago.getValue())) {
                tfDiasActivos.setText("Sin cuota activa");
            } else { tfDiasActivos.clear(); }
        };
        cbPago.setOnAction(e -> actualizarDiasActivos.run());
        dpFechaPago.setOnAction(e -> actualizarDiasActivos.run());
        tfDuracion.focusedProperty().addListener((obs, oldF, newF) -> { if (!newF) actualizarDiasActivos.run(); });

        // SELECCION DE CLIENTE
        listaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> btnEditar.setDisable(sel == null));

        // VALIDACIONES
        Runnable validarFormulario = () -> {
            if (tfNombre.getText().isBlank() || tfApellido.getText().isBlank()) throw new RuntimeException("Nombre y Apellido son obligatorios");
            if (tfDNI.getText().isBlank()) throw new RuntimeException("DNI es obligatorio");
            if (tfCelular.getText().isBlank()) throw new RuntimeException("Celular es obligatorio");
            if (dpFechaNac.getValue() == null) throw new RuntimeException("Fecha de nacimiento es obligatoria");
            if (dpFechaNac.getValue().isAfter(LocalDate.now())) throw new RuntimeException("La fecha de nacimiento no puede ser futura");
            if (cbActividad.getValue() == null || cbActividad.getValue().isBlank()) throw new RuntimeException("Debe seleccionar una actividad");
            if (cbPago.getValue() == null || cbPago.getValue().isBlank()) throw new RuntimeException("Estado de pago es obligatorio");
        };

        // BOTONES
        btnNuevo.setOnAction(e -> {
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Cliente");
            tfNombre.clear(); tfApellido.clear(); tfDNI.clear(); tfCelular.clear(); tfCorreo.clear();
            dpFechaNac.setValue(null); tfEdad.clear(); cbActividad.setValue(null); tfPrecio.clear();
            cbPago.setValue(null); dpFechaPago.setValue(LocalDate.now()); tfDuracion.setText("30"); tfDiasActivos.clear();
            cbRutina.setValue(null);
        });

        btnEditar.setOnAction(e -> {
            Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ficha.setVisible(true);
                btnGuardar.setText("Guardar Cambios");
                tfNombre.setText(sel.getNombre()); tfApellido.setText(sel.getApellido()); tfDNI.setText(sel.getDni());
                tfCelular.setText(sel.getCelular()); tfCorreo.setText(sel.getCorreo()); dpFechaNac.setValue(sel.getFechaNac());
                tfEdad.setText(sel.getEdad() > 0 ? String.valueOf(sel.getEdad()) : "");
                cbActividad.setValue(sel.getActividad()); tfPrecio.setText(sel.getPrecio());
                cbPago.setValue(sel.getPago()); dpFechaPago.setValue(sel.getFechaPago() != null ? sel.getFechaPago() : LocalDate.now());
                tfDuracion.setText(String.valueOf(sel.getDuracion())); tfDiasActivos.setText(sel.getDiasActivos());
                cbRutina.setValue(sel.getRutina());
            }
        });

        btnGuardar.setOnAction(e -> {
            try {
                validarFormulario.run();
                if ("Registrar Cliente".equals(btnGuardar.getText())) {
                    Cliente nuevo = new Cliente(tfNombre.getText(), tfApellido.getText(), tfDNI.getText());
                    nuevo.setCelular(tfCelular.getText()); nuevo.setCorreo(tfCorreo.getText());
                    nuevo.setFechaNac(dpFechaNac.getValue());
                    nuevo.setEdad(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears());
                    nuevo.setActividad(cbActividad.getValue()); nuevo.setPrecio(tfPrecio.getText());
                    nuevo.setPago(cbPago.getValue()); nuevo.setFechaPago(dpFechaPago.getValue());
                    nuevo.setDuracion(Integer.parseInt(tfDuracion.getText()));
                    actualizarDiasActivos.run();
                    nuevo.setDiasActivos(tfDiasActivos.getText());
                    nuevo.setRutina(cbRutina.getValue());
                    ClienteDAO.insertarCliente(nuevo);
                    clientes.add(nuevo);
                    PagosController.Pago nuevoPago = new PagosController.Pago(
                            nuevo.getNombre() + " " + nuevo.getApellido(), nuevo.getDni(),
                            dpFechaPago.getValue(), nuevo.getActividad(),
                            Double.parseDouble(nuevo.getPrecio()), nuevo.getPago()
                    );
                    PagoDAO.insertarPago(nuevoPago);
                    new Alert(Alert.AlertType.INFORMATION, "Registro exitoso!").showAndWait();
                    ficha.setVisible(false);
                } else {
                    Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
                    sel.setNombre(tfNombre.getText()); sel.setApellido(tfApellido.getText()); sel.setDni(tfDNI.getText());
                    sel.setCelular(tfCelular.getText()); sel.setCorreo(tfCorreo.getText()); sel.setFechaNac(dpFechaNac.getValue());
                    sel.setEdad(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears());
                    sel.setActividad(cbActividad.getValue()); sel.setPrecio(tfPrecio.getText());
                    sel.setPago(cbPago.getValue()); sel.setFechaPago(dpFechaPago.getValue());
                    sel.setDuracion(Integer.parseInt(tfDuracion.getText()));
                    actualizarDiasActivos.run();
                    sel.setDiasActivos(tfDiasActivos.getText()); sel.setRutina(cbRutina.getValue());
                    ClienteDAO.actualizarCliente(sel);
                    listaClientes.refresh();

                    // Registrar pago actualizado
                    PagosController.Pago nuevoPago = new PagosController.Pago(
                            sel.getNombre() + " " + sel.getApellido(), sel.getDni(), dpFechaPago.getValue(),
                            sel.getActividad(), Double.parseDouble(sel.getPrecio()), sel.getPago()
                    );
                    PagoDAO.insertarPago(nuevoPago);
                    new Alert(Alert.AlertType.INFORMATION, "Cambios guardados!").showAndWait();
                    ficha.setVisible(false);
                }
            } catch (RuntimeException ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        btnBorrar.setOnAction(e -> {
            Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ClienteDAO.borrarCliente(sel.getDni());
                clientes.remove(sel);
                listaClientes.getSelectionModel().clearSelection();
                new Alert(Alert.AlertType.INFORMATION, "Cliente eliminado!").showAndWait();
                ficha.setVisible(false);
            }
        });

        // LAYOUT
        VBox listaVBox = new VBox(10, btnNuevo, btnEditar, lblBuscar, tfBuscar, listaClientes);
        listaVBox.setPadding(new Insets(10)); listaVBox.setPrefWidth(300);

        ScrollPane spFicha = new ScrollPane(ficha); spFicha.setFitToWidth(true); spFicha.setPrefWidth(650); spFicha.setPrefHeight(450);

        HBox topBox = new HBox(10, btnVolver); topBox.setPadding(new Insets(10));
        HBox root = new HBox(10, listaVBox, spFicha);
        VBox main = new VBox(topBox, root);

        Scene scene = new Scene(main, 1000, 500);
        scene.getStylesheets().add(ClientesController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }
}
