package ry.ms.view.product;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ry.ms.businessLogic.product.models.Product;

/**
 * Controller for the Create Product view (Shop).
 */
public class CreateProductController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField stockField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button cancelButton;

    private final ProductController controller = new ProductController();

    @FXML
    private void handleSave() {
        String name = nameField.getText();
        String description = descriptionField.getText();
        String category = categoryField.getText();

        double price;
        int stock;
        try {
            price = Double.parseDouble(priceField.getText());
            stock = Integer.parseInt(stockField.getText());
        } catch (NumberFormatException ex) {
            errorLabel.setText("Prix/stock doivent être numériques.");
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            errorLabel.setText("Le nom est obligatoire.");
            return;
        }
        if (price <= 0) {
            errorLabel.setText("Le prix doit être positif.");
            return;
        }
        if (stock < 0) {
            errorLabel.setText("Le stock ne peut pas être négatif.");
            return;
        }

        Product product = new Product(null, name.trim(), description, price, stock, category);
        try {
            controller.createProduct(product);
            closeWindow();
        } catch (RuntimeException ex) {
            errorLabel.setText("Erreur : " + ex.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
