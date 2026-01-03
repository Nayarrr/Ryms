package ry.ms.view.product;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import ry.ms.businessLogic.product.SessionFacade;
import ry.ms.businessLogic.product.models.Product;

public class ProductController {

    private final SessionFacade sessionFacade;

    public ProductController() {
        this(SessionFacade.getSessionFactory());
    }

    public ProductController(SessionFacade sessionFacade) {
        this.sessionFacade = Objects.requireNonNull(sessionFacade);
    }

    public Product createProduct(Product product) {
        try {
            return sessionFacade.createProduct(product);
        } catch (SQLException ex) {
            return handleSqlException("Failed to create product", ex);
        }
    }

    public void editProduct(Product product) {
        runWithHandling(() -> sessionFacade.editProduct(product),
                "Failed to update product");
    }

    public void deleteProduct(Long id) {
        runWithHandling(() -> sessionFacade.deleteProduct(id),
                "Failed to delete product");
    }

    public Product getProductById(Long id) {
        try {
            return sessionFacade.getProductById(id);
        } catch (SQLException ex) {
            return handleSqlException("Failed to fetch product", ex);
        }
    }

    public List<Product> getAllProducts() {
        try {
            return sessionFacade.getAllProducts();
        } catch (SQLException ex) {
            return handleSqlException("Failed to load products", ex);
        }
    }

    private void runWithHandling(SqlRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (SQLException ex) {
            handleSqlException(message, ex);
        }
    }

    private <T> T handleSqlException(String context, SQLException ex) {
        System.err.println(context + ": " + ex.getMessage());
        throw new RuntimeException(context, ex);
    }

    @FunctionalInterface
    private interface SqlRunnable {
        void run() throws SQLException;
    }
}
