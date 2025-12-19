package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres; // Ou via Factory si tu préfères
import ry.ms.businessLogic.team.models.Team;

import java.io.IOException;

public class MainLayoutController {

    @FXML private StackPane contentArea;

    public void initialize() {
        checkUserTeamStatus();
    }

    // Décide quelle vue afficher (Création ou Dashboard)
    public void checkUserTeamStatus() {
        String email = UserSession.getInstance().getUserEmail();
        try {
            // Appel direct au DAO pour vérifier le statut (Pour simplifier la vue)
            Team team = new TeamDAOPostgres().getTeamByMemberEmail(email);
            
            if (team != null) {
                loadView("/ry/ms/view/team/fxml/TeamDashboard.fxml");
            } else {
                loadView("/ry/ms/view/team/fxml/CreateTeam.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenInvitations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/team/fxml/InvitationsModal.fxml"));
            Parent root = loader.load();
            
            // Création de la fenêtre Pop-up (Modale)
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Mes Invitations");
            stage.setScene(new Scene(root));
            
            // Rafraîchir le dashboard si on accepte une invitation
            stage.setOnHidden(e -> checkUserTeamStatus());
            
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("Déconnexion...");
        // Logique pour fermer et revenir au login (à implémenter selon ton App.java)
        ((Stage) contentArea.getScene().getWindow()).close();
    }
}