package ry.ms.persistLogic.product.dao;

import java.sql.SQLException;
import java.util.List;
import ry.ms.businessLogic.product.models.Product;

public interface ProductDAO {
    Product saveProduct(Product product) throws SQLException;
    void updateProduct(Product product) throws SQLException;
    void deleteProduct(Long id) throws SQLException;
    Product getProductById(Long id) throws SQLException;
    List<Product> getAllProducts() throws SQLException;
}