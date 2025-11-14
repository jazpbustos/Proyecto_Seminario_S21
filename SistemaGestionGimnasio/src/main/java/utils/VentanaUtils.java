package utils;

import javafx.stage.Stage;

public class VentanaUtils {

    public static void centrar(Stage stage) {
        stage.centerOnScreen();
        stage.setOnShown(e -> stage.centerOnScreen());
    }
}
