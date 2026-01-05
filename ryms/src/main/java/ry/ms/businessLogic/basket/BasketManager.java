package ry.ms.businessLogic.basket;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.basket.models.BasketItem;
import ry.ms.businessLogic.product.models.Product;
import ry.ms.persistLogic.basket.dao.BasketDAO;
import ry.ms.persistLogic.product.dao.ProductDAO;

public class BasketManager {

    private final BasketDAO basketDAO;
    private final ProductDAO productDAO;

    public BasketManager(AbsFactory factory) {
        Objects.requireNonNull(factory, "Factory must not be null.");
        this.basketDAO = factory.createBasketDAO();
        this.productDAO = factory.createProductDAO();
    }

    public void addItem(String userEmail, Long productId, int quantity) throws SQLException {
        validateUser(userEmail);
        validateQuantity(quantity);

        Product product = requireProduct(productId);

        BasketItem existing = basketDAO.getItem(userEmail.trim(), productId);
        int totalQuantity = (existing == null ? 0 : existing.getQuantity()) + quantity;

        ensureStock(product, totalQuantity);

        BasketItem item = new BasketItem(userEmail.trim(), productId, totalQuantity, new Timestamp(System.currentTimeMillis()));
        basketDAO.saveItem(item);
    }

    public void updateQuantity(String userEmail, Long productId, int quantity) throws SQLException {
        validateUser(userEmail);
        validateQuantity(quantity);

        Product product = requireProduct(productId);
        ensureStock(product, quantity);

        basketDAO.updateQuantity(userEmail.trim(), productId, quantity);
    }

    public void removeItem(String userEmail, Long productId) throws SQLException {
        validateUser(userEmail);
        requireProduct(productId);
        basketDAO.deleteItem(userEmail.trim(), productId);
    }

    public void emptyBasket(String userEmail) throws SQLException {
        validateUser(userEmail);
        basketDAO.clearBasket(userEmail.trim());
    }

    public List<BasketItem> getBasketByUser(String userEmail) throws SQLException {
        validateUser(userEmail);
        return basketDAO.getBasketByUser(userEmail.trim());
    }

    private Product requireProduct(Long productId) throws SQLException {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required.");
        }
        Product product = productDAO.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }
        return product;
    }

    private void ensureStock(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
        }
    }

    private void validateUser(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required.");
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
    }
}
