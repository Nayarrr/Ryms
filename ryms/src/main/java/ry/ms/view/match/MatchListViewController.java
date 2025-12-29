package ry.ms.view.match;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ry.ms.models.Match;
import ry.ms.models.Team;

import java.time.LocalDate;
import java.util.List;

public class MatchListViewController {

    @FXML private TableView<Match> matchTableView;
    @FXML private TableColumn<Match, Integer> idColumn;
    @FXML private TableColumn<Match, String> team1Column;
    @FXML private TableColumn<Match, String> team2Column;
    @FXML private TableColumn<Match, LocalDate> dateColumn;
    @FXML private TableColumn<Match, String> statusColumn;
    @FXML private TableColumn<Match, Void> actionsColumn;
    @FXML private Button createMatchButton;
    @FXML private Button refreshButton;
    @FXML private Label statusLabel;

    private MatchController controller;
    private Stage ownerStage;
    private ObservableList<Match> matchList;

    @FXML
    public void initialize() {
        controller = new MatchController();
        matchList = FXCollections.observableArrayList();
        
        setupTableColumns();
        loadMatches();
    }

    public void setOwnerStage(Stage stage) {
        this.ownerStage = stage;
    }

    private void setupTableColumns() {
        // Colonnes simples
        idColumn.setCellValueFactory(new PropertyValueFactory<>("matchId"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        
        // Équipe 1 - afficher le nom
        team1Column.setCellValueFactory(cellData -> {
            Team team = controller.getTeamForMatch(cellData.getValue(), 1);
            return new javafx.beans.property.SimpleStringProperty(
                team != null ? team.getName() : "Non assignée"
            );
        });
        
        // Équipe 2 - afficher le nom
        team2Column.setCellValueFactory(cellData -> {
            Team team = controller.getTeamForMatch(cellData.getValue(), 2);
            return new javafx.beans.property.SimpleStringProperty(
                team != null ? team.getName() : "Non assignée"
            );
        });
        
        // Statut (vous pouvez ajouter un champ dans Match)
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("À venir")
        );
        
        // Colonne Actions avec boutons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
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
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    javafx.scene.layout.HBox buttons = new javafx.scene.layout.HBox(5, viewButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadMatches() {
        List<Match> matches = controller.getAllMatches();
        matchList.clear();
        matchList.addAll(matches);
        matchTableView.setItems(matchList);
        statusLabel.setText("Chargé : " + matches.size() + " match(s)");
    }

    @FXML
    private void handleCreateMatch() {
        CreateMatchFrame createFrame = new CreateMatchFrame(ownerStage, this::handleRefresh);
        createFrame.show();
    }

    @FXML
    private void handleRefresh() {
        loadMatches();
        statusLabel.setText("Liste actualisée");
    }

    private void handleViewMatch(Match match) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du match");
        alert.setHeaderText("Match #" + match.getMatchId());
        
        Team team1 = controller.getTeamForMatch(match, 1);
        Team team2 = controller.getTeamForMatch(match, 2);
        
        alert.setContentText(
            "Date: " + match.getMatchDate() + "\n" +
            "Équipe 1: " + (team1 != null ? team1.getName() : "Non assignée") + "\n" +
            "Équipe 2: " + (team2 != null ? team2.getName() : "Non assignée")
        );
        alert.showAndWait();
    }

    private void handleDeleteMatch(Match match) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le match #" + match.getMatchId() + " ?");
        confirm.setContentText("Cette action est irréversible.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // ✅ Convertir int en Long
                boolean success = controller.deleteMatch(match.getMatchId());
                if (success) {
                    statusLabel.setText("Match supprimé");
                    handleRefresh();
                } else {
                    statusLabel.setText("Erreur lors de la suppression");
                }
            }
        });
    }
}