package ry.ms;

import junit.framework.TestCase;
import ry.ms.businessLogic.basket.BasketManager;
import ry.ms.businessLogic.basket.models.BasketItem;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.basket.postgres.BasketDAOPostgres;
import ry.ms.persistLogic.product.dao.ProductDAO;
import ry.ms.persistLogic.product.postgres.ProductDAOPostgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Integration test for the Basket Management Use Case.
 */
public class BasketUseCaseTest extends TestCase {

    private static final String USER_EMAIL = "basket_junit@test.com";
    private static final String USER_NAME = "BasketJUnit";
    private static final String USER_PASSWORD = "pass";
    private static final String PRODUCT_NAME = "JUnit_Basket_Product";

    public void testBasketScenario() throws Exception {
        ensureBasketTable();
        cleanData();
        Long productId = createProduct();
        createUser();

        BasketManager basketManager = new BasketManager(new PostgresFactory());
        BasketDAOPostgres basketDAO = new BasketDAOPostgres();

        try {
            basketManager.addItem(USER_EMAIL, productId, 2);

            List<BasketItem> items = basketDAO.getBasketByUser(USER_EMAIL);
            assertFalse("Basket should contain items after add", items.isEmpty());
            BasketItem first = items.get(0);
            assertEquals("Quantity should match added amount", 2, first.getQuantity());
            assertEquals("Product id should match", productId, first.getProductId());

            basketManager.updateQuantity(USER_EMAIL, productId, 3);
            BasketItem updated = basketDAO.getItem(USER_EMAIL, productId);
            assertNotNull("Item should exist after update", updated);
            assertEquals("Updated quantity should match", 3, updated.getQuantity());

            basketManager.removeItem(USER_EMAIL, productId);
            List<BasketItem> afterRemove = basketDAO.getBasketByUser(USER_EMAIL);
            assertTrue("Basket should be empty after removal", afterRemove.isEmpty());
        } finally {
            cleanData();
        }
    }

    private void createUser() throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO users (email, username, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, USER_EMAIL);
            stmt.setString(2, USER_NAME);
            stmt.setString(3, USER_PASSWORD);
            stmt.executeUpdate();
        }
    }

    private Long createProduct() throws SQLException {
        ProductDAO productDAO = new ProductDAOPostgres();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO products (name, description, price, stock, category) VALUES (?, ?, ?, ?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, PRODUCT_NAME);
            stmt.setString(2, "JUnit basket product");
            stmt.setDouble(3, 10.0);
            stmt.setInt(4, 10);
            stmt.setString(5, "TEST");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        // Fallback to DAO query if needed
        return productDAO.getAllProducts().stream()
            .filter(p -> PRODUCT_NAME.equals(p.getName()))
            .map(p -> p.getProductId())
            .findFirst()
            .orElseThrow(() -> new SQLException("Failed to create product for test"));
    }

    private void ensureBasketTable() throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS basket_items (" +
                "user_email VARCHAR(255) REFERENCES users(email) ON DELETE CASCADE," +
                "product_id INT REFERENCES products(product_id) ON DELETE CASCADE," +
                "quantity INT NOT NULL," +
                "added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY (user_email, product_id))");
        }
    }

    private void cleanData() throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM basket_items WHERE user_email = ?")) {
                stmt.setString(1, USER_EMAIL);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE name = ?")) {
                stmt.setString(1, PRODUCT_NAME);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email = ?")) {
                stmt.setString(1, USER_EMAIL);
                stmt.executeUpdate();
            }
        }
    }
}
