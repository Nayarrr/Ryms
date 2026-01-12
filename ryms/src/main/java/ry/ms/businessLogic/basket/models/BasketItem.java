package ry.ms.businessLogic.basket.models;

import java.sql.Timestamp;

/**
 * Represents an item in a user's shopping basket.
 * Contains product ID, quantity, and timestamp details.
 */
public class BasketItem {
    private String userEmail;
    private Long productId;
    private int quantity;
    private Timestamp addedAt;

    /**
     * Constructs a new BasketItem.
     * 
     * @param userEmail The email of the user who owns the basket.
     * @param productId The ID of the product.
     * @param quantity  The quantity of the product.
     * @param addedAt   The timestamp when the item was added also used for sorting.
     */
    public BasketItem(String userEmail, Long productId, int quantity, Timestamp addedAt) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    /**
     * Gets the user's email.
     * 
     * @return The user's email.
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Sets the user's email.
     * 
     * @param userEmail The user's email.
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Gets the product ID.
     * 
     * @return The product ID.
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * Sets the product ID.
     * 
     * @param productId The product ID.
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * Gets the quantity.
     * 
     * @return The quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * 
     * @param quantity The quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the timestamp when the item was added.
     * 
     * @return The timestamp.
     */
    public Timestamp getAddedAt() {
        return addedAt;
    }

    /**
     * Sets the timestamp when the item was added.
     * 
     * @param addedAt The timestamp.
     */
    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
}
