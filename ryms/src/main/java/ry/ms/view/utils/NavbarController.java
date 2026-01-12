package ry.ms.view.utils;

import javafx.fxml.FXML;

public class NavbarController {

    private Runnable onLoginClick;
    private Runnable onRegisterClick;
    private Runnable onLogoutClick; // Add new Runnable
    private Runnable onProfileClick; // Add new Runnable

    @FXML
    private javafx.scene.layout.HBox authBox;
    @FXML
    private javafx.scene.layout.HBox userBox;
    @FXML
    private javafx.scene.control.Label userLabel;

    public void setOnLoginClick(Runnable onLoginClick) {
        this.onLoginClick = onLoginClick;
    }

    public void setOnRegisterClick(Runnable onRegisterClick) {
        this.onRegisterClick = onRegisterClick;
    }

    public void setOnLogoutClick(Runnable onLogoutClick) {
        this.onLogoutClick = onLogoutClick;
    }

    public void setOnProfileClick(Runnable onProfileClick) {
        this.onProfileClick = onProfileClick;
    }

    @FXML
    private void handleLogin() {
        if (onLoginClick != null)
            onLoginClick.run();
    }

    @FXML
    private void handleRegister() {
        if (onRegisterClick != null)
            onRegisterClick.run();
    }

    @FXML
    private void handleLogout() {
        if (onLogoutClick != null)
            onLogoutClick.run();
    }

    @FXML
    private void handleProfile() {
        if (onProfileClick != null)
            onProfileClick.run();
    }

    private Runnable onAdminClick; // Admin runnable

    public void setOnAdminClick(Runnable onAdminClick) {
        this.onAdminClick = onAdminClick;
    }

    @FXML
    private javafx.scene.control.Button adminButton;

    @FXML
    private void handleAdmin() {
        if (onAdminClick != null)
            onAdminClick.run();
    }

    @FXML
    private javafx.scene.layout.HBox navLinks;

    // Runnables for feature navigation
    private Runnable onTeamsClick;
    private Runnable onMatchesClick;
    private Runnable onGamesClick;
    private Runnable onTournamentsClick;
    private Runnable onProductsClick;
    private Runnable onShopClick;
    private Runnable onInvitationsClick;

    public void setOnTeamsClick(Runnable onTeamsClick) {
        this.onTeamsClick = onTeamsClick;
    }

    public void setOnMatchesClick(Runnable onMatchesClick) {
        this.onMatchesClick = onMatchesClick;
    }

    public void setOnGamesClick(Runnable onGamesClick) {
        this.onGamesClick = onGamesClick;
    }

    public void setOnTournamentsClick(Runnable onTournamentsClick) {
        this.onTournamentsClick = onTournamentsClick;
    }

    public void setOnProductsClick(Runnable onProductsClick) {
        this.onProductsClick = onProductsClick;
    }

    public void setOnShopClick(Runnable onShopClick) {
        this.onShopClick = onShopClick;
    }

    public void setOnInvitationsClick(Runnable onInvitationsClick) {
        this.onInvitationsClick = onInvitationsClick;
    }

    @FXML
    private void handleTeamsClick() {
        if (onTeamsClick != null)
            onTeamsClick.run();
    }

    @FXML
    private void handleMatchesClick() {
        if (onMatchesClick != null)
            onMatchesClick.run();
    }

    @FXML
    private void handleGamesClick() {
        if (onGamesClick != null)
            onGamesClick.run();
    }

    @FXML
    private void handleTournamentsClick() {
        if (onTournamentsClick != null)
            onTournamentsClick.run();
    }

    @FXML
    private void handleProductsClick() {
        if (onProductsClick != null)
            onProductsClick.run();
    }

    @FXML
    private void handleShopClick() {
        if (onShopClick != null)
            onShopClick.run();
    }

    @FXML
    private void handleInvitationsClick() {
        if (onInvitationsClick != null)
            onInvitationsClick.run();
    }

    public void updateNavbarState(boolean isLoggedIn, String username) {
        if (authBox != null && userBox != null) {
            authBox.setVisible(!isLoggedIn);
            authBox.setManaged(!isLoggedIn);
            userBox.setVisible(isLoggedIn);
            userBox.setManaged(isLoggedIn);

            // Check for Admin role.
            ry.ms.businessLogic.user.models.User currentUser = ry.ms.businessLogic.user.login.SessionFacade
                    .getSessionFactory().getCurrentUser();
            boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

            // Show Nav Links only when logged in
            if (navLinks != null) {
                navLinks.setVisible(isLoggedIn);
                navLinks.setManaged(isLoggedIn);

                // Hide "Produits" (for Product Management) for non-admins
                for (javafx.scene.Node node : navLinks.getChildren()) {
                    if (node instanceof javafx.scene.control.Button) {
                        javafx.scene.control.Button btn = (javafx.scene.control.Button) node;
                        if ("Produits".equals(btn.getText()) || "Gestion des Produits".equals(btn.getText())) {
                            btn.setVisible(isAdmin);
                            btn.setManaged(isAdmin);
                        }
                    }
                }
            }

            if (isLoggedIn && userLabel != null) {
                userLabel.setText("Hello, " + username);
            }
            if (adminButton != null) {
                adminButton.setVisible(isAdmin);
                adminButton.setManaged(isAdmin);
            }
        }
    }
}