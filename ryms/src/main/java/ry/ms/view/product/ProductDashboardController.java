package ry.ms.view.product;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.businessLogic.product.models.Product;
import ry.ms.view.product.EditProductController;

public class ProductDashboardController implements Initializable {

    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private Label messageLabel;
    @FXML private Button deleteButton;
    @FXML private Button editButton;

    private final ProductController controller = new ProductController();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupSelectionListener();
        loadProducts();
    }

    private void setupColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void loadProducts() {
        try {
            List<Product> products = controller.getAllProducts();
            ObservableList<Product> items = FXCollections.observableArrayList(products);
            productsTable.setItems(items);
            messageLabel.setText("");
            productsTable.getSelectionModel().clearSelection();
            updateActionButtons(null);
        } catch (RuntimeException ex) {
            messageLabel.setText("Erreur chargement: " + ex.getMessage());
        }
    }

    private void setupSelectionListener() {
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> updateActionButtons(newSel));
    }

    private void updateActionButtons(Product selected) {
        boolean hasSelection = selected != null;
        if (deleteButton != null) {
            deleteButton.setDisable(!hasSelection);
        }
        if (editButton != null) {
            editButton.setDisable(!hasSelection);
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/product/fxml/CreateProduct.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Créer un produit");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadProducts());
            stage.showAndWait();
        } catch (Exception ex) {
            messageLabel.setText("Erreur ouverture: " + ex.getMessage());
        }
    }

    @FXML
    private void handleEditProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Sélectionnez un produit à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/product/fxml/EditProduct.fxml"));
            Parent root = loader.load();
            EditProductController editController = loader.getController();
            editController.initData(selected);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le produit");
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> loadProducts());
            stage.showAndWait();
        } catch (Exception ex) {
            messageLabel.setText("Erreur ouverture: " + ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Sélectionnez un produit à supprimer.");
            return;
        }
        try {
            controller.deleteProduct(selected.getProductId());
            messageLabel.setText("Produit supprimé.");
            loadProducts();
        } catch (RuntimeException ex) {
            messageLabel.setText("Erreur suppression: " + ex.getMessage());
        }
    }
}
