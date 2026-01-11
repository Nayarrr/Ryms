package ry.ms.view.user.login;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.user.login.SessionFacade;

public class ForgotPasswordController {

    @FXML
    private VBox requestPane;
    @FXML
    private VBox resetPane;
    @FXML
    private TextField emailField;
    @FXML
    private TextField codeField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label messageLabel;

    private Runnable onBackRequest;

    public void setOnBackRequest(Runnable onBackRequest) {
        this.onBackRequest = onBackRequest;
    }

    @FXML
    private void handleRequestReset() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            messageLabel.setText("Please enter your email.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        if (SessionFacade.getSessionFactory().requestPasswordReset(email)) {
            messageLabel.setText("Verification code sent to console (mock).");
            messageLabel.setTextFill(Color.GREEN);
            requestPane.setVisible(false);
            requestPane.setManaged(false);
            resetPane.setVisible(true);
            resetPane.setManaged(true);
        } else {
            messageLabel.setText("Failed to send code. User might not exist.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleResetPassword() {
        String email = emailField.getText();
        String code = codeField.getText();
        String newPassword = newPasswordField.getText();

        if (code.isEmpty() || newPassword.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            if (SessionFacade.getSessionFactory().completePasswordReset(email, code, newPassword)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password reset successfully. Please login.");
                alert.showAndWait();

                if (onBackRequest != null) {
                    onBackRequest.run();
                }
            } else {
                messageLabel.setText("Invalid code or email.");
                messageLabel.setTextFill(Color.RED);
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void handleBack() {
        if (onBackRequest != null) {
            onBackRequest.run();
        }
    }
}
