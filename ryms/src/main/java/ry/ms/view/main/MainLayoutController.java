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

    private static MainLayoutController instance;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        instance = this;
        loadTeamDashboard();
    }

    public static MainLayoutController getInstance() {
        return instance;
    }

    // Méthode pour charger un contenu dans contentArea
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
    private void handleGamesClick() {
        showGameCatalog();
    }

    @FXML
    private void handleProductClick(){
        handleOpenProducts();
    }

    @FXML
    private void handleTournamentsClick() {
        showTournamentList();
    }

    @FXML
    private void handleShopClick() {
        loadShopView();
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            loadContent(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOpenInvitations() {
        loadView("/ry/ms/view/team/fxml/InvitationsModal.fxml");
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
        loadView("/ry/ms/view/team/fxml/TeamDashboard.fxml");
    }

    private void loadMatchList() {
        loadView("/ry/ms/view/match/fxml/MatchListView.fxml");
    }

    @FXML
    private void handleOpenProducts() {
        loadView("/ry/ms/view/product/fxml/ProductDashboard.fxml");
    }


    private void loadShopView() {
        loadView("/ry/ms/view/product/fxml/ShopView.fxml");
    }


    public void showGameCatalog() {
        loadView("/ry/ms/view/game/fxml/gameCatalogLayout.fxml");
    }

    public void showTournamentList() { loadView("/ry/ms/view/tournament/fxml/TournamentList.fxml");}

    

    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}