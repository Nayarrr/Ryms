package ry.ms.view.user.login;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;
import ry.ms.view.user.UserSession;

public class LoginController {

    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
    @FXML
    private Label messageLabel;

    private final SessionFacade sessionFacade = SessionFacade.getSessionFactory();
    private Runnable onSuccess;

    /**
     * Sets a callback to be run on successful login.
     * 
     * @param onSuccess the runnable to execute.
     */
    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    private Runnable onRegisterRequest;

    public void setOnRegisterRequest(Runnable onRegisterRequest) {
        this.onRegisterRequest = onRegisterRequest;
    }

    private Runnable onForgotPasswordRequest;

    public void setOnForgotPasswordRequest(Runnable onForgotPasswordRequest) {
        this.onForgotPasswordRequest = onForgotPasswordRequest;
    }

    @FXML
    private void handleRegisterLinkAction() {
        if (onRegisterRequest != null) {
            onRegisterRequest.run();
        }
    }

    @FXML
    private void handleForgotPasswordLinkAction() {
        if (onForgotPasswordRequest != null) {
            onForgotPasswordRequest.run();
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = loginField.getText();
        String password = passField.getText();

        // Validation simple
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez saisir le nom et le mot de passe.");
            messageLabel.setTextFill(Color.RED);
            return;
        }

        try {
            // Appel BDD
            User user = sessionFacade.loginUser(username, password);

            if (user != null) {
                // Check if account is deactivated
                if (!user.isActive()) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                            javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Account Deactivated");
                    alert.setHeaderText("Your account is deactivated.");
                    alert.setContentText("Do you want to reactivate it?");

                    java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                        sessionFacade.reactivateAccount();
                    } else {
                        sessionFacade.logout();
                        messageLabel.setText("Account remains deactivated.");
                        messageLabel.setTextFill(Color.RED);
                        return;
                    }
                }

                UserSession userSession = UserSession.getInstance();
                userSession.setUser(user);
                messageLabel.setText("Connexion réussie !");
                messageLabel.setTextFill(Color.GREEN);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } else {
                messageLabel.setText("Email ou mot de passe incorrect.");
                messageLabel.setTextFill(Color.RED);
                passField.clear(); // Sécurité : on efface le mot de passe incorrect
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur de connexion serveur.");
            messageLabel.setTextFill(Color.RED);
        }
    }
}