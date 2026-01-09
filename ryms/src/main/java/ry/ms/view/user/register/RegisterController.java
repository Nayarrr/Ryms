package ry.ms.view.user.register;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.login.exceptions.UserAlreadyExistsException;
import ry.ms.businessLogic.user.login.exceptions.UserCreationException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label messageLabel;

    private final SessionFacade sessionFacade = SessionFacade.getSessionFactory();
    private Runnable onSuccess;
    private Runnable onLoginRequest;

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public void setOnLoginRequest(Runnable onLoginRequest) {
        this.onLoginRequest = onLoginRequest;
    }

    @FXML
    private void handleRegisterButtonAction() {
        String username = usernameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!validateInput(username, surname, email, password, confirmPassword)) {
            return;
        }

        try {
            sessionFacade.registerUser(email, password, username, surname);
            messageLabel.setText("Inscription réussie ! Vous pouvez maintenant vous connecter.");
            messageLabel.setTextFill(Color.GREEN);
            if (onSuccess != null) {
                // Optional: add a delay before navigating
                onSuccess.run();
            }
        } catch (UserAlreadyExistsException e) {
            messageLabel.setText("Un utilisateur avec cet email existe déjà.");
            messageLabel.setTextFill(Color.RED);
        } catch (UserCreationException | java.sql.SQLException e) {
            messageLabel.setText("Erreur serveur lors de l'inscription: " + e.getMessage());
            messageLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginLinkAction() {
        if (onLoginRequest != null) {
            onLoginRequest.run();
        }
    }

    private boolean validateInput(String username, String surname, String email, String password,
            String confirmPassword) {
        if (username.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez remplir tous les champs.");
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Les mots de passe ne correspondent pas.");
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        // Basic email validation
        if (!email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            messageLabel.setText("Veuillez entrer une adresse email valide.");
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        return true;
    }
}
