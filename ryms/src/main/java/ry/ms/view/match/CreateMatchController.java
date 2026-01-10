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
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;

public class CreateMatchController {

    @FXML
    private TextField team1SearchField;
    @FXML
    private ListView<Team> team1ListView;
    @FXML
    private Label team1SelectedLabel;
    @FXML
    private ComboBox<Team> team1ComboBox;

    @FXML
    private TextField team2SearchField;
    @FXML
    private ListView<Team> team2ListView;
    @FXML
    private Label team2SelectedLabel;
    @FXML
    private ComboBox<Team> team2ComboBox;

    @FXML
    private DatePicker matchDatePicker;
    @FXML
    private TextField matchHourField;
    @FXML
    private TextField matchMinuteField;

    @FXML
    private ComboBox<String> gameComboBox;

    @FXML
    private TextField refereeSearchField;
    @FXML
    private ListView<User> refereeListView;
    @FXML
    private VBox selectedRefereesContainer;

    @FXML
    private Button createButton;
    @FXML
    private Label messageLabel;

    private final MatchController matchController;
    private Team selectedTeam1;
    private Team selectedTeam2;
    private final List<User> selectedReferees = new ArrayList<>();

    private Stage modalStage;
    private Runnable onMatchCreated;
    private ry.ms.businessLogic.tournament.models.Tournament tournamentContext;

    public CreateMatchController() {
        this.matchController = new MatchController();
    }

    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    public void setOnMatchCreated(Runnable callback) {
        this.onMatchCreated = callback;
    }

    /**
     * Définir le contexte du tournoi pour pré-remplir le jeu
     */
    public void setTournamentContext(ry.ms.businessLogic.tournament.models.Tournament tournament) {
        this.tournamentContext = tournament;
        if (tournament != null && tournament.getGame() != null) {
            // Pré-remplir et désactiver la sélection du jeu
            gameComboBox.setValue(tournament.getGame().getName());
            gameComboBox.setDisable(true);

            // Charger les équipes inscrites au tournoi
            loadRegisteredTeams();
        }
    }

