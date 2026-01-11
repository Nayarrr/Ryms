package ry.ms.view.main;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import ry.ms.view.user.UserSession;
import ry.ms.view.match.MatchListViewController;
import ry.ms.view.match.matchDetail.MatchDetailsController;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    private Runnable onLogout;

    @FXML
    public void initialize() {
        loadTeamDashboard();
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
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
    private void handleProductClick() {
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
            showError("Failed to load view: " + fxmlPath);
        }
    }

    @FXML
    private void handleOpenInvitations() {
        loadView("/ry/ms/view/team/fxml/InvitationsModal.fxml");
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clearSession();
        if (onLogout != null) {
            onLogout.run();
        }
    }

    public void loadTeamDashboard() {
        loadView("/ry/ms/view/team/fxml/TeamDashboard.fxml");
    }

    public void loadMatchList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/match/fxml/MatchListView.fxml"));
            Parent view = loader.load();
            MatchListViewController controller = loader.getController();
            controller.setNavigationController(this::loadMatchDetails); // Pass navigation logic
            loadContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load match list.");
        }
    }

    public void loadMatchDetails(long matchId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/match/fxml/MatchDetailsView.fxml"));
            Parent view = loader.load();
            MatchDetailsController controller = loader.getController();
            controller.setNavigationController(this::loadMatchList); // Pass navigation logic
            controller.loadMatchDetails(matchId);
            loadContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load match details.");
        }
    }

    @FXML
    public void handleOpenProducts() {
        loadView("/ry/ms/view/product/fxml/ProductDashboard.fxml");
    }

    public void loadShopView() {
        loadView("/ry/ms/view/product/fxml/ShopView.fxml");
    }

    public void showGameCatalog() {
        loadView("/ry/ms/view/game/fxml/gameCatalogLayout.fxml");
    }

    public void showTournamentList() {
        loadView("/ry/ms/view/tournament/fxml/TournamentList.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}