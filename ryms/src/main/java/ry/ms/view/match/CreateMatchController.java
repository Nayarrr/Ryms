package ry.ms.view.match;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ry.ms.models.User;
import ry.ms.models.team.Team;

public class CreateMatchController {

    @FXML private TextField team1SearchField;
    @FXML private ListView<Team> team1ListView;
    @FXML private Label team1SelectedLabel;

    @FXML private TextField team2SearchField;
    @FXML private ListView<Team> team2ListView;
    @FXML private Label team2SelectedLabel;

    @FXML private DatePicker matchDatePicker;
    @FXML private TextField matchHourField;
    @FXML private TextField matchMinuteField;

    @FXML private ComboBox<String> gameComboBox;

    @FXML private TextField refereeSearchField;
    @FXML private ListView<User> refereeListView;
    @FXML private VBox selectedRefereesContainer;

    @FXML private Button createButton;
    @FXML private Label messageLabel;

    private final MatchController matchController;
    private Team selectedTeam1;
    private Team selectedTeam2;
    private final List<User> selectedReferees = new ArrayList<>();
    
    private Stage modalStage;
    private Runnable onMatchCreated;

    public CreateMatchController() {
        this.matchController = new MatchController();
    }

    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    public void setOnMatchCreated(Runnable callback) {
        this.onMatchCreated = callback;
    }

    @FXML
    public void initialize() {
        setupGameComboBox();
        setupTeamSearch();
        setupRefereeSearch();
        setupTimeFields();
        setupValidationListeners();

        //Pour désactiver les dates passées
        matchDatePicker.setDayCellFactory(picker -> new javafx.scene.control.DateCell() {
        @Override
        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            
            LocalDate today = LocalDate.now();
            
            // Désactiver les dates passées
            if (date.isBefore(today)) {
                setDisable(true);
            }
        }
    });
    }

    // En dur pour le moment en attendant le useCase adequat
    private void setupGameComboBox() {
        gameComboBox.getItems().addAll(
            "League of Legends",
            "Valorant",
            "CS:GO",
            "Rocket League",
            "Fortnite"
        );
    }

    private void setupTimeFields() {
        matchHourField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                matchHourField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                matchHourField.setText(newVal.substring(0, 2));
            }
        });

        matchMinuteField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                matchMinuteField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                matchMinuteField.setText(newVal.substring(0, 2));
            }
        });
    }

    // Écouter TOUS les champs pour activer/désactiver le bouton pour créer
    private void setupValidationListeners() {
        // Écouter les changements de date
        matchDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        // Écouter les changements d'heure et minute
        matchHourField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        matchMinuteField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        
        // Écouter les changements de jeu
        gameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
    }

    private void setupTeamSearch() {
        team1SearchField.textProperty().addListener((obs, oldVal, newVal) -> 
            handleTeamSearch(newVal, team1ListView)
        );

        team2SearchField.textProperty().addListener((obs, oldVal, newVal) -> 
            handleTeamSearch(newVal, team2ListView)
        );

        team1ListView.setOnMouseClicked(event -> {
            Team selected = team1ListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedTeam1 = selected;
                team1SelectedLabel.setText("✓ " + selected.getName());
                team1SearchField.setText(selected.getName());
                team1ListView.setVisible(false);
                team1ListView.setManaged(false);
                validateForm();
            }
        });

        team2ListView.setOnMouseClicked(event -> {
            Team selected = team2ListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedTeam2 = selected;
                team2SelectedLabel.setText("✓ " + selected.getName());
                team2SearchField.setText(selected.getName());
                team2ListView.setVisible(false);
                team2ListView.setManaged(false);
                validateForm();
            }
        });
    }

    private void handleTeamSearch(String searchTerm, ListView<Team> listView) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            listView.setVisible(false);
            listView.setManaged(false);
            return;
        }

        List<Team> teams = matchController.searchTeamsByName(searchTerm.trim());
        
        if (teams.isEmpty()) {
            listView.setVisible(false);
            listView.setManaged(false);
        } else {
            listView.getItems().setAll(teams);
            listView.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    setText(empty || team == null ? null : team.getName() + " [" + team.getTag() + "]");
                }
            });
            listView.setVisible(true);
            listView.setManaged(true);
        }
    }

    private void setupRefereeSearch() {
        refereeSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                refereeListView.setVisible(false);
                refereeListView.setManaged(false);
                return;
            }

            List<User> users = matchController.searchUsersByEmail(newVal.trim());
            
            if (users.isEmpty()) {
                refereeListView.setVisible(false);
                refereeListView.setManaged(false);
            } else {
                refereeListView.getItems().setAll(users);
                refereeListView.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        setText(empty || user == null ? null : user.getEmail());
                    }
                });
                refereeListView.setVisible(true);
                refereeListView.setManaged(true);
            }
        });

        refereeListView.setOnMouseClicked(event -> {
            User selected = refereeListView.getSelectionModel().getSelectedItem();
            if (selected != null && !selectedReferees.contains(selected)) {
                selectedReferees.add(selected);
                updateSelectedRefereesDisplay();
                refereeSearchField.clear();
                refereeListView.setVisible(false);
                refereeListView.setManaged(false);
                validateForm(); // Revalider après ajout d'arbitre
            }
        });
    }

    private void updateSelectedRefereesDisplay() {
        selectedRefereesContainer.getChildren().clear();
        
        for (User referee : selectedReferees) {
            HBox refereeBox = new HBox(10);
            
            Label refereeLabel = new Label("• " + referee.getEmail());
            Button removeButton = new Button("✕");
            removeButton.setOnAction(e -> {
                selectedReferees.remove(referee);
                updateSelectedRefereesDisplay();
                validateForm(); // Revalider après suppression d'arbitre
            });
            
            refereeBox.getChildren().addAll(refereeLabel, removeButton);
            selectedRefereesContainer.getChildren().add(refereeBox);
        }
    }

    
    private boolean validateInputs() {
        if (selectedTeam1 == null) {
            showError("Veuillez sélectionner l'équipe 1");
            return false;
        }

        if (selectedTeam2 == null) {
            showError("Veuillez sélectionner l'équipe 2");
            return false;
        }

        if (selectedTeam1.getTeamId().equals(selectedTeam2.getTeamId())) {
            showError("Les deux équipes doivent être différentes");
            return false;
        }

        if (matchDatePicker.getValue() == null) {
            showError("Veuillez sélectionner une date");
            return false;
        }

        if (matchHourField.getText().isBlank() || matchMinuteField.getText().isBlank()) {
            showError("Veuillez spécifier l'heure du match");
            return false;
        }

        if (gameComboBox.getValue() == null) {
            showError("Veuillez sélectionner un jeu");
            return false;
        }

        if (selectedReferees.isEmpty()) {
            showError("Veuillez ajouter au moins un arbitre");
            return false;
        }

        return true;
    }

    private void validateForm() {
        boolean isValid = selectedTeam1 != null && 
                         selectedTeam2 != null && 
                         matchDatePicker.getValue() != null &&
                         !matchHourField.getText().isBlank() &&
                         !matchMinuteField.getText().isBlank() &&
                         gameComboBox.getValue() != null &&
                         !selectedReferees.isEmpty();
        
        createButton.setDisable(!isValid);
    }

    @FXML
    private void handleCreateMatch() { //Appelée dans le fxml
        if (!validateInputs()) {
            return;
        }

        try {
            LocalDate localDate = matchDatePicker.getValue();
            int hour = Integer.parseInt(matchHourField.getText());
            int minute = Integer.parseInt(matchMinuteField.getText());

            LocalDate today = LocalDate.now();
            if (localDate.isBefore(today)) {
                showError("❌ La date du match ne peut pas être dans le passé !");
                return;
            }

            // Si la date est aujourd'hui, vérifier l'heure
            if (localDate.isEqual(today)) {
                java.time.LocalTime now = java.time.LocalTime.now();
                java.time.LocalTime matchTime = java.time.LocalTime.of(hour, minute);
                
                if (matchTime.isBefore(now)) {
                    showError("❌ L'heure du match ne peut pas être dans le passé !");
                    return;
                }
            }

            if (hour < 0 || hour > 23) {
                showError("❌ L'heure doit être entre 0 et 23");
                return;
            }

            if (minute < 0 || minute > 59) {
                showError("❌ Les minutes doivent être entre 0 et 59");
                return;
            }

            LocalDateTime localDateTime = localDate.atTime(hour, minute);
            Date matchDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            int gameId = gameComboBox.getSelectionModel().getSelectedIndex() + 1;

            boolean success = matchController.createMatch(
                selectedTeam1, 
                selectedTeam2, 
                matchDate, 
                gameId, 
                selectedReferees
            );

            if (success) {
                showSuccess("✅ Match créé avec succès !");
                
                // Fermer la modale après 1 seconde
                javafx.application.Platform.runLater(() -> {
                    try {
                        Thread.sleep(1000);
                        if (modalStage != null) {
                            modalStage.close();
                        }
                        if (onMatchCreated != null) {
                            onMatchCreated.run();
                        }
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
            } else {
                showError("❌ Erreur lors de la création du match");
            }

        } catch (NumberFormatException e) {
            showError("❌ Heure ou minute invalide");
        } catch (Exception e) {
            showError("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() { //Appelée dans le fxml
        if (modalStage != null) {
            modalStage.close();
        }
    }

    private void showSuccess(String message) { //Appelée dans le fxml
        messageLabel.setText(message);
    }

    private void showError(String message) { //Appelée dans le fxml
        messageLabel.setText(message);
    }
}