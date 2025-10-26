package interfaz;

import control.PagoDAO;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PagosController {

    public static class Pago {
        private String clienteNombre;
        private String dni;
        private LocalDate fecha;
        private String actividad;
        private double monto;
        private String estado; // "Pagado" o "Adeuda"

        public Pago(String clienteNombre, String dni, LocalDate fecha, String actividad, double monto, String estado) {
            this.clienteNombre = clienteNombre;
            this.dni = dni;
            this.fecha = fecha;
            this.actividad = actividad;
            this.monto = monto;
            this.estado = estado;
        }

        public String getClienteNombre() { return clienteNombre; }
        public String getDni() { return dni; }
        public LocalDate getFecha() { return fecha; }
        public String getActividad() { return actividad; }
        public double getMonto() { return monto; }
        public String getEstado() { return estado; }
    }

    public static void mostrarPagos(Stage stage) {
        ObservableList<Pago> pagos = PagoDAO.listarPagos();

        TableView<Pago> tablaPagos = new TableView<>();

        // Columna Cliente — muestra “Apellido, Nombre”
        TableColumn<Pago, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(c -> {
            String nombreCompleto = c.getValue().getClienteNombre();
            if (nombreCompleto == null || nombreCompleto.isBlank()) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            String[] partes = nombreCompleto.trim().split(" ");
            if (partes.length >= 2) {
                String nombre = partes[0];
                String apellido = partes[partes.length - 1];
                return new javafx.beans.property.SimpleStringProperty(apellido + ", " + nombre);
            } else {
                return new javafx.beans.property.SimpleStringProperty(nombreCompleto);
            }
        });

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
                    if (item.equalsIgnoreCase("Pagado")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
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

        ComboBox<String> cbEstado = new ComboBox<>(FXCollections.observableArrayList("Todos", "Pagado", "Adeuda"));
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

        Runnable aplicarFiltros = () -> {
            String filtroTexto = tfBuscar.getText().toLowerCase();
            String estadoSel = cbEstado.getValue();
            LocalDate desde = dpDesde.getValue();
            LocalDate hasta = dpHasta.getValue();

            pagosFiltrados.setPredicate(p -> {
                boolean coincideTexto = p.getClienteNombre().toLowerCase().contains(filtroTexto)
                        || p.getDni().toLowerCase().contains(filtroTexto);
                boolean coincideEstado = estadoSel.equals("Todos") || p.getEstado().equalsIgnoreCase(estadoSel);
                boolean coincideFecha = true;
                if (desde != null && hasta != null) {
                    coincideFecha = !p.getFecha().isBefore(desde) && !p.getFecha().isAfter(hasta);
                }
                return coincideTexto && coincideEstado && coincideFecha;
            });
        };

        tfBuscar.textProperty().addListener((obs, old, nuevo) -> aplicarFiltros.run());
        cbEstado.setOnAction(e -> aplicarFiltros.run());
        btnFiltrar.setOnAction(e -> aplicarFiltros.run());

        // 🔹 Ordenar por nombre asc/desc
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

        btnReset.setOnAction(e -> {
            dpDesde.setValue(null);
            dpHasta.setValue(null);
            cbEstado.setValue("Todos");
            tfBuscar.clear();
            pagosFiltrados.setPredicate(p -> true);
            tablaPagos.getSortOrder().clear();
        });

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

        HBox filtrosFecha = new HBox(10, lblDesde, dpDesde, lblHasta, dpHasta, btnFiltrar, btnOrdenAsc, btnOrdenDesc, btnReset);
        HBox filtrosEstado = new HBox(10, new Label("Estado:"), cbEstado);
        VBox root = new VBox(10, btnVolver, tfBuscar, filtrosEstado, filtrosFecha, tablaPagos);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 950, 550);
        scene.getStylesheets().add(PagosController.class.getResource("/estilos.css").toExternalForm());
        stage.setScene(scene);
    }
}




