package ry.ms.persistLogic.basket.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import ry.ms.businessLogic.basket.models.BasketItem;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.basket.dao.BasketDAO;

public class BasketDAOPostgres implements BasketDAO {

    private static final String UPSERT_SQL =
        "INSERT INTO basket_items (user_email, product_id, quantity) " +
        "VALUES (?, ?, ?) " +
        "ON CONFLICT (user_email, product_id) DO UPDATE " +
        "SET quantity = EXCLUDED.quantity, added_at = CURRENT_TIMESTAMP";

    private static final String UPDATE_QUANTITY_SQL =
        "UPDATE basket_items SET quantity = ?, added_at = CURRENT_TIMESTAMP " +
        "WHERE user_email = ? AND product_id = ?";

    private static final String DELETE_ITEM_SQL =
        "DELETE FROM basket_items WHERE user_email = ? AND product_id = ?";

    private static final String CLEAR_BASKET_SQL =
        "DELETE FROM basket_items WHERE user_email = ?";

    private static final String SELECT_BY_USER_SQL =
        "SELECT user_email, product_id, quantity, added_at " +
        "FROM basket_items WHERE user_email = ? ORDER BY added_at DESC";

    @Override
    public void saveItem(BasketItem item) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPSERT_SQL)) {
            stmt.setString(1, item.getUserEmail());
            stmt.setLong(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateQuantity(String userEmail, Long productId, int quantity) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_QUANTITY_SQL)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, userEmail);
            stmt.setLong(3, productId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteItem(String userEmail, Long productId) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_ITEM_SQL)) {
            stmt.setString(1, userEmail);
            stmt.setLong(2, productId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void clearBasket(String userEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CLEAR_BASKET_SQL)) {
            stmt.setString(1, userEmail);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<BasketItem> getBasketByUser(String userEmail) throws SQLException {
        List<BasketItem> items = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_BY_USER_SQL)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        }
        return items;
    }

    private BasketItem mapItem(ResultSet rs) throws SQLException {
        String email = rs.getString("user_email");
        Long productId = rs.getLong("product_id");
        int quantity = rs.getInt("quantity");
        Timestamp addedAt = rs.getTimestamp("added_at");
        return new BasketItem(email, productId, quantity, addedAt);
    }
}
