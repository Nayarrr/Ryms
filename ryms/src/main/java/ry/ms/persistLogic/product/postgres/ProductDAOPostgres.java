package ry.ms.persistLogic.product.postgres;

import ry.ms.businessLogic.product.models.Product;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.product.dao.ProductDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOPostgres implements ProductDAO {

    private final Connection conn;

    public ProductDAOPostgres() {
        this.conn = initConnection();
    }

    private static Connection initConnection() {
        try {
            return DBConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to obtain database connection", e);
        }
    }

    @Override
    public Product saveProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, stock, category) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCategory());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setProductId(rs.getLong(1));
                }
            }
        }
        return product;
    }

    @Override
    public void updateProduct(Product product) throws SQLException {
        if (product.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required for update");
        }
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock = ?, category = ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStock());
            stmt.setString(5, product.getCategory());
            stmt.setLong(6, product.getProductId());
            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Update failed, product ID not found.");
            }
        }
    }

    @Override
    public void deleteProduct(Long id) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Product getProductById(Long id) throws SQLException {
        String sql = "SELECT product_id, name, description, price, stock, category FROM products WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        String sql = "SELECT product_id, name, description, price, stock, category FROM products";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        return new Product(
            rs.getLong("product_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("stock"),
            rs.getString("category")
        );
    }
}