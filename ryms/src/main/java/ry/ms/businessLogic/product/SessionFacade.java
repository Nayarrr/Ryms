package ry.ms.businessLogic.product;

import java.sql.SQLException;
import java.util.List;

import ry.ms.PostgresFactory;
import ry.ms.businessLogic.product.models.Product;

public class SessionFacade {

    private static SessionFacade instance;
    private final ProductManager productManager;

    private SessionFacade() {
        this.productManager = new ProductManager(new PostgresFactory());
    }

    public static SessionFacade getSessionFactory() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public Product createProduct(Product product) throws SQLException {
        return productManager.createProduct(product);
    }

    public void editProduct(Product product) throws SQLException {
        productManager.editProduct(product);
    }

    public void deleteProduct(Long id) throws SQLException {
        productManager.deleteProduct(id);
    }

    public Product getProductById(Long id) throws SQLException {
        return productManager.getProductById(id);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productManager.getAllProducts();
    }
}