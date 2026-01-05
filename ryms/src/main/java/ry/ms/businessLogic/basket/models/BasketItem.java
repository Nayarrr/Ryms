package ry.ms.businessLogic.basket.models;

import java.sql.Timestamp;

public class BasketItem {
    private String userEmail;
    private Long productId;
    private int quantity;
    private Timestamp addedAt;

    public BasketItem(String userEmail, Long productId, int quantity, Timestamp addedAt) {
        this.userEmail = userEmail;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }
}
