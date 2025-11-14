package interfaz;

import control.PagoDAO;
import entidad.Sesion;
import entidad.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.VentanaUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PagosController {

    public static class Pago {
        private String nombre;
        private String apellido;
        private String dni;
        private LocalDate fecha;
        private String actividad;
        private double monto;
        private String estado;

        public Pago(String nombre, String apellido, String dni, LocalDate fecha,
                    String actividad, double monto, String estado) {
            this.nombre = nombre;
            this.apellido = apellido;
            this.dni = dni;
            this.fecha = fecha;
            this.actividad = actividad;
            this.monto = monto;
            this.estado = estado;
        }

        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getDni() { return dni; }
        public LocalDate getFecha() { return fecha; }
        public String getActividad() { return actividad; }
        public double getMonto() { return monto; }
        public String getEstado() { return estado; }
    }

    public static void mostrarPagos(Stage stage) {
        ObservableList<Pago> pagos = PagoDAO.listarPagos();

        TableView<Pago> tablaPagos = new TableView<>();

        // Columna Cliente
        TableColumn<Pago, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getApellido() + ", " + c.getValue().getNombre())
        );


        // Comparador natural para ordenar bien (ignora mayúsculas y tildes)
        colCliente.setComparator((n1, n2) -> {
            if (n1 == null && n2 == null) return 0;
            if (n1 == null) return -1;
            if (n2 == null) return 1;
            java.text.Collator collator = java.text.Collator.getInstance(new java.util.Locale("es", "ES"));
            collator.setStrength(java.text.Collator.PRIMARY);
            return collator.compare(n1.trim(), n2.trim());
        });

        TableColumn<Pago, String> colDni = new TableColumn<>("DNI");
        colDni.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDni()));

        TableColumn<Pago, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        TableColumn<Pago, String> colActividad = new TableColumn<>("Actividad");
        colActividad.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getActividad()));

        TableColumn<Pago, String> colMonto = new TableColumn<>("Monto");
        colMonto.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty("$" + c.getValue().getMonto()));

        TableColumn<Pago, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEstado()));
        colEstado.setCellFactory(column -> new TableCell<Pago, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("Pagó")) {
                        setStyle("-fx-text-fill: green !important; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("Adeuda")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        tablaPagos.getColumns().addAll(colCliente, colDni, colFecha, colActividad, colMonto, colEstado);
        tablaPagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        FilteredList<Pago> pagosFiltrados = new FilteredList<>(pagos, p -> true);
        SortedList<Pago> pagosOrdenados = new SortedList<>(pagosFiltrados);
        pagosOrdenados.comparatorProperty().bind(tablaPagos.comparatorProperty());
        tablaPagos.setItems(pagosOrdenados);

        TextField tfBuscar = new TextField();
        tfBuscar.setPromptText("Buscar por nombre o DNI...");

        ComboBox<String> cbEstado = new ComboBox<>(FXCollections.observableArrayList("Todos", "Pagó", "Adeuda"));
        cbEstado.setValue("Todos");

        Label lblDesde = new Label("Desde:");
        DatePicker dpDesde = new DatePicker();
        Label lblHasta = new Label("Hasta:");
        DatePicker dpHasta = new DatePicker();

        Button btnFiltrar = new Button("Filtrar por Fecha");
        btnFiltrar.getStyleClass().add("button-secundario");
        Button btnOrdenAsc = new Button("Orden ASC");
        btnOrdenAsc.getStyleClass().add("button-secundario");
        Button btnOrdenDesc = new Button("Orden DESC");
        btnOrdenDesc.getStyleClass().add("button-secundario");
        Button btnReset = new Button("Limpiar Filtros");
        btnReset.getStyleClass().add("button-secundario");

        // ────────────────────────────────
        // ORDENAR POR NOMBRE ASC / DESC
        // ────────────────────────────────
        btnOrdenAsc.setOnAction(e -> {
            tablaPagos.getSortOrder().clear();
            colCliente.setSortType(TableColumn.SortType.ASCENDING);
            tablaPagos.getSortOrder().add(colCliente);
            tablaPagos.sort();
        });

        btnOrdenDesc.setOnAction(e -> {
            tablaPagos.getSortOrder().clear();
            colCliente.setSortType(TableColumn.SortType.DESCENDING);
            tablaPagos.getSortOrder().add(colCliente);
            tablaPagos.sort();
        });

        // ────────────────────────────────
        // LIMPIAR FILTROS
        // ────────────────────────────────
        btnReset.setOnAction(e -> {
            dpDesde.setValue(null);
            dpHasta.setValue(null);
            cbEstado.setValue("Todos");
            tfBuscar.clear();
            pagosFiltrados.setPredicate(p -> true);
            tablaPagos.getSortOrder().clear();
        });

        // ────────────────────────────────
        // BOTÓN VOLVER
        // ────────────────────────────────
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


        // ────────────────────────────────
        // LAYOUT FINAL
        // ────────────────────────────────
        HBox filtrosFecha = new HBox(10, lblDesde, dpDesde, lblHasta, dpHasta, btnFiltrar, btnOrdenAsc, btnOrdenDesc, btnReset);
        HBox filtrosEstado = new HBox(10, new Label("Estado:"), cbEstado);


        // ────────────────────────────────
// Labels para totales (debajo de la tabla)
        Label lblTotalRecaudado = new Label("Total recaudado: $0.00");
        Label lblTotalAdeudado = new Label("Total adeudado: $0.00");
        lblTotalRecaudado.getStyleClass().add("label-total"); // opcional: dale estilo en tu CSS
        lblTotalAdeudado.getStyleClass().add("label-total");

// Método para actualizar totales basado en los items actualmente visibles en la tabla
        Runnable actualizarTotales = () -> {
            double totalRecaudado = 0.0;
            double totalAdeudado = 0.0;

            // Usamos los items visibles de la tabla (ya vienen filtrados/ordenados)
            for (Pago p : tablaPagos.getItems()) {
                if (p.getEstado() != null) {
                    String est = p.getEstado().trim().toLowerCase();
                    if (est.equals("pagado") || est.equals("pagó") /*por si guardaron con tilde*/) {
                        totalRecaudado += p.getMonto();
                    } else if (est.equals("adeuda") || est.equals("deuda") || est.equals("adeudado")) {
                        totalAdeudado += p.getMonto();
                    }
                }
            }

            // Formateo sencillo a moneda (dos decimales). Si querés locale argentino, lo cambiamos.
            String recaudadoStr = String.format("$%.2f", totalRecaudado);
            String adeudadoStr = String.format("$%.2f", totalAdeudado);

            lblTotalRecaudado.setText("Total recaudado: " + recaudadoStr);
            lblTotalAdeudado.setText("Total adeudado: " + adeudadoStr);
        };

// ────────────────────────────────
// FILTROS Y BÚSQUEDAS (version actualizada para que también llame a actualizarTotales)
// ────────────────────────────────
        Runnable aplicarFiltros = () -> {
            String filtroTexto = tfBuscar.getText() == null ? "" : tfBuscar.getText().toLowerCase();
            String estadoSel = cbEstado.getValue();
            LocalDate desde = dpDesde.getValue();
            LocalDate hasta = dpHasta.getValue();

            pagosFiltrados.setPredicate(p -> {
                // Si filtro vacío, mostramos todo
                if (p == null) return false;

                // 🔹 Coincidencia por nombre, apellido o DNI
                boolean coincideTexto =
                        p.getNombre() != null && p.getNombre().toLowerCase().contains(filtroTexto) ||
                                p.getApellido() != null && p.getApellido().toLowerCase().contains(filtroTexto) ||
                                (p.getApellido() + " " + p.getNombre()).toLowerCase().contains(filtroTexto) ||
                                (p.getDni() != null && p.getDni().toLowerCase().contains(filtroTexto));

                // 🔹 Coincidencia por estado
                boolean coincideEstado = estadoSel == null || estadoSel.equals("Todos") || p.getEstado().equalsIgnoreCase(estadoSel);

                // 🔹 Coincidencia por fecha
                boolean coincideFecha = true;
                if (desde != null && hasta != null && p.getFecha() != null) {
                    coincideFecha = !p.getFecha().isBefore(desde) && !p.getFecha().isAfter(hasta);
                }

                return coincideTexto && coincideEstado && coincideFecha;
            });

            // después de aplicar predicate, actualizamos totales
            actualizarTotales.run();
        };

// eventos que disparan filtros y totales
        tfBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros.run());
        cbEstado.setOnAction(e -> aplicarFiltros.run());
        btnFiltrar.setOnAction(e -> aplicarFiltros.run());
        btnReset.setOnAction(e -> {
            dpDesde.setValue(null);
            dpHasta.setValue(null);
            cbEstado.setValue("Todos");
            tfBuscar.clear();
            pagosFiltrados.setPredicate(p -> true);
            tablaPagos.getSortOrder().clear();
            actualizarTotales.run();
        });

// Además, si la lista visible cambia por otras razones, mantenemos los totales sincronizados
        tablaPagos.getItems().addListener((javafx.collections.ListChangeListener<Pago>) change -> actualizarTotales.run());

// Llamada inicial para mostrar totales al abrir la ventana
        actualizarTotales.run();

// ────────────────────────────────
// Colocamos los labels en la interfaz (debajo de la tabla)
// ────────────────────────────────
        HBox totalsBox = new HBox(40, lblTotalRecaudado, lblTotalAdeudado);
        totalsBox.setPadding(new Insets(10, 20, 10, 20));
        totalsBox.setStyle("-fx-alignment: center-right; -fx-background-color: #303030; -fx-font-weight: bold;");

        VBox root = new VBox(10, btnVolver, tfBuscar, filtrosEstado, filtrosFecha, tablaPagos, totalsBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 950, 550);
        scene.getStylesheets().add(PagosController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
        VentanaUtils.centrar(stage);


        actualizarTotales.run();
    }
}






