package ry.ms.view.user.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;

import java.sql.SQLException;
import java.util.Optional;

public class ProfileController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private Runnable onBackRequest;
    private Runnable onLogoutRequest;

    public void setOnBackRequest(Runnable onBackRequest) {
        this.onBackRequest = onBackRequest;
    }

    public void setOnLogoutRequest(Runnable onLogoutRequest) {
        this.onLogoutRequest = onLogoutRequest;
    }

    @FXML
    public void initialize() {
        User currentUser = SessionFacade.getSessionFactory().getCurrentUser();
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
            emailField.setText(currentUser.getEmail());
            // Password field left empty or could show placeholder
        }
    }

    @FXML
    private void handleUpdate() {
        String username = usernameField.getText();
        String surname = surnameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || surname.isEmpty()) {
            messageLabel.setText("Username and Surname cannot be empty.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            User currentUser = SessionFacade.getSessionFactory().getCurrentUser();
            currentUser.setUsername(username);
            if (password != null && !password.isEmpty()) {
                currentUser.setPassword(password);
            }

            SessionFacade.getSessionFactory().updateUser(currentUser);
            messageLabel.setText("Profile updated successfully!");
            messageLabel.setTextFill(Color.GREEN);

        } catch (SQLException e) {
            messageLabel.setText("Error updating profile: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                SessionFacade.getSessionFactory().deleteUser();
                if (onLogoutRequest != null) {
                    onLogoutRequest.run();
                }
            } catch (SQLException e) {
                messageLabel.setText("Error deleting account: " + e.getMessage());
                messageLabel.setTextFill(Color.RED);
            }
        }
    }

    @FXML
    private void handleBack() {
        if (onBackRequest != null) {
            onBackRequest.run();
        }
    }
}
