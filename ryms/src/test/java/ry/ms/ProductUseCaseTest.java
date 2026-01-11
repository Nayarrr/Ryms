package ry.ms;

import junit.framework.TestCase;
import ry.ms.businessLogic.product.ProductManager;
import ry.ms.businessLogic.product.models.Product;
import ry.ms.persistLogic.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Integration test for the Product Management Use Case.
 */
public class ProductUseCaseTest extends TestCase {

    private static final String PRODUCT_NAME = "JUnit_Product_One";

    public void testProductScenario() throws Exception {
        cleanData();

        ProductManager manager = new ProductManager(new PostgresFactory());
        Product product = new Product(null, PRODUCT_NAME, "JUnit product description", 20.0, 5, "TEST");

        try {
            Product created = manager.createProduct(product);
            assertNotNull("Product should be created", created);
            assertNotNull("Product ID should be generated", created.getProductId());

            created.setPrice(25.0);
            created.setStock(7);
            manager.editProduct(created);

            Product fetched = manager.getProductById(created.getProductId());
            assertNotNull("Product should be retrievable after update", fetched);
            assertEquals("Updated price should match", 25.0, fetched.getPrice());
            assertEquals("Updated stock should match", 7, fetched.getStock());

            manager.deleteProduct(created.getProductId());
            Product deleted = manager.getProductById(created.getProductId());
            assertNull("Product should be deleted", deleted);
        } finally {
            cleanData();
        }
    }

    private void cleanData() throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE name = ?")) {
            stmt.setString(1, PRODUCT_NAME);
            stmt.executeUpdate();
        }
    }
}
