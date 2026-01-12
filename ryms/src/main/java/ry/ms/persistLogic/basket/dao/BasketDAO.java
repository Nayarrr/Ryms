package ry.ms.persistLogic.basket.dao;

import java.sql.SQLException;
import java.util.List;
import ry.ms.businessLogic.basket.models.BasketItem;

/**
 * Data Access Object interface for managing the User's Basket.
 * Provides methods for CRUD operations on basket items.
 */
public interface BasketDAO {

    /**
     * Saves or updates a basket item.
     * 
     * @param item The {@link BasketItem} to save.
     * @throws SQLException If a database access error occurs.
     */
    void saveItem(BasketItem item) throws SQLException;

    /**
     * Updates the quantity of a specific product in the user's basket.
     * 
     * @param userEmail The email of the user.
     * @param productId The ID of the product.
     * @param quantity  The new quantity.
     * @throws SQLException If a database access error occurs.
     */
    void updateQuantity(String userEmail, Long productId, int quantity) throws SQLException;

    /**
     * Removes a specific item from the user's basket.
     * 
     * @param userEmail The email of the user.
     * @param productId The ID of the product to remove.
     * @throws SQLException If a database access error occurs.
     */
    void deleteItem(String userEmail, Long productId) throws SQLException;

    /**
     * Clears all items from the user's basket.
     * 
     * @param userEmail The email of the user.
     * @throws SQLException If a database access error occurs.
     */
    void clearBasket(String userEmail) throws SQLException;

    /**
     * Retrieves all items in the user's basket.
     * 
     * @param userEmail The email of the user.
     * @return A list of {@link BasketItem}s.
     * @throws SQLException If a database access error occurs.
     */
    List<BasketItem> getBasketByUser(String userEmail) throws SQLException;

    /**
     * Retrieves a specific item from the user's basket.
     * 
     * @param userEmail The email of the user.
     * @param productId The ID of the product.
     * @return The {@link BasketItem} if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    BasketItem getItem(String userEmail, Long productId) throws SQLException;
}
