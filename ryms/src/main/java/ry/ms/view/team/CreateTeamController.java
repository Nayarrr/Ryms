package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ry.ms.view.team.MainLayoutController;
import ry.ms.view.team.UserSession;

public class CreateTeamController {

    @FXML private TextField nameField;
    @FXML private TextField tagField;
    @FXML private TextField avatarField;
    @FXML private Label errorLabel;

    private final TeamController teamController = new TeamController();

    @FXML
    private void handleCreateTeam() {
        String name = nameField.getText();
        String tag = tagField.getText();
        String avatar = avatarField.getText();
        String userEmail = UserSession.getInstance().getUserEmail();

        try {
            teamController.createTeam(name, tag, avatar, userEmail);
            // On force le rechargement du Layout pour afficher le Dashboard
            // Astuce simple : On retrouve le controlleur parent via la Scene
            // (Note: Dans une vraie app complexe, on utiliserait un Observer pattern)
            errorLabel.getScene().getRoot().fireEvent(new javafx.event.Event(javafx.event.Event.ANY)); 
            // Pour simplifier ici : le bouton notification du MainLayout rafraichit la vue quand fermé.
            // Mieux : On relance la vérification :
             MainLayoutController main = findMainController();
             if(main != null) main.checkUserTeamStatus();

        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
        }
    }
    
    // Helper simple pour retrouver le parent
    private MainLayoutController findMainController() {
         // Ceci est une simplification. Idéalement on passe le parent au child.
         // Pour l'instant, on assume que ça marche ou on demande à l'utilisateur de cliquer sur un bouton refresh.
         // Une meilleure façon est d'injecter le MainLayoutController lors du chargement du FXML.
         return null; // TODO: Relancer checkUserTeamStatus via injection
    }
}