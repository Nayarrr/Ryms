package ry.ms.view.match;

import java.text.SimpleDateFormat;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.view.user.UserSession;

public class MatchListViewController {

    @FXML private TableView<Match> matchTableView;
    @FXML private TableColumn<Match, Long> matchIdColumn;
    @FXML private TableColumn<Match, String> team1Column;
    @FXML private TableColumn<Match, String> team2Column;
    @FXML private TableColumn<Match, String> dateColumn;
    @FXML private TableColumn<Match, Void> actionsColumn;
    @FXML private Button createMatchButton;
    @FXML private VBox rootContainer;

    private MatchController matchController;
    private String currentUserEmail;

    @FXML
    public void initialize() {
        matchController = new MatchController();
        currentUserEmail = UserSession.getInstance().getUserEmail();

        setupTableColumns();
        loadMatches();
        
        // ✅ Gérer la visibilité du bouton "Créer match" (admin uniquement)
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        createMatchButton.setVisible(isAdmin);
        createMatchButton.setManaged(isAdmin);
    }

    private void setupTableColumns() {
        matchIdColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleLongProperty(cellData.getValue().getMatchId()).asObject());

        team1Column.setCellValueFactory(cellData -> {
            Team team = matchController.getTeamForMatch(cellData.getValue(), 1);
            String teamName = team != null ? team.getName() + " [" + team.getTag() + "]" : "En attente";
            return new javafx.beans.property.SimpleStringProperty(teamName);
        });

        team2Column.setCellValueFactory(cellData -> {
            Team team = matchController.getTeamForMatch(cellData.getValue(), 2);
            String teamName = team != null ? team.getName() + " [" + team.getTag() + "]" : "En attente";
            return new javafx.beans.property.SimpleStringProperty(teamName);
        });

        dateColumn.setCellValueFactory(cellData -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String dateStr = cellData.getValue().getMatchDate() != null 
                ? dateFormat.format(cellData.getValue().getMatchDate()) 
                : "Non définie";
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");
            private final Button deleteButton = new Button("Supprimer");

            {
                viewButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                viewButton.setOnAction(event -> {
                    Match match = getTableView().getItems().get(getIndex());
                    handleViewMatch(match);
                });

                deleteButton.setOnAction(event -> {
                    Match match = getTableView().getItems().get(getIndex());
                    handleDeleteMatch(match);
                });

                boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
                deleteButton.setVisible(isAdmin);
                deleteButton.setManaged(isAdmin);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, viewButton);
                    if (deleteButton.isVisible()) {
                        buttons.getChildren().add(deleteButton);
                    }
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadMatches() {
        List<Match> matches = matchController.getAllMatches();
        matchTableView.getItems().setAll(matches);
    }

    @FXML
    private void handleCreateMatch() {
        CreateMatchFrame createMatchFrame = new CreateMatchFrame();
        createMatchFrame.setOnMatchCreated(this::loadMatches);
        createMatchFrame.show();
    }

    private void handleViewMatch(Match match) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/ry/ms/view/match/fxml/MatchDetailsView.fxml")
            );
            javafx.scene.Parent detailsView = loader.load();
            
            MatchDetailsController controller = loader.getController();
            controller.loadMatchDetails(match.getMatchId());
            
            ry.ms.view.main.MainLayoutController mainController = 
                ry.ms.view.main.MainLayoutController.getInstance();
            
            if (mainController != null) {
                mainController.loadContent(detailsView);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible d'ouvrir les détails");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleDeleteMatch(Match match) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le match #" + match.getMatchId() + " ?");
        confirm.setContentText("Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = matchController.deleteMatch(match.getMatchId());
                
                if (success) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Succès");
                    info.setHeaderText("Match supprimé");
                    info.showAndWait();
                    loadMatches();
                } else {
                    Alert error = new Alert(Alert.AlertType.ERROR);
                    error.setTitle("Erreur");
                    error.setHeaderText("Impossible de supprimer le match");
                    error.showAndWait();
                }
            }
        });
    }
}