package utils;

import javafx.scene.control.Alert;

public class AlertUtils {

    public static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Aplicar estilos globales
        alert.getDialogPane().getStylesheets().add(
                AlertUtils.class.getResource("/estilos.css").toExternalForm()
        );

        alert.showAndWait();
    }

    public static Alert crear(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.getDialogPane().getStylesheets().add(
                AlertUtils.class.getResource("/estilos.css").toExternalForm()
        );

        return alert;
    }
}
