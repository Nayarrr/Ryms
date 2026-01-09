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

    public void updateNavbarState(boolean isLoggedIn, String username) {
        if (authBox != null && userBox != null) {
            authBox.setVisible(!isLoggedIn);
            authBox.setManaged(!isLoggedIn);
            userBox.setVisible(isLoggedIn);
            userBox.setManaged(isLoggedIn);
            if (isLoggedIn && userLabel != null) {
                userLabel.setText("Hello, " + username);
            }
        }
    }
}