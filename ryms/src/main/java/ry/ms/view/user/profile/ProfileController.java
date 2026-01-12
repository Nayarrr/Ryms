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
        String password = passwordField.getText();

        if (username.isEmpty()) {
            messageLabel.setText("Username cannot be empty.");
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
    private void handleChangePassword() {
        // Create a custom dialog for password change
        javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your current and new password");

        ButtonType changeButtonType = new ButtonType("Change", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Enable/Disable login button depending on whether a username was entered.
        javafx.scene.Node loginButton = dialog.getDialogPane().lookupButton(changeButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        currentPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || newPasswordField.getText().trim().isEmpty());
        });
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || currentPasswordField.getText().trim().isEmpty());
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == changeButtonType) {
            try {
                SessionFacade.getSessionFactory().changePassword(currentPasswordField.getText(),
                        newPasswordField.getText());

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Success");
                info.setHeaderText(null);
                info.setContentText("Password changed successfully.");
                info.showAndWait();

            } catch (Exception e) { // Catch UserDoesntExist, IncorrectPassword, SQLException
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Password Change Failed");
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deactivate Account");
        alert.setHeaderText("Are you sure you want to deactivate your account?");
        alert.setContentText("You can reactivate it by logging in again.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                SessionFacade.getSessionFactory().deactivateAccount();
                if (onLogoutRequest != null) {
                    onLogoutRequest.run();
                }
            } catch (Exception e) {
                messageLabel.setText("Error deactivating account: " + e.getMessage());
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
