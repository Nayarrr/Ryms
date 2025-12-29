package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.view.user.UserSession;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        loadTeamDashboard();
    }

    /**
     * Bouton "Équipes"
     */
    @FXML
    private void handleTeamsClick() {
        loadTeamDashboard();
    }

    /**
     * Bouton "Matchs"
     */
    @FXML
    private void handleMatchsClick() {
        loadMatchList();
    }

    /**
     * Bouton "Invitations"
     */
    @FXML
    private void handleOpenInvitations() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/team/fxml/InvitationsModal.fxml")
            );
            Parent root = loader.load();

            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setTitle("Invitations");
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture des invitations.");
        }
    }

    /**
     * Bouton "Déconnexion"
     */
    @FXML
    private void handleLogout() {
        try {
            UserSession.getInstance().clearSession();

            Stage stage = (Stage) contentArea.getScene().getWindow();
            ry.ms.App app = new ry.ms.App();
            app.start(stage);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la déconnexion.");
        }
    }

    /**
     * Charge TeamDashboard.fxml
     */
    private void loadTeamDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/team/fxml/TeamDashboard.fxml")
            );
            Parent teamView = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(teamView);
            
            System.out.println("✅ TeamDashboard chargé dans MainLayout");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la gestion des équipes.");
        }
    }

    /**
     * Charge MatchListView.fxml
     */
    private void loadMatchList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/match/fxml/MatchListView.fxml")
            );
            Parent matchView = loader.load();
            
            Object controller = loader.getController();
            try {
                Stage stage = (Stage) contentArea.getScene().getWindow();
                controller.getClass()
                    .getMethod("setOwnerStage", Stage.class)
                    .invoke(controller, stage);
            } catch (Exception e) {
                // OK
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(matchView);
            
            System.out.println("✅ MatchListView chargé dans MainLayout");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la gestion des matchs.");
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}