package interfaz;

import control.ActividadDAO;
import control.ClienteDAO;
import control.PagoDAO;
import control.RutinaDAO;
import entidad.Rutinas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Period;

public class ClientesController {

    public static class Cliente {
        private String nombre, apellido, dni, celular, correo, actividad, pago, rutina, precio, diasActivos;
        private LocalDate fechaNac, fechaPago;
        private int edad, duracion;

        public Cliente(String nombre, String apellido, String dni) {
            this.nombre = nombre; this.apellido = apellido; this.dni = dni;
        }

        public String getNombreCompleto() { return nombre + " " + apellido + " (" + dni + ")"; }

        // --- Getters & Setters ---
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
        public String getDiasActivos() { return diasActivos; }
        public void setDiasActivos(String diasActivos) { this.diasActivos = diasActivos; }
        public LocalDate getFechaPago() { return fechaPago; }
        public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
        public int getEdad() { return edad; }
        public void setEdad(int edad) { this.edad = edad; }
        public int getDuracion() { return duracion; }
        public void setDuracion(int duracion) { this.duracion = duracion; }
    }

    public static void mostrarClientes(Stage stage) {
        ObservableList<Cliente> clientes = FXCollections.observableArrayList(ClienteDAO.listarClientes());
        ListView<Cliente> listaClientes = new ListView<>(clientes);
        listaClientes.setItems(clientes);

        // --- LISTADO CLIENTES CON ESTADO ---
        listaClientes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Text nombre = new Text(item.getNombreCompleto() + " ");
                    nombre.setFill(Color.WHITE);
                    Text dias = new Text(item.getDiasActivos() != null ? item.getDiasActivos() : "");
                    if ("Pagó".equalsIgnoreCase(item.getPago())) {
                        dias.setStyle(item.getDiasActivos() != null && item.getDiasActivos().startsWith("-")
                                ? "-fx-fill: red; -fx-font-weight: bold;"
                                : "-fx-fill: green; -fx-font-weight: bold;");
                    } else dias.setStyle("-fx-fill: gray; -fx-font-weight: bold;");
                    setGraphic(new TextFlow(nombre, dias));
                }
            }
        });

        // --- BUSCAR CLIENTE ---
        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Buscar por nombre, apellido o DNI");
        tfBuscar.textProperty().addListener((obs, old, val) -> {
            String lower = val.toLowerCase();
            listaClientes.setItems(clientes.filtered(c ->
                    c.getNombre().toLowerCase().contains(lower)
                            || c.getApellido().toLowerCase().contains(lower)
                            || c.getDni().toLowerCase().contains(lower)
            ));
        });

        // --- BOTONES PRINCIPALES ---
        Button btnNuevo = crearBotonConIcono("/icons/plus.png");
        Button btnEditar = crearBotonConIcono("/icons/edit.png");
        btnEditar.setDisable(true);

        Button btnVolver = new Button();
        btnVolver.getStyleClass().add("button-principal");
        ImageView iconVolver = new ImageView(new Image(ClientesController.class.getResourceAsStream("/icons/arrow-left.png")));
        iconVolver.setFitWidth(16);
        iconVolver.setFitHeight(16);
        btnVolver.setGraphic(iconVolver);
        btnVolver.setOnAction(e -> {
            try {
                Stage stageMenu = new Stage();
                GestionGimnasioFX menu = new GestionGimnasioFX();
                menu.start(stageMenu);
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        // --- FICHA ---
        GridPane ficha = new GridPane();
        ficha.setHgap(10); ficha.setVgap(10); ficha.setPadding(new Insets(10)); ficha.setVisible(false);

        TextField tfNombre = new TextField();
        TextField tfApellido = new TextField();
        TextField tfDNI = new TextField();
        TextField tfCelular = new TextField();
        TextField tfCorreo = new TextField();
        DatePicker dpFechaNac = new DatePicker();
        TextField tfEdad = new TextField(); tfEdad.setEditable(false);
        TextField tfPrecio = new TextField(); tfPrecio.setEditable(false);
        TextField tfDuracion = new TextField(); tfDuracion.setEditable(false);
        TextField tfDiasActivos = new TextField(); tfDiasActivos.setEditable(false);
        DatePicker dpFechaPago = new DatePicker(LocalDate.now());
        ComboBox<String> cbPago = new ComboBox<>(FXCollections.observableArrayList("Pagó", "Adeuda"));

        // --- ACTIVIDADES DB ---
        ComboBox<ActividadesController.Actividad> cbActividad = new ComboBox<>();
        cbActividad.setItems(ActividadDAO.listarActividades());
        cbActividad.setConverter(new StringConverter<>() {
            @Override public String toString(ActividadesController.Actividad a) { return a != null ? a.getNombre() : ""; }
            @Override public ActividadesController.Actividad fromString(String s) { return null; }
        });

        cbActividad.setOnAction(e -> {
            ActividadesController.Actividad act = cbActividad.getValue();
            if (act != null) {
                tfPrecio.setText(String.valueOf(act.getPrecio()));
                tfDuracion.setText(String.valueOf(act.getDuracion()));
            }
        });

        // --- RUTINAS DB ---
        ComboBox<Rutinas> cbRutina = new ComboBox<>();
        cbRutina.setItems(RutinaDAO.listarRutinas());
        cbRutina.setConverter(new StringConverter<>() {
            @Override public String toString(Rutinas r) { return r != null ? r.getNombre() : ""; }
            @Override public Rutinas fromString(String s) { return null; }
        });

        // --- BOTONES SECUNDARIOS ---
        Button btnGuardar = new Button("Guardar");
        btnGuardar.getStyleClass().add("button-secundario-naranja");
        Button btnBorrar = new Button("Borrar");
        btnBorrar.getStyleClass().add("button-secundario-rojo");
        Button btnRegistrarPago = new Button("Registrar Pago");
        btnRegistrarPago.getStyleClass().add("button-secundario");
        Button btnEnviarRutina = new Button("Enviar Rutina");
        btnEnviarRutina.getStyleClass().add("button-secundario");


        // --- ORGANIZAR FICHA ---
        int r = 0;
        ficha.addRow(r++, new Label("Nombre:"), tfNombre);
        ficha.addRow(r++, new Label("Apellido:"), tfApellido);
        ficha.addRow(r++, new Label("DNI:"), tfDNI);
        ficha.addRow(r++, new Label("Celular:"), tfCelular);
        ficha.addRow(r++, new Label("Correo:"), tfCorreo);
        ficha.addRow(r++, new Label("Fecha Nacimiento:"), dpFechaNac);
        ficha.addRow(r++, new Label("Edad:"), tfEdad);
        ficha.addRow(r++, new Label("Actividad:"), cbActividad);
        ficha.addRow(r++, new Label("Precio:"), tfPrecio);
        ficha.addRow(r++, new Label("Duración (días):"), tfDuracion);
        ficha.addRow(r++, new Label("Estado Pago:"), cbPago);
        ficha.addRow(r++, new Label("Fecha Pago:"), dpFechaPago);
        ficha.add(btnRegistrarPago, 1, r++);
        GridPane.setMargin(btnRegistrarPago, new Insets(5, 0, 10, 0));
        ficha.addRow(r++, new Label("Días Activos:"), tfDiasActivos);
        ficha.addRow(r++, new Label("Rutina:"), cbRutina);
        ficha.add(btnEnviarRutina, 1, r++);
        GridPane.setMargin(btnEnviarRutina, new Insets(5, 0, 10, 0));
        //Botones secundarios
        HBox botonesFinales = new HBox(10, btnGuardar, btnBorrar);
        ficha.add(botonesFinales, 1, r);
        GridPane.setMargin(botonesFinales, new Insets(10, 0, 0, 0));

        // --- FUNCIONES ---
        Runnable actualizarDias = () -> {
            if ("Pagó".equals(cbPago.getValue()) && dpFechaPago.getValue() != null) {
                int duracion = tfDuracion.getText().isEmpty() ? 30 : Integer.parseInt(tfDuracion.getText());
                long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dpFechaPago.getValue().plusDays(duracion));
                tfDiasActivos.setText(dias + " días restantes");
            } else tfDiasActivos.setText("Sin cuota activa");
        };
        cbPago.setOnAction(e -> actualizarDias.run());
        dpFechaPago.setOnAction(e -> actualizarDias.run());

        listaClientes.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> btnEditar.setDisable(sel == null));

        Runnable validar = () -> {
            // Nombre y apellido: obligatorios y solo letras (con acentos y espacios)
            if (tfNombre.getText().isBlank() || tfApellido.getText().isBlank())
                throw new RuntimeException("Nombre y apellido obligatorios");
            if (!tfNombre.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+"))
                throw new RuntimeException("El nombre solo puede contener letras y espacios");
            if (!tfApellido.getText().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+"))
                throw new RuntimeException("El apellido solo puede contener letras y espacios");
            if (tfNombre.getText().length() < 2 || tfApellido.getText().length() < 2)
                throw new RuntimeException("El nombre y apellido deben tener al menos 2 letras");


// --- Limitar formato DNI (solo números, máx 8 dígitos) ---
            tfDNI.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tfDNI.setText(newValue.replaceAll("[^\\d]", "")); // borra letras o símbolos
                }
                if (tfDNI.getText().length() > 8) {
                    tfDNI.setText(tfDNI.getText().substring(0, 8)); // limita a 8 dígitos
                }
            });

// --- Limitar formato CELULAR (solo números, máx 12 dígitos) ---
            tfCelular.textProperty().addListener((obs, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tfCelular.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (tfCelular.getText().length() > 12) {
                    tfCelular.setText(tfCelular.getText().substring(0, 12));
                }
            });

            // Validar que no exista otro cliente con el mismo DNI (solo al registrar nuevo)
            if ("Registrar Cliente".equals(btnGuardar.getText())) {
                boolean existe = clientes.stream()
                        .anyMatch(c -> c.getDni().equals(tfDNI.getText()));
                if (existe)
                    throw new RuntimeException("Ya existe un cliente registrado con ese DNI");
            }

            // Correo: opcional, pero si se completa debe tener formato válido
            if (!tfCorreo.getText().isBlank() && !tfCorreo.getText().matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
                throw new RuntimeException("El correo electrónico no tiene un formato válido");

            // Actividad obligatoria
            if (cbActividad.getValue() == null)
                throw new RuntimeException("Seleccioná una actividad");
        };


// --- NUEVO CLIENTE ---
        btnNuevo.setOnAction(e -> {
            ficha.setVisible(true);
            btnGuardar.setText("Registrar Cliente");
            tfNombre.clear(); tfApellido.clear(); tfDNI.clear(); tfCelular.clear(); tfCorreo.clear();
            dpFechaNac.setValue(null); tfEdad.clear();
            cbActividad.setValue(null); tfPrecio.clear(); tfDuracion.clear();
            cbPago.setValue(null); dpFechaPago.setValue(LocalDate.now());
            tfDiasActivos.clear(); cbRutina.setValue(null);
        });

// --- CALCULAR EDAD AUTOMÁTICAMENTE ---
        dpFechaNac.setOnAction(ev -> {
            if (dpFechaNac.getValue() != null) {
                int edad = Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears();
                tfEdad.setText(String.valueOf(edad));
            } else {
                tfEdad.clear();
            }
        });

// --- EDITAR CLIENTE ---
        btnEditar.setOnAction(e -> {
            Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ficha.setVisible(true);
                btnGuardar.setText("Guardar Cambios");

                tfNombre.setText(sel.getNombre());
                tfApellido.setText(sel.getApellido());
                tfDNI.setText(sel.getDni());
                tfCelular.setText(sel.getCelular());
                tfCorreo.setText(sel.getCorreo());
                dpFechaNac.setValue(sel.getFechaNac());

                // Calcular edad actualizada al abrir
                if (sel.getFechaNac() != null) {
                    int edad = Period.between(sel.getFechaNac(), LocalDate.now()).getYears();
                    tfEdad.setText(String.valueOf(edad));
                } else {
                    tfEdad.clear();
                }

                cbActividad.setValue(cbActividad.getItems().stream()
                        .filter(a -> a.getNombre().equals(sel.getActividad()))
                        .findFirst().orElse(null));

                tfPrecio.setText(sel.getPrecio());
                tfDuracion.setText(String.valueOf(sel.getDuracion()));
                cbPago.setValue(sel.getPago());
                dpFechaPago.setValue(sel.getFechaPago() != null ? sel.getFechaPago() : LocalDate.now());
                tfDiasActivos.setText(sel.getDiasActivos());
                cbRutina.setValue(cbRutina.getItems().stream()
                        .filter(rut -> rut.getNombre().equals(sel.getRutina()))
                        .findFirst().orElse(null));
            }
        });

        // --- GUARDAR CLIENTE ---
        btnGuardar.setOnAction(e -> {
            try {
                validar.run(); // valida campos obligatorios

                if ("Registrar Cliente".equals(btnGuardar.getText())) {
                    ActividadesController.Actividad actSel = cbActividad.getValue();
                    String actNombre = actSel != null ? actSel.getNombre() : "";
                    String precio = actSel != null ? String.valueOf(actSel.getPrecio()) : tfPrecio.getText();
                    int duracion = actSel != null ? actSel.getDuracion() : Integer.parseInt(tfDuracion.getText().isEmpty() ? "30" : tfDuracion.getText());

                    Cliente nuevo = new Cliente(tfNombre.getText(), tfApellido.getText(), tfDNI.getText());
                    nuevo.setCelular(tfCelular.getText());
                    nuevo.setCorreo(tfCorreo.getText());
                    nuevo.setFechaNac(dpFechaNac.getValue());
                    nuevo.setEdad(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears());
                    nuevo.setActividad(actNombre);
                    nuevo.setPrecio(precio);
                    nuevo.setDuracion(duracion);
                    nuevo.setPago(cbPago.getValue());
                    nuevo.setFechaPago(dpFechaPago.getValue());
                    nuevo.setDiasActivos(cbPago.getValue() != null && cbPago.getValue().equals("Pagó") ?
                            java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dpFechaPago.getValue().plusDays(duracion)) + " días restantes"
                            : "Sin cuota activa");
                    String rutNombre = cbRutina.getValue() != null ? cbRutina.getValue().getNombre() : null;
                    nuevo.setRutina(rutNombre);

                    ClienteDAO.insertarCliente(nuevo);
                    clientes.add(nuevo);
                    new Alert(Alert.AlertType.INFORMATION, "Cliente registrado!").showAndWait();

                } else { // Editar cliente
                    Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        sel.setNombre(tfNombre.getText());
                        sel.setApellido(tfApellido.getText());
                        sel.setCelular(tfCelular.getText());
                        sel.setCorreo(tfCorreo.getText());
                        sel.setFechaNac(dpFechaNac.getValue());
                        sel.setEdad(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears());
                        sel.setActividad(cbActividad.getValue() != null ? cbActividad.getValue().getNombre() : sel.getActividad());
                        sel.setPrecio(tfPrecio.getText());
                        sel.setDuracion(tfDuracion.getText().isEmpty() ? sel.getDuracion() : Integer.parseInt(tfDuracion.getText()));
                        sel.setPago(cbPago.getValue());
                        sel.setFechaPago(dpFechaPago.getValue());
                        sel.setDiasActivos(cbPago.getValue() != null && cbPago.getValue().equals("Pagó") ?
                                java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dpFechaPago.getValue().plusDays(sel.getDuracion())) + " días restantes"
                                : "Sin cuota activa");
                        sel.setRutina(cbRutina.getValue() != null ? cbRutina.getValue().getNombre() : sel.getRutina());

                        ClienteDAO.actualizarCliente(sel);
                        listaClientes.refresh();
                        new Alert(Alert.AlertType.INFORMATION, "Cambios guardados!").showAndWait();
                    }
                }

                ficha.setVisible(false);

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });


// --- REGISTRAR PAGO MANUAL ---
        btnRegistrarPago.setOnAction(e -> {
            try {
                // Validar que haya datos mínimos
                if (tfNombre.getText().isBlank() || tfApellido.getText().isBlank() || tfDNI.getText().isBlank()) {
                    new Alert(Alert.AlertType.WARNING, "Completá al menos nombre, apellido y DNI antes de registrar el pago.").showAndWait();
                    return;
                }

                // Buscar si el cliente ya existe en la lista
                Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
                Cliente existente = clientes.stream()
                        .filter(c -> c.getDni().equals(tfDNI.getText()))
                        .findFirst().orElse(null);

                // Si no existe, confirmar creación automática
                if (sel == null && existente == null) {
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "El cliente no está registrado.\n¿Deseás crear el cliente y registrar su pago?",
                            ButtonType.YES, ButtonType.NO);
                    confirm.setHeaderText("Confirmar creación de cliente");
                    confirm.showAndWait();
                    if (confirm.getResult() != ButtonType.YES) return;
                }

                // --- Datos de la ficha ---
                String estadoPago = cbPago.getValue() != null ? cbPago.getValue() : "Adeuda";
                LocalDate fechaPago = dpFechaPago.getValue() != null ? dpFechaPago.getValue() : LocalDate.now();
                ActividadesController.Actividad act = cbActividad.getValue();

                String actividad = act != null ? act.getNombre() : (sel != null ? sel.getActividad() : "");
                double monto = act != null ? act.getPrecio() :
                        (!tfPrecio.getText().isBlank() ? Double.parseDouble(tfPrecio.getText()) : 0);

                int duracion = act != null ? act.getDuracion() :
                        (!tfDuracion.getText().isBlank() ? Integer.parseInt(tfDuracion.getText()) : 30);

                // --- Crear o actualizar cliente ---
                if (sel == null && existente == null) {
                    Cliente nuevo = new Cliente(tfNombre.getText(), tfApellido.getText(), tfDNI.getText());
                    nuevo.setCelular(tfCelular.getText());
                    nuevo.setCorreo(tfCorreo.getText());
                    nuevo.setFechaNac(dpFechaNac.getValue());
                    if (dpFechaNac.getValue() != null)
                        nuevo.setEdad(Period.between(dpFechaNac.getValue(), LocalDate.now()).getYears());
                    nuevo.setActividad(actividad);
                    nuevo.setPrecio(String.valueOf(monto));
                    nuevo.setDuracion(duracion);
                    nuevo.setPago(estadoPago);
                    nuevo.setFechaPago(fechaPago);
                    if ("Pagó".equalsIgnoreCase(estadoPago)) {
                        long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaPago.plusDays(duracion));
                        nuevo.setDiasActivos(dias + " días restantes");
                    } else {
                        nuevo.setDiasActivos("Sin cuota activa");
                    }
                    nuevo.setRutina(cbRutina.getValue() != null ? cbRutina.getValue().getNombre() : null);

                    ClienteDAO.insertarCliente(nuevo);
                    clientes.add(nuevo);
                    sel = nuevo; // lo usamos para registrar el pago
                } else {
                    if (sel == null) sel = existente; // si lo seleccionamos recién creado
                    sel.setActividad(actividad);
                    sel.setPrecio(String.valueOf(monto));
                    sel.setDuracion(duracion);
                    sel.setPago(estadoPago);
                    sel.setFechaPago(fechaPago);
                    if ("Pagó".equalsIgnoreCase(estadoPago)) {
                        long dias = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaPago.plusDays(duracion));
                        sel.setDiasActivos(dias + " días restantes");
                    } else {
                        sel.setDiasActivos("Sin cuota activa");
                    }
                    ClienteDAO.actualizarCliente(sel);
                }

                listaClientes.refresh();

                // --- Crear o actualizar pago ---
                PagosController.Pago pago = new PagosController.Pago(
                        sel.getNombre(),
                        sel.getApellido(),
                        sel.getDni(),
                        fechaPago,
                        actividad,
                        monto,
                        estadoPago
                );

                boolean actualizado = PagoDAO.insertarPagoYDetectar(pago);

                if (actualizado) {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Se actualizó el pago existente de " + sel.getNombre() + " " + sel.getApellido()).showAndWait();
                } else {
                    new Alert(Alert.AlertType.INFORMATION,
                            "Pago registrado exitosamente para " + sel.getNombre() + " " + sel.getApellido()).showAndWait();
                }

                ficha.setVisible(false);

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error al registrar pago: " + ex.getMessage()).showAndWait();
            }
        });





        // --- BORRAR CLIENTE ---
        btnBorrar.setOnAction(e -> {
            Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ClienteDAO.borrarCliente(sel.getDni());
                clientes.remove(sel);
                new Alert(Alert.AlertType.INFORMATION, "Cliente eliminado!").showAndWait();
                ficha.setVisible(false);
            }
        });

        // --- LAYOUT ---
        VBox listaVBox = new VBox(10, btnNuevo, btnEditar, new Label("Buscar:"), tfBuscar, listaClientes);
        listaVBox.setPadding(new Insets(10));
        ScrollPane spFicha = new ScrollPane(ficha);
        spFicha.setFitToWidth(true);
        HBox root = new HBox(10, listaVBox, spFicha);
        HBox topBox = new HBox(10, btnVolver); topBox.setPadding(new Insets(10));
        VBox main = new VBox(topBox, root);

        Scene scene = new Scene(main, 1000, 600);
        scene.getStylesheets().add(ClientesController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }

    private static Button crearBotonConIcono(String rutaIcono) {
        Button btn = new Button();
        btn.getStyleClass().add("button-principal");
        ImageView icon = new ImageView(new Image(ClientesController.class.getResourceAsStream(rutaIcono)));
        icon.setFitWidth(16); icon.setFitHeight(16);
        btn.setGraphic(icon);
        return btn;
    }
}