    /**
     * Charger les équipes inscrites au tournoi dans les ComboBox
     */
    private void loadRegisteredTeams() {
        if (tournamentContext == null)
            return;

        try {
            ry.ms.persistLogic.tournament.postgres.TournamentRegistrationDAOPostgres registrationDAO = new ry.ms.persistLogic.tournament.postgres.TournamentRegistrationDAOPostgres();
            ry.ms.persistLogic.team.postgres.TeamDAOPostgres teamDAO = new ry.ms.persistLogic.team.postgres.TeamDAOPostgres();

            java.util.List<ry.ms.businessLogic.tournament.models.TournamentRegistration> registrations = registrationDAO
                    .getRegistrationsByTournament((long) tournamentContext.getTournamentId());

            java.util.List<ry.ms.businessLogic.team.models.Team> registeredTeams = new java.util.ArrayList<>();

            for (ry.ms.businessLogic.tournament.models.TournamentRegistration reg : registrations) {
                ry.ms.businessLogic.team.models.Team team = teamDAO.getTeamById(reg.getTeamId());
                if (team != null) {
                    registeredTeams.add(team);
                }
            }

            // Masquer les champs de recherche
            team1SearchField.setVisible(false);
            team1SearchField.setManaged(false);
            team1ListView.setVisible(false);
            team1ListView.setManaged(false);

            team2SearchField.setVisible(false);
            team2SearchField.setManaged(false);
            team2ListView.setVisible(false);
            team2ListView.setManaged(false);

            // Afficher et remplir les ComboBox
            team1ComboBox.getItems().setAll(registeredTeams);
            team1ComboBox.setVisible(true);
            team1ComboBox.setManaged(true);
            team1ComboBox.setButtonCell(new javafx.scene.control.ListCell<Team>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    setText(empty || team == null ? null : team.getName() + " [" + team.getTag() + "]");
                }
            });
            team1ComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Team>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    setText(empty || team == null ? null : team.getName() + " [" + team.getTag() + "]");
                }
            });

            team2ComboBox.getItems().setAll(registeredTeams);
            team2ComboBox.setVisible(true);
            team2ComboBox.setManaged(true);
            team2ComboBox.setButtonCell(new javafx.scene.control.ListCell<Team>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    setText(empty || team == null ? null : team.getName() + " [" + team.getTag() + "]");
                }
            });
            team2ComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<Team>() {
                @Override
                protected void updateItem(Team team, boolean empty) {
                    super.updateItem(team, empty);
                    setText(empty || team == null ? null : team.getName() + " [" + team.getTag() + "]");
                }
            });

            // Ajouter des listeners pour la sélection
            team1ComboBox.setOnAction(e -> {
                selectedTeam1 = team1ComboBox.getValue();
                if (selectedTeam1 != null) {
                    team1SelectedLabel.setText("✓ " + selectedTeam1.getName());
                }
                validateForm();
            });

            team2ComboBox.setOnAction(e -> {
                selectedTeam2 = team2ComboBox.getValue();
                if (selectedTeam2 != null) {
                    team2SelectedLabel.setText("✓ " + selectedTeam2.getName());
                }
                validateForm();
            });

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des équipes inscrites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        setupGameComboBox();
        setupTeamSearch();
        setupRefereeSearch();
        setupTimeFields();
        setupValidationListeners();

        // Pour désactiver les dates passées
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
                "Fortnite");
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
        team1SearchField.textProperty().addListener((obs, oldVal, newVal) -> handleTeamSearch(newVal, team1ListView));

        team2SearchField.textProperty().addListener((obs, oldVal, newVal) -> handleTeamSearch(newVal, team2ListView));

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

    /**
     * Ajouter manuellement un arbitre par email
     */
    @FXML
    private void handleAddRefereeManually() {
        String email = refereeSearchField.getText();
        if (email == null || email.trim().isEmpty()) {
            showError("❌ Veuillez entrer un email");
            return;
        }

        email = email.trim();

        // Vérifier si l'arbitre n'est pas déjà ajouté
        for (User ref : selectedReferees) {
            if (ref.getEmail().equalsIgnoreCase(email)) {
                showError("⚠️ Cet arbitre est déjà ajouté");
                return;
            }
        }

        // Rechercher l'utilisateur par email
        List<User> users = matchController.searchUsersByEmail(email);

        User matchingUser = null;
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                matchingUser = user;
                break;
            }
        }

        if (matchingUser != null) {
            selectedReferees.add(matchingUser);
            updateSelectedRefereesDisplay();
            refereeSearchField.clear();
            validateForm();
            showSuccess("✓ Arbitre ajouté : " + matchingUser.getEmail());
        } else {
            showError("❌ Utilisateur introuvable : " + email);
        }
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

        // Validation de la date du match par rapport au tournoi
        if (tournamentContext != null) {
            java.time.LocalDate matchDate = matchDatePicker.getValue();

            // Convertir les dates du tournoi (Date) en LocalDate
            java.time.LocalDate tournamentStart = tournamentContext.getStartDate()
                    .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate tournamentEnd = tournamentContext.getEndDate()
                    .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            if (matchDate.isBefore(tournamentStart)) {
                showError("La date du match doit être après le début du tournoi (" +
                        tournamentStart.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
                return false;
            }

            if (matchDate.isAfter(tournamentEnd)) {
                showError("La date du match doit être avant la fin du tournoi (" +
                        tournamentEnd.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ")");
                return false;
            }
        }

        return true;
    }

    private void validateForm() {
        // Vérifier la sélection des équipes (soit via recherche, soit via ComboBox)
        boolean team1Selected = selectedTeam1 != null ||
                (team1ComboBox != null && team1ComboBox.getValue() != null);
        boolean team2Selected = selectedTeam2 != null ||
                (team2ComboBox != null && team2ComboBox.getValue() != null);

        boolean isValid = team1Selected &&
                team2Selected &&
                matchDatePicker.getValue() != null &&
                !matchHourField.getText().isBlank() &&
                !matchMinuteField.getText().isBlank() &&
                gameComboBox.getValue() != null &&
                !selectedReferees.isEmpty();

        createButton.setDisable(!isValid);
    }

    @FXML
    private void handleCreateMatch() { // Appelée dans le fxml
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

            // Utiliser le tournamentId du contexte si disponible, sinon utiliser gameId
            int tournamentId = (tournamentContext != null) ? tournamentContext.getTournamentId() : gameId;

            boolean success = matchController.createMatch(
                    selectedTeam1,
                    selectedTeam2,
                    matchDate,
                    tournamentId,
                    selectedReferees);
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
    private void handleCancel() { // Appelée dans le fxml
        if (modalStage != null) {
            modalStage.close();
        }
    }

    private void showSuccess(String message) { // Appelée dans le fxml
        messageLabel.setText(message);
    }

    private void showError(String message) { // Appelée dans le fxml
        messageLabel.setText(message);
    }
}