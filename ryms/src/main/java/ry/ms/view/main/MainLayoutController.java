package ry.ms.view.main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.view.user.UserSession;

public class MainLayoutController {

    // ✅ Singleton pour accès global
    private static MainLayoutController instance;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        instance = this; // ✅ Enregistrer l'instance
        loadTeamDashboard();
    }

    // ✅ Getter pour l'instance singleton
    public static MainLayoutController getInstance() {
        return instance;
    }

    // ✅ Méthode pour charger un contenu dans contentArea
    public void loadContent(Node content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    @FXML
    private void handleTeamsClick() {
        loadTeamDashboard();
    }

    @FXML
    private void handleMatchsClick() {
        loadMatchList();
    }

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

    private void loadTeamDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/team/fxml/TeamDashboard.fxml")
            );
            Parent teamView = loader.load();
            loadContent(teamView);
            
            System.out.println("✅ TeamDashboard chargé dans MainLayout");
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la gestion des équipes.");
        }
    }

    private void loadMatchList() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/match/fxml/MatchListView.fxml")
            );
            Parent matchView = loader.load();
            loadContent(matchView);
            
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