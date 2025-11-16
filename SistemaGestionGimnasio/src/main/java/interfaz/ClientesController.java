package interfaz;

import utils.AlertUtils;
import control.ActividadDAO;
import control.ClienteDAO;
import control.PagoDAO;
import control.RutinaDAO;
import entidad.Rutinas;
import entidad.Sesion;
import entidad.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import utils.PDFRutinaUtils;
import utils.VentanaUtils;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    private static String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return texto;
        return Arrays.stream(texto.trim().toLowerCase().split(" "))
                .filter(p -> !p.isBlank())
                .map(p -> Character.toUpperCase(p.charAt(0)) + p.substring(1))
                .collect(Collectors.joining(" "));
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
                Usuario usuario = Sesion.getUsuarioActual();

                // Reutilizamos el Stage actual del módulo
                Stage stageActual = (Stage) btnVolver.getScene().getWindow();

                GestionGimnasioFX menu = new GestionGimnasioFX();
                menu.mostrarVentanaPrincipal(stageActual, usuario); // <-- pasamos el Stage actual

                // NO necesitamos stage.close(); porque estamos reutilizando
                // stageActual.setScene(...) dentro de mostrarVentanaPrincipal
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

        // Nombre solo letras
        tfNombre.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]*")) {
                tfNombre.setText(oldV);
            }
        });

// Apellido solo letras
        tfApellido.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]*")) {
                tfApellido.setText(oldV);
            }
        });

// DNI números (máx 8)
        tfDNI.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) {
                tfDNI.setText(newV.replaceAll("[^\\d]", ""));
            }
            if (tfDNI.getText().length() > 8) {
                tfDNI.setText(tfDNI.getText().substring(0, 8));
            }
        });

// CELULAR números (máx 12)
        tfCelular.textProperty().addListener((obs, oldV, newV) -> {
            if (!newV.matches("\\d*")) {
                tfCelular.setText(newV.replaceAll("[^\\d]", ""));
            }
            if (tfCelular.getText().length() > 12) {
                tfCelular.setText(tfCelular.getText().substring(0, 12));
            }
        });

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

        // --- VALIDACIONES ---
        Runnable validar = () -> {

            if (tfNombre.getText().isBlank() || tfApellido.getText().isBlank())
                throw new RuntimeException("Nombre y apellido obligatorios");

            if (tfNombre.getText().length() < 2 || tfApellido.getText().length() < 2)
                throw new RuntimeException("El nombre y apellido deben tener al menos 2 letras");

            // DNIs válidos
            if (tfDNI.getText().length() != 8)
                throw new RuntimeException("El DNI debe tener 8 dígitos");

            // DNI repetido
            if ("Registrar Cliente".equals(btnGuardar.getText())) {
                boolean existe = clientes.stream()
                        .anyMatch(c -> c.getDni().equals(tfDNI.getText()));
                if (existe)
                    throw new RuntimeException("Ya existe un cliente registrado con ese DNI");
            }

            // Mail
            if (!tfCorreo.getText().isBlank() &&
                    !tfCorreo.getText().matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"))
                throw new RuntimeException("El correo electrónico no tiene un formato válido");

            // Actividad
            if (cbActividad.getValue() == null)
                throw new RuntimeException("Seleccioná una actividad");
        };
        // Fecha de nacimiento no puede ser futura
        if (dpFechaNac.getValue() != null && dpFechaNac.getValue().isAfter(LocalDate.now())) {
            throw new RuntimeException("La fecha de nacimiento no puede ser futura");
        }


// --- NUEVO CLIENTE ---
        btnNuevo.setOnAction(e -> {
            listaClientes.getSelectionModel().clearSelection();
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

                if (dpFechaNac.getValue().isAfter(LocalDate.now())) {
                    AlertUtils.mostrar(Alert.AlertType.ERROR, "Fecha inválida",
                            "La fecha de nacimiento no puede ser futura.");

                    dpFechaNac.setValue(null);
                    tfEdad.clear();
                    return;
                }

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

                    Cliente nuevo = new Cliente(
                            capitalizar(tfNombre.getText()),
                            capitalizar(tfApellido.getText()),
                            tfDNI.getText()
                    );
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
                    AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Éxito", "Cliente registrado correctamente!");

                } else { // Editar cliente
                    Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
                    if (sel != null) {
                        sel.setNombre(capitalizar(tfNombre.getText()));
                        sel.setApellido(capitalizar(tfApellido.getText()));
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
                        AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Éxito", "Cambios guardados correctamente!");
                    }
                }

                ficha.setVisible(false);

            } catch (RuntimeException ex) {
                // Errores de validación → ALERTA ESTILIZADA
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", ex.getMessage());

            } catch (Exception ex) {
                // Errores inesperados (BD, NullPointer, etc.)
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error inesperado", "Ocurrió un problema: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


// REGISTRAR PAGO MANUAL
        btnRegistrarPago.setOnAction(e -> {
            try {
                // Validar datos mínimos
                if (tfNombre.getText().isBlank() || tfApellido.getText().isBlank() || tfDNI.getText().isBlank()) {
                    AlertUtils.mostrar(Alert.AlertType.WARNING, "Datos incompletos",
                            "Completá al menos nombre, apellido y DNI antes de registrar el pago.");
                    return;
                }

                // Normalizar nombre y apellido
                String nombreCap = capitalizar(tfNombre.getText());
                String apellidoCap = capitalizar(tfApellido.getText());
                tfNombre.setText(nombreCap);
                tfApellido.setText(apellidoCap);

                // Buscar si el cliente ya existe
                Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
                Cliente existente = clientes.stream()
                        .filter(c -> c.getDni().equals(tfDNI.getText()))
                        .findFirst().orElse(null);

                // Si no existe, confirmar creación automática
                if (sel == null && existente == null) {
                    AlertUtils.mostrar(Alert.AlertType.CONFIRMATION, "Confirmar creación de cliente",
                            "El cliente no está registrado.\n¿Deseás crear el cliente y registrar su pago?");
                    if (AlertUtils.crear(Alert.AlertType.CONFIRMATION, "", "").getResult() != ButtonType.YES) return;
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
                    Cliente nuevo = new Cliente(nombreCap, apellidoCap, tfDNI.getText());
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
                    sel = nuevo; // usar para registrar el pago
                } else {
                    if (sel == null) sel = existente;
                    sel.setNombre(nombreCap);
                    sel.setApellido(apellidoCap);
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

                // --- Crear el pago ---
                PagosController.Pago pago = new PagosController.Pago(
                        sel.getNombre(),
                        sel.getApellido(),
                        sel.getDni(),
                        fechaPago,
                        actividad,
                        monto,
                        estadoPago
                );

                // --- NUEVA LÓGICA: detectar pago existente en misma fecha ---
                if (PagoDAO.existePagoEnFecha(sel.getDni(), fechaPago)) {
                    Alert decision = AlertUtils.crear(Alert.AlertType.CONFIRMATION, "Pago existente",
                            "Ya hay un pago registrado el " + fechaPago + " para " + sel.getNombre() + " " + sel.getApellido() +
                                    "\n¿Querés actualizar el pago existente (por cambio de actividad/monto)?\n\n" +
                                    "Sí = Actualiza el pago existente\nNo = Crea un nuevo registro");
                    decision.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                    decision.showAndWait();

                    if (decision.getResult() == ButtonType.YES) {
                        PagoDAO.actualizarPagoExistente(pago);
                        AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Pago actualizado", "Pago actualizado correctamente.");
                    } else if (decision.getResult() == ButtonType.NO) {
                        PagoDAO.insertarPago(pago);
                        AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Nuevo registro", "Se creó un nuevo registro de pago.");
                    } else {
                        return; // cancelado
                    }
                } else {
                    PagoDAO.insertarPago(pago);
                    AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Pago registrado",
                            "Pago registrado exitosamente para " + sel.getNombre() + " " + sel.getApellido());
                }

                ficha.setVisible(false);

            } catch (Exception ex) {
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", "Error al registrar pago: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        // --- BORRAR CLIENTE ---
        btnBorrar.setOnAction(e -> {
            Cliente sel = listaClientes.getSelectionModel().getSelectedItem();
            if (sel != null) {
                ClienteDAO.borrarCliente(sel.getDni());
                clientes.remove(sel);
                AlertUtils.mostrar(Alert.AlertType.INFORMATION, "Cliente eliminado", "Cliente eliminado correctamente!");
                ficha.setVisible(false);
            }
        });

// --- ENVIAR RUTINA ---
        btnEnviarRutina.setOnAction(e -> {
            try {
                Cliente cliente = listaClientes.getSelectionModel().getSelectedItem();
                Rutinas rutina = cbRutina.getValue();

                if (cliente == null || rutina == null) {
                    AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", "Seleccioná un cliente y una rutina.");
                    return;
                }

                // 1) EXPORTAR EL PDF
                File pdf = PDFRutinaUtils.exportarPDF(rutina);

                if (pdf == null) {
                    AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", "Error al generar el PDF.");
                    return;
                }

                // 2) ARMAR MENSAJE AUTOMÁTICO
                String mensaje = "Hola " + cliente.getNombre() + "! Te paso la rutina en PDF. Adjunto archivo debajo \uD83D\uDE0A\uD83D\uDCAA\uD83C\uDFFC👇";
                String msgEncoded = URLEncoder.encode(mensaje, StandardCharsets.UTF_8);

                // 3) CELULAR DEL CLIENTE (sin +54)
                String numero = cliente.getCelular().replace("+", "").replace(" ", "");

                // 4) ABRIR WHATSAPP WEB
                String url = "https://wa.me/54" + numero + "?text=" + msgEncoded;
                Desktop.getDesktop().browse(new URI(url));

                // 5) ABRIR CARPETA DEL PDF PARA ADJUNTARLO AL TOQUE
                Desktop.getDesktop().open(pdf.getParentFile());

                // 6) NOTIFICACIÓN
                AlertUtils.mostrar(Alert.AlertType.INFORMATION, "WhatsApp Web",
                        "WhatsApp Web se abrió.\nAdjuntá el PDF desde la carpeta que se abrió.");

            } catch (Exception ex) {
                ex.printStackTrace();
                AlertUtils.mostrar(Alert.AlertType.ERROR, "Error", "No fue posible abrir WhatsApp.");
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
        VentanaUtils.centrar(stage);
        stage.setResizable(true);

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


