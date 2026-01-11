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

    public void updateNavbarState(boolean isLoggedIn, String username) {
        if (authBox != null && userBox != null) {
            authBox.setVisible(!isLoggedIn);
            authBox.setManaged(!isLoggedIn);
            userBox.setVisible(isLoggedIn);
            userBox.setManaged(isLoggedIn);
            if (isLoggedIn && userLabel != null) {
                userLabel.setText("Hello, " + username);
            }
            // Check for Admin role.
            // We need to pass the role or get it from SessionFacade.
            // Better to decouple and let MainViewController configure it, or just pull from
            // Session here.
            // Since this is a simple View Helper, let's pull from session for now or
            // better, update signature.
            // But changing signature affects callers.
            // Let's use SessionFacade here for convenience as it is used elsewhere in View
            // layer (e.g. ProfileController)

            ry.ms.businessLogic.user.models.User currentUser = ry.ms.businessLogic.user.login.SessionFacade
                    .getSessionFactory().getCurrentUser();
            if (adminButton != null) {
                boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
                adminButton.setVisible(isAdmin);
                adminButton.setManaged(isAdmin);
            }
        }
    }
}