package ry.ms.view.product;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import ry.ms.businessLogic.basket.SessionFacade;
import ry.ms.businessLogic.basket.models.BasketItem;
import ry.ms.businessLogic.product.models.Product;
import ry.ms.view.user.UserSession;

public class ShopController implements Initializable {

    @FXML
    private FlowPane productsGrid;
    @FXML
    private TableView<BasketDisplayItem> basketTable;
    @FXML
    private TableColumn<BasketDisplayItem, String> productCol;
    @FXML
    private TableColumn<BasketDisplayItem, Integer> qtyCol;
    @FXML
    private TableColumn<BasketDisplayItem, Double> priceCol;
    @FXML
    private TableColumn<BasketDisplayItem, Double> subtotalCol;
    @FXML
    private Label totalLabel;
    @FXML
    private Label statusLabel;

    private final ProductController productController = new ProductController();
    private final SessionFacade basketFacade = SessionFacade.getInstance();
    private final DecimalFormat priceFormat = new DecimalFormat("0.00 €");

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        setupBasketTable();
        loadProducts();
        refreshBasket();
    }

    private void setupBasketTable() {
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
    }

    private void loadProducts() {
        productsGrid.getChildren().clear();
        List<Product> products;
        try {
            products = productController.getAllProducts();
        } catch (RuntimeException ex) {
            setError("Impossible de charger les produits: " + ex.getMessage());
            return;
        }

        for (Product product : products) {
            productsGrid.getChildren().add(createProductCard(product));
        }
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefWidth(240);
        card.setStyle(
                "-fx-background-color: white; -fx-padding: 14; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #dfe6e9;");

        Label name = new Label(product.getName());
        name.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label category = new Label(product.getCategory() != null ? product.getCategory() : "");
        category.setStyle("-fx-text-fill: #7f8c8d;");

        Label description = new Label(product.getDescription() != null ? product.getDescription() : "");
        description.setWrapText(true);
        description.setStyle("-fx-text-fill: #636e72;");

        Label price = new Label(priceFormat.format(product.getPrice()));
        price.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        Label stock = new Label("Stock: " + product.getStock());
        stock.setStyle("-fx-text-fill: #7f8c8d;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("Ajouter au panier");
        addBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setMaxWidth(Double.MAX_VALUE);
        addBtn.setOnAction(e -> addToBasket(product));

        card.getChildren().addAll(name, category, description, price, stock, spacer, addBtn);
        return card;
    }

    private void addToBasket(Product product) {
        clearStatus();
        String email = UserSession.getInstance().getUserEmail();
        if (email == null || email.isBlank()) {
            setError("Vous devez être connecté pour ajouter au panier.");
            return;
        }
        try {
            basketFacade.addItem(email, product.getProductId(), 1);
            refreshBasket();
            setInfo("Produit ajouté au panier.");
        } catch (SQLException ex) {
            setError("Erreur lors de l'ajout au panier: " + ex.getMessage());
        } catch (RuntimeException ex) {
            setError(ex.getMessage());
        }
    }

    @FXML
    private void handleClearBasket() {
        clearStatus();
        String email = UserSession.getInstance().getUserEmail();
        if (email == null || email.isBlank()) {
            setError("Aucun utilisateur connecté.");
            return;
        }
        try {
            basketFacade.emptyBasket(email);
            refreshBasket();
        } catch (SQLException ex) {
            setError("Erreur lors du vidage du panier: " + ex.getMessage());
        }
    }

    @FXML
    private void handleCheckout() {
        clearStatus();
        if (basketTable.getItems().isEmpty()) {
            setError("Votre panier est vide.");
            return;
        }

        try {
            // Calculate total
            double total = basketTable.getItems().stream()
                    .mapToDouble(BasketDisplayItem::getSubtotal)
                    .sum();

            // Open Payment Window
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ry/ms/view/payment/fxml/Payment.fxml"));
            javafx.scene.Parent root = loader.load();

            ry.ms.view.payment.PaymentController controller = loader.getController();
            controller.setAmount(total);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Paiement Sécurisé");
            stage.setScene(new javafx.scene.Scene(root));

            controller.setOnSuccess(() -> {
                // Handle success: clear basket, close window, show message
                try {
                    String email = UserSession.getInstance().getUserEmail();
                    basketFacade.emptyBasket(email);
                    refreshBasket();
                    setInfo("Paiement effectué avec succès ! Merci de votre commande.");
                } catch (SQLException e) {
                    setError("Paiement réussi mais erreur lors du vidage du panier: " + e.getMessage());
                }
                stage.close();
            });

            controller.setOnCancel(() -> {
                stage.close();
            });

            stage.showAndWait();

        } catch (java.io.IOException e) {
            e.printStackTrace();
            setError("Impossible d'ouvrir la fenêtre de paiement: " + e.getMessage());
        }
    }

    private void refreshBasket() {
        String email = UserSession.getInstance().getUserEmail();
        if (email == null || email.isBlank()) {
            basketTable.getItems().clear();
            totalLabel.setText("0.00 €");
            return;
        }
        try {
            List<BasketItem> items = basketFacade.getBasketByUser(email);
            Map<Long, Product> productIndex = productController.getAllProducts()
                    .stream().collect(Collectors.toMap(Product::getProductId, p -> p, (a, b) -> a, HashMap::new));

            List<BasketDisplayItem> displayItems = items.stream()
                    .map(item -> toDisplayItem(item, productIndex.get(item.getProductId())))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            basketTable.getItems().setAll(displayItems);
            double total = displayItems.stream().mapToDouble(BasketDisplayItem::getSubtotal).sum();
            totalLabel.setText(priceFormat.format(total));
        } catch (SQLException ex) {
            setError("Erreur lors du chargement du panier: " + ex.getMessage());
        }
    }

    private BasketDisplayItem toDisplayItem(BasketItem item, Product product) {
        if (product == null) {
            return null;
        }
        double price = product.getPrice();
        double subtotal = price * item.getQuantity();
        return new BasketDisplayItem(product.getName(), item.getQuantity(), price, subtotal);
    }

    private void setError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #c0392b;");
        ry.ms.view.utils.AlertManager.showError("Erreur Boutique", message);
    }

    private void setInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }

    private void clearStatus() {
        statusLabel.setText("");
    }

    public static class BasketDisplayItem {
        private final String productName;
        private final int quantity;
        private final double price;
        private final double subtotal;

        public BasketDisplayItem(String productName, int quantity, double price, double subtotal) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = subtotal;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }

        public double getSubtotal() {
            return subtotal;
        }
    }
}
