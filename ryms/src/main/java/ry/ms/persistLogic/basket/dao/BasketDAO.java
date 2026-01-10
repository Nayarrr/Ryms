package ry.ms.persistLogic.basket.dao;

import java.sql.SQLException;
import java.util.List;
import ry.ms.businessLogic.basket.models.BasketItem;

public interface BasketDAO {

    void saveItem(BasketItem item) throws SQLException;

    void updateQuantity(String userEmail, Long productId, int quantity) throws SQLException;

    void deleteItem(String userEmail, Long productId) throws SQLException;

    void clearBasket(String userEmail) throws SQLException;

    List<BasketItem> getBasketByUser(String userEmail) throws SQLException;

    BasketItem getItem(String userEmail, Long productId) throws SQLException;
}
