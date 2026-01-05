package ry.ms.businessLogic.basket;

import java.sql.SQLException;
import java.util.List;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.basket.models.BasketItem;

public final class SessionFacade {

    private static SessionFacade instance;

    private final BasketManager basketManager;

    private SessionFacade() {
        AbsFactory factory = AbsFactory.getInstance();
        this.basketManager = new BasketManager(factory);
    }

    public static synchronized SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public void addItem(String userEmail, Long productId, int quantity) throws SQLException {
        basketManager.addItem(userEmail, productId, quantity);
    }

    public void updateQuantity(String userEmail, Long productId, int quantity) throws SQLException {
        basketManager.updateQuantity(userEmail, productId, quantity);
    }

    public void removeItem(String userEmail, Long productId) throws SQLException {
        basketManager.removeItem(userEmail, productId);
    }

    public void emptyBasket(String userEmail) throws SQLException {
        basketManager.emptyBasket(userEmail);
    }

    public List<BasketItem> getBasketByUser(String userEmail) throws SQLException {
        return basketManager.getBasketByUser(userEmail);
    }
}
