package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.tournament.models.Tournament;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for validating tournament match results.
 * Allows admins or referees to confirm scores.
 */
public class ValidateResultsController {

    @FXML
    private Label headerLabel;
    @FXML
    private TableView<Match> resultsTableView;
    @FXML
    private TableColumn<Match, String> roundColumn;
    @FXML
    private TableColumn<Match, String> team1Column;
    @FXML
    private TableColumn<Match, Integer> score1Column;
    @FXML
    private TableColumn<Match, String> team2Column;
    @FXML
    private TableColumn<Match, Integer> score2Column;
    @FXML
    private TableColumn<Match, Date> dateColumn;
    @FXML
    private TableColumn<Match, String> timeColumn;
    @FXML
    private TableColumn<Match, String> statusColumn;
    @FXML
    private TableColumn<Match, Void> actionsColumn;

    private Tournament tournament;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * Initializes the controller.
     * Sets up table columns.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
    }

    /**
     * Sets the tournament context.
     * 
     * @param tournament The tournament to validate results for.
     */
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
        if (tournament != null) {
            headerLabel.setText("Valider les résultats - " + tournament.getName());
            loadResults();
        }
    }

    /**
     * Configuration des colonnes du TableView
     */
    private void setupTableColumns() {
        // Colonne Round
        roundColumn.setCellValueFactory(cellData -> {
            // TODO: Implémenter la logique pour déterminer le round
            return new javafx.beans.property.SimpleStringProperty("Round 1");
        });

        // Colonne Équipe 1
        team1Column.setCellValueFactory(cellData -> {
            // TODO: Récupérer le nom de l'équipe 1
            return new javafx.beans.property.SimpleStringProperty("Équipe 1");
        });

        // Colonne Score 1
        score1Column.setCellValueFactory(cellData -> {
            // TODO: Récupérer le score de l'équipe 1
            return new javafx.beans.property.SimpleObjectProperty<>(0);
        });

        // Colonne Équipe 2
        team2Column.setCellValueFactory(cellData -> {
            // TODO: Récupérer le nom de l'équipe 2
            return new javafx.beans.property.SimpleStringProperty("Équipe 2");
        });

        // Colonne Score 2
        score2Column.setCellValueFactory(cellData -> {
            // TODO: Récupérer le score de l'équipe 2
            return new javafx.beans.property.SimpleObjectProperty<>(0);
        });

        // Colonne Date
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("matchDate"));
        dateColumn.setCellFactory(column -> new TableCell<Match, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Colonne Heure
        timeColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMatchDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        timeFormat.format(cellData.getValue().getMatchDate()));
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        // Colonne Statut
        statusColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getStatus().toString());
        });

        // Colonne Actions
        actionsColumn.setCellFactory(param -> new TableCell<Match, Void>() {
            private final Button validateButton = new Button("Valider");

            {
                validateButton.setOnAction(event -> {
                    Match match = getTableView().getItems().get(getIndex());
                    handleValidateMatch(match);
                });
                validateButton.getStyleClass().add("primary-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(validateButton);
                }
            }
        });
    }

    /**
     * Charger les résultats du tournoi
     */
    private void loadResults() {
        if (tournament != null && tournament.getMatches() != null) {
            resultsTableView.getItems().setAll(tournament.getMatches());
        }
    }

    @FXML
    private void handleRefresh() {
        loadResults();
    }

    @FXML
    private void handleValidateAll() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Validation des résultats");
        confirmation.setHeaderText("Valider tous les résultats?");
        confirmation.setContentText("Cette action validera tous les résultats affichés.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implémenter la validation de tous les résultats
                showInfo("Succès", "Tous les résultats ont été validés");
                loadResults();
            }
        });
    }

    private void handleValidateMatch(Match match) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Validation du résultat");
        confirmation.setHeaderText("Valider ce résultat?");
        confirmation.setContentText("Match #" + match.getMatchId());

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implémenter la validation d'un résultat
                showInfo("Succès", "Le résultat a été validé");
                loadResults();
            }
        });
    }

    @FXML
    private void handleBackToList() {
        navigateToView("/ry/ms/view/tournament/fxml/TournamentList.fxml", "Liste des tournois");
    }

    @FXML
    private void handleManageRegistrations() {
        if (tournament != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ry/ms/view/tournament/fxml/ManageRegistrations.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Inscriptions - " + tournament.getName());
                stage.setScene(new Scene(root, 800, 600));

                ManageRegistrationsController controller = loader.getController();
                controller.setTournament(tournament);

                stage.show();

                // Fermer la fenêtre actuelle
                ((Stage) resultsTableView.getScene().getWindow()).close();
            } catch (IOException e) {
                showError("Impossible d'ouvrir les inscriptions: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewBracket() {
        if (tournament != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ry/ms/view/tournament/fxml/TournamentBracket.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Bracket - " + tournament.getName());
                stage.setScene(new Scene(root, 1200, 800));

                TournamentBracketController controller = loader.getController();
                controller.setTournament(tournament);

                stage.show();

                // Fermer la fenêtre actuelle
                ((Stage) resultsTableView.getScene().getWindow()).close();
            } catch (IOException e) {
                showError("Impossible d'ouvrir le bracket: " + e.getMessage());
            }
        }
    }

    private void navigateToView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) resultsTableView.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Impossible de naviguer: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
