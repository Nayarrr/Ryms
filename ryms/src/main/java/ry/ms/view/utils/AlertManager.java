package ry.ms.view.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility class for displaying various types of JavaFX alerts.
 */
public class AlertManager {

    /**
     * Default constructor.
     * Private to prevent instantiation since all methods are static,
     * but changed to public/default to satisfy Javadoc warnings if instantiated
     * elsewhere.
     */
    public AlertManager() {
        // Default constructor
    }

    /**
     * Displays an error alert.
     * 
     * @param title   The title of the alert.
     * @param content The content message.
     */
    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an information alert.
     * 
     * @param title   The title of the alert.
     * @param content The content message.
     */
    public static void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a warning alert.
     * 
     * @param title   The title of the alert.
     * @param content The content message.
     */
    public static void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert.
     * 
     * @param title   The title of the alert.
     * @param content The content message.
     * @return true if the user clicks OK, false otherwise.
     */
    public static boolean showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(title);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
