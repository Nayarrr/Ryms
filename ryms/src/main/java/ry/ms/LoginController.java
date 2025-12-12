package ry.ms;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ry.ms.businessLogic.login.SessionFacade;

public class LoginController {
    /**
     * Méthode appelée lorsque l'utilisateur clique sur le bouton "Se connecter".
     * A CHANGER
     */

    private SessionFacade sessionFacade = SessionFacade.getSessionFactory();
    public boolean handleLoginButtonAction(TextField loginField, PasswordField passField, Label messageLabel) {
        String username = loginField.getText();
        String password = passField.getText();

        // 2. Logique de validation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez saisir le nom et le mot de passe.");
        }
        try {
            boolean isLoggingTrue = sessionFacade.login(username, password);

        // Exemple simple de vérification (à remplacer par une vérification en base de données)
            if (isLoggingTrue) {
                messageLabel.setText("Connexion réussie ! Bienvenue, " + username + ".");
                messageLabel.setTextFill(javafx.scene.paint.Color.GREEN); // Texte vert pour le succès
                return true;


            }
            
            else {
                messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
                messageLabel.setTextFill(javafx.scene.paint.Color.RED); // Texte rouge pour l'erreur
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Optionnel : effacer le champ de mot de passe après l'essai
        passField.setText("");
        return false;
    }
}
