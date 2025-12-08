package ry.ms; // Assurez-vous que ce package correspond à celui de votre projet

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    // 1. Déclaration des variables liées aux fx:id dans le FXML



    /**
     * Méthode appelée lorsque l'utilisateur clique sur le bouton "Se connecter".
     * L'annotation @FXML est nécessaire car elle est appelée par le FXML Loader.
     */
    public boolean handleLoginButtonAction(TextField loginField, PasswordField passField, Label messageLabel) {
        String username = loginField.getText();
        String password = passField.getText();

        // 2. Logique de validation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Veuillez saisir le nom et le mot de passe.");
        }

        // Exemple simple de vérification (à remplacer par une vérification en base de données)
        if (username.equals("admin") && password.equals("password")) {
            messageLabel.setText("Connexion réussie ! Bienvenue, " + username + ".");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN); // Texte vert pour le succès
            return true;


        } else {
            messageLabel.setText("Nom d'utilisateur ou mot de passe incorrect.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED); // Texte rouge pour l'erreur
        }

        // Optionnel : effacer le champ de mot de passe après l'essai
        passField.setText("");
        return false;
    }
}
