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

    @FXML
    private void handleRegisterLinkAction() {
        if (onRegisterRequest != null) {
            onRegisterRequest.run();
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