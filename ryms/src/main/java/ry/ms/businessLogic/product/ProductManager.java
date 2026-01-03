package ry.ms.businessLogic.product;

import java.sql.SQLException;
import java.util.List;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.product.models.Product;
import ry.ms.persistLogic.product.dao.ProductDAO;

public class ProductManager {

    private final ProductDAO productDAO;

    public ProductManager(AbsFactory factory) {
        this.productDAO = factory.createProductDAO();
    }

    public Product createProduct(Product product) throws SQLException {
        validate(product);
        return productDAO.saveProduct(product);
    }

    public void editProduct(Product product) throws SQLException {
        validate(product);
        productDAO.updateProduct(product);
    }

    public void deleteProduct(Long id) throws SQLException {
        productDAO.deleteProduct(id);
    }

    public Product getProductById(Long id) throws SQLException {
        return productDAO.getProductById(id);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAllProducts();
    }

    private void validate(Product product) {
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
    }
}