package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ry.ms.businessLogic.games.GameCatalogFacade;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.tournament.TournamentFacade;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentStatus;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Add Tournament view.
 * Handles the creation of new tournaments.
 */
public class AddTournamentController {

    @FXML
    private TextField nameField;
    @FXML
    private ComboBox<String> gameCombo;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Spinner<Integer> maxParticipantsSpinner;
    @FXML
    private TextField locationField;
    @FXML
    private ComboBox<TournamentStatus> statusCombo;
    @FXML
    private Label errorLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private TournamentFacade tournamentFacade;
    private GameCatalogFacade gameFacade;
    private Runnable onTournamentSaved;

    /**
     * Initializes the controller class.
     * Sets up the facade instances and loads necessary data for combo boxes.
     */
    @FXML
    public void initialize() {
        tournamentFacade = TournamentFacade.getInstance();
        gameFacade = GameCatalogFacade.getGameCatalogFactory();

        // Load games
        List<Game> games = gameFacade.loadGameCatalog();
        for (Game game : games) {
            gameCombo.getItems().add(game.getName());
        }

        // Initialize status combo
        statusCombo.getItems().setAll(TournamentStatus.values());
        statusCombo.setValue(TournamentStatus.PLANNING);

        // Spinner value factory is set in FXML, but we can ensure it's correct here if
        // needed
        // It's already defined in FXML, so we just use it.
    }

    /**
     * Sets the callback to be executed when a tournament is successfully saved.
     * 
     * @param onTournamentSaved The callback runnable.
     */
    public void setOnTournamentSaved(Runnable onTournamentSaved) {
        this.onTournamentSaved = onTournamentSaved;
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                String name = nameField.getText();
                String gameName = gameCombo.getValue();
                LocalDate startLocalDate = startDatePicker.getValue();
                LocalDate endLocalDate = endDatePicker.getValue();
                int maxParticipants = maxParticipantsSpinner.getValue();
                String location = locationField.getText();
                TournamentStatus status = statusCombo.getValue();

                // Convertir LocalDate en java.util.Date pour le modèle
                java.util.Date startDate = startLocalDate != null ? Date.valueOf(startLocalDate) : null;
                java.util.Date endDate = endLocalDate != null ? Date.valueOf(endLocalDate) : null;

                // Récupérer l'objet Game
                Game selectedGame = null;
                if (gameName != null) {
                    selectedGame = gameFacade.getGameByName(gameName);
                }

                Tournament tournament = new Tournament(0, name, selectedGame, startDate, endDate, maxParticipants,
                        location);
                tournament.setStatus(status != null ? status : TournamentStatus.PLANNING);

                tournamentFacade.createTournament(tournament);

                if (onTournamentSaved != null) {
                    onTournamentSaved.run();
                }

                closeDialog();
            } catch (SQLException e) {
                showError("Erreur d'enregistrement", "Impossible de créer le tournoi: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();

        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            errorMessage.append("Le nom du tournoi est requis.\n");
        }

        if (maxParticipantsSpinner.getValue() == null || maxParticipantsSpinner.getValue() <= 0) {
            errorMessage.append("Le nombre de participants doit être positif.\n");
        }

        if (errorMessage.length() == 0) {
            errorLabel.setVisible(false);
            return true;
        } else {
            errorLabel.setText(errorMessage.toString());
            errorLabel.setVisible(true);
            // Also show Alert for consistency if needed, but Label is better for inline
            // validation
            // sticking to show error via label as per FXML design
            return false;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        if (saveButton != null && saveButton.getScene() != null) {
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        }
    }
}