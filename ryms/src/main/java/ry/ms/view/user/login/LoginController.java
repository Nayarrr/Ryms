package ry.ms.view.user.login;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.user.login.SessionFacade;

public class LoginController {

    private SessionFacade sessionFacade = SessionFacade.getSessionFactory();

    /**
     * Vérifie les identifiants via la Facade et met à jour l'interface en conséquence.
     * @return true si la connexion est réussie, false sinon.
     */
    public boolean handleLoginButtonAction(TextField loginField, PasswordField passField, Label messageLabel) {
        String username = loginField.getText();
        String password = passField.getText();

        // Validation simple
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez saisir le nom et le mot de passe.");
            messageLabel.setTextFill(Color.RED);
            return false;
        }

        try {
            // Appel BDD
            boolean isAuthenticated = sessionFacade.login(username, password);

            if (isAuthenticated) {
                messageLabel.setText("Connexion réussie !");
                messageLabel.setTextFill(Color.GREEN);
                return true; // Indique à App.java qu'on peut changer de page
            } else {
                messageLabel.setText("Email ou mot de passe incorrect.");
                messageLabel.setTextFill(Color.RED);
                passField.clear(); // Sécurité : on efface le mot de passe incorrect
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Erreur de connexion serveur.");
            messageLabel.setTextFill(Color.RED);
            return false;
        }
    }
}