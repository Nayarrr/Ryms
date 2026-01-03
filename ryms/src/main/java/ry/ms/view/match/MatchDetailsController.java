package ry.ms.view.match;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.models.Match;
import ry.ms.models.MatchStatus;
import ry.ms.models.Team;
import ry.ms.models.TeamResult;
import ry.ms.models.User;
import ry.ms.view.main.MainLayoutController;
import ry.ms.view.user.UserSession;

public class MatchDetailsController {

    @FXML private VBox rootContainer;
    @FXML private Label matchTitleLabel;
    @FXML private Label matchDateLabel;
    @FXML private Button editDateButton;
    @FXML private VBox refereesListContainer;
    @FXML private Button addRefereeButton;
    @FXML private Label team1NameLabel;
    @FXML private Label team1CoachLabel;
    @FXML private VBox team1RosterContainer;
    @FXML private Button team1UpdateButton;
    @FXML private Label team2NameLabel;
    @FXML private Label team2CoachLabel;
    @FXML private VBox team2RosterContainer;
    @FXML private Button team2UpdateButton;
    @FXML private VBox scoresContainer;
    @FXML private Button startMatchButton;

    private MatchController matchController;
    private String currentUserEmail;
    private Long matchId;
    private Team team1;
    private Team team2;
    private List<User> matchReferees;

    /**
     * Initialise le controller en chargeant les dépendances nécessaires
     */
    @FXML
    public void initialize() {
        matchController = new MatchController();
        currentUserEmail = UserSession.getInstance().getUserEmail();
    }

    /**
     * Vérifie si l'utilisateur connecté est un administrateur ou un arbitre
     */
    private boolean isAdminOrReferee(){
        if (currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com")) {
            return true;
        }

        if (matchReferees != null && currentUserEmail != null) {
            for (User referee : matchReferees) {
                if (referee.getEmail().equalsIgnoreCase(currentUserEmail)) {
                    return true;
                }
            }
        return false;
        }

        return false;
    }

    /**
     * Charge et affiche tous les détails d'un match spécifique
     */
    public void loadMatchDetails(Long matchId) {
        this.matchId = matchId;
        
        Match match = matchController.getMatchById(matchId);
        
        if (match == null) {
            System.err.println("❌ Match introuvable");
            return;
        }

        // Charger les équipes
        team1 = matchController.getTeamForMatch(match, 1);
        team2 = matchController.getTeamForMatch(match, 2);

        // Titre
        String team1Name = team1 != null ? team1.getName() + " [" + team1.getTag() + "]" : "En attente";
        String team2Name = team2 != null ? team2.getName() + " [" + team2.getTag() + "]" : "En attente";
        matchTitleLabel.setText(team1Name + " vs " + team2Name);

        // Date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
        String dateStr = match.getMatchDate() != null ? dateFormat.format(match.getMatchDate()) : "Date non définie";
        matchDateLabel.setText("📅 Date : " + dateStr);

        // Arbitres
        loadReferees(match);
        this.matchReferees = match.getReferees();

        // Équipes
        loadTeamDetails(team1, team1NameLabel, team1CoachLabel, team1RosterContainer, team1UpdateButton);
        loadTeamDetails(team2, team2NameLabel, team2CoachLabel, team2RosterContainer, team2UpdateButton);

        // Scores
        loadScores(matchId);

        // ✅ NOUVEAU : Gestion du bouton "Commencer le match"
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        boolean isReferee = matchReferees != null && currentUserEmail != null && 
                        matchReferees.stream().anyMatch(ref -> ref.getEmail().equalsIgnoreCase(currentUserEmail));
        
        // Visible uniquement si admin/arbitre ET match pas encore commencé
        if ((isAdmin || isReferee) && match.getStatus() == MatchStatus.SCHEDULED) {
            startMatchButton.setVisible(true);
            startMatchButton.setManaged(true);
        } else {
            startMatchButton.setVisible(false);
            startMatchButton.setManaged(false);
        }

        // Visibilité des autres boutons admin
        addRefereeButton.setVisible(isAdmin);
        addRefereeButton.setManaged(isAdmin);
        editDateButton.setVisible(isAdmin);
        editDateButton.setManaged(isAdmin);
    }

    /**
     * Charge et affiche la liste des arbitres assignés au match
     */
    private void loadReferees(Match match) {
        refereesListContainer.getChildren().clear();

        if (match.getReferees() != null && !match.getReferees().isEmpty()) {
            for (User referee : match.getReferees()) {
                Label refLabel = new Label("• " + referee.getUsername());
                refereesListContainer.getChildren().add(refLabel);
            }
        } else {
            Label noRefLabel = new Label("Aucun arbitre assigné");
            refereesListContainer.getChildren().add(noRefLabel);
        }
    }

    /**
     * Charge et affiche les scores des équipes avec possibilité de modification
     */
    private void loadScores(Long matchId) {
        if (scoresContainer == null) {
            System.err.println("⚠️ scoresContainer non défini dans le FXML");
            return;
        }

        scoresContainer.getChildren().clear();

        List<TeamResult> results = matchController.getMatchResults(matchId);

        // Si aucun score n'existe encore
        if (results == null || results.isEmpty()) {
            Label titleLabel = new Label("📊 Scores");
            scoresContainer.getChildren().add(titleLabel);

            Label noScoresLabel = new Label("Les scores n'ont pas encore été initialisés pour ce match.");
            scoresContainer.getChildren().add(noScoresLabel);

            // Bouton pour initialiser les scores (admin/arbitre uniquement)
            if (isAdminOrReferee()) {
                Button initButton = new Button("Initialiser les scores");
                initButton.setOnAction(e -> handleInitializeScores(matchId));
                scoresContainer.getChildren().add(initButton);
            }
            
            return;
        }

        // Titre
        Label titleLabel = new Label("📊 Scores");
        scoresContainer.getChildren().add(titleLabel);

        // Afficher chaque résultat
        for (TeamResult result : results) {
            HBox scoreBox = createScoreBox(matchId, result);
            scoresContainer.getChildren().add(scoreBox);
        }

        // Bouton "Finaliser le match" (admin/arbitre uniquement)
        if (isAdminOrReferee()) {
            boolean isFinalized = results.stream().anyMatch(r -> r.getResult() != null);
            
            if (!isFinalized) {
                Button finalizeButton = new Button("Finaliser le match");
                finalizeButton.setOnAction(e -> handleFinalizeMatch(matchId));
                
                VBox.setMargin(finalizeButton, new javafx.geometry.Insets(10, 0, 0, 0));
                scoresContainer.getChildren().add(finalizeButton);
            } else {
                Label finalizedLabel = new Label("✅ Match finalisé");
                scoresContainer.getChildren().add(finalizedLabel);
            }
        }
    }

    /**
     * Crée une boîte d'affichage de score pour une équipe avec contrôles de modification
     */
    private HBox createScoreBox(Long matchId, TeamResult result) {
        HBox box = new HBox(15);

        // Nom de l'équipe
        Label teamLabel = new Label(result.getTeam().getName() + " [" + result.getTeam().getTag() + "]");
        teamLabel.setPrefWidth(200);

        // Score actuel
        Label scoreLabel = new Label("Score: " + result.getScore());
        scoreLabel.setPrefWidth(100);

        // Résultat (WIN/Loss/Draw ou "En cours")
        String resultText = result.getResult() != null ? result.getResult().toString() : "En cours";
        Label resultLabel = new Label(resultText);

        box.getChildren().addAll(teamLabel, scoreLabel, resultLabel);

        // Si admin/arbitre ET match non finalisé : permettre la modification du score
        if (isAdminOrReferee() && result.getResult() == null) {
            TextField scoreField = new TextField(String.valueOf(result.getScore()));
            scoreField.setPrefWidth(60);
            scoreField.setMaxWidth(60);
            scoreField.setPromptText("Score");

            Button updateButton = new Button("✓");
            updateButton.setOnAction(e -> {
                try {
                    int newScore = Integer.parseInt(scoreField.getText());
                    Label messageLabel = new Label();
                    boolean success = matchController.updateScore(matchId, result.getTeam().getTeamId(), newScore, messageLabel);
                    
                    if (success) {
                        loadMatchDetails(matchId); // Recharger les détails
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("❌ Score invalide");
                }
            });

            box.getChildren().addAll(scoreField, updateButton);
        }

        return box;
    }

    /**
     * Finalise le match en calculant automatiquement les résultats Win/Loss/Draw
     */
    private void handleFinalizeMatch(Long matchId) {
        Label tempLabel = new Label();
        boolean success = matchController.finalizeMatch(matchId, tempLabel);
        
        if (success) {
            loadMatchDetails(matchId); // Recharger pour afficher les résultats
        } else {
            System.err.println("❌ Erreur finalisation : " + tempLabel.getText());
        }
    }

    /**
     * Initialise les scores à 0 pour les deux équipes du match
     */
    private void handleInitializeScores(Long matchId) {
        // Récupérer les équipes du match
        if (team1 == null || team2 == null) {
            System.err.println("❌ Impossible d'initialiser les scores : équipes non chargées");
            return;
        }

        Label messageLabel = new Label();
        
        // Initialiser score équipe 1
        boolean success1 = matchController.updateScore(matchId, team1.getTeamId(), 0, messageLabel);
        
        // Initialiser score équipe 2
        boolean success2 = matchController.updateScore(matchId, team2.getTeamId(), 0, messageLabel);
        
        if (success1 && success2) {
            System.out.println("✅ Scores initialisés pour le match " + matchId);
            loadMatchDetails(matchId); // Recharger pour afficher les scores
        } else {
            System.err.println("❌ Erreur lors de l'initialisation des scores");
        }
    }

    /**
     * Charge et affiche les informations détaillées d'une équipe (nom, coach, roster)
     */
    private void loadTeamDetails(Team team, Label nameLabel, Label coachLabel, VBox rosterContainer, Button updateButton) {
        if (team == null) {
            nameLabel.setText("En attente");
            coachLabel.setText("Coach : Non assigné");
            updateButton.setVisible(false);
            updateButton.setManaged(false);
            return;
        }

        nameLabel.setText(team.getName() + " [" + team.getTag() + "]");
        
        String coachEmail = team.getCaptainEmail() != null ? team.getCaptainEmail() : "Non assigné";
        coachLabel.setText("Coach : " + coachEmail);

        // Roster
        rosterContainer.getChildren().clear();
        
        try {
            List<User> members = matchController.getTeamMembers(team.getTeamId());

            if (members.isEmpty()) {
                rosterContainer.getChildren().add(new Label("• Aucun membre"));
            } else {
                for (User member : members) {
                    rosterContainer.getChildren().add(new Label("• " + member.getEmail()));
                }
            }
        } catch (Exception e) {
            rosterContainer.getChildren().add(new Label("• Erreur de chargement"));
            e.printStackTrace();
        }

        // Bouton "Update Roster"
        boolean canUpdate = currentUserEmail != null && 
                           (currentUserEmail.equalsIgnoreCase(coachEmail) || 
                            currentUserEmail.equalsIgnoreCase("admin@ryms.com"));
        updateButton.setVisible(canUpdate);
        updateButton.setManaged(canUpdate);
    }

    /**
     * Retourne à la vue liste des matchs
     */
    @FXML
    private void handleBackToList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/match/fxml/MatchListView.fxml"));
            VBox matchListView = loader.load();
            
            MainLayoutController mainController = MainLayoutController.getInstance();
            if (mainController != null) {
                mainController.loadContent(matchListView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre une modale pour modifier la date et l'heure du match
     */
    @FXML
    private void handleEditDate() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner((Stage) rootContainer.getScene().getWindow());
        modal.setTitle("Modifier la date du match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Nouvelle date et heure du match :");

        // Date picker
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Sélectionner une date");
        datePicker.setPrefWidth(250);

        // Champs heure et minute
        HBox timeBox = new HBox(10);
        TextField hourField = new TextField();
        hourField.setPromptText("HH");
        hourField.setPrefWidth(60);
        hourField.setMaxWidth(60);

        Label separator = new Label(":");

        TextField minuteField = new TextField();
        minuteField.setPromptText("MM");
        minuteField.setPrefWidth(60);
        minuteField.setMaxWidth(60);

        Label format24h = new Label("(format 24h)");

        timeBox.getChildren().addAll(hourField, separator, minuteField, format24h);

        // Validation des champs heure/minute
        hourField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                hourField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                hourField.setText(newVal.substring(0, 2));
            }
        });

        minuteField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                minuteField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                minuteField.setText(newVal.substring(0, 2));
            }
        });

        Label messageLabel = new Label();

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                messageLabel.setText("❌ Veuillez sélectionner une date");
                return;
            }

            if (hourField.getText().isBlank() || minuteField.getText().isBlank()) {
                messageLabel.setText("❌ Veuillez spécifier l'heure");
                return;
            }

            try {
                int hour = Integer.parseInt(hourField.getText());
                int minute = Integer.parseInt(minuteField.getText());

                if (hour < 0 || hour > 23) {
                    messageLabel.setText("❌ L'heure doit être entre 0 et 23");
                    return;
                }

                if (minute < 0 || minute > 59) {
                    messageLabel.setText("❌ Les minutes doivent être entre 0 et 59");
                    return;
                }

                LocalDate localDate = datePicker.getValue();
                LocalDateTime localDateTime = localDate.atTime(hour, minute);
                Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                boolean updated = matchController.updateMatchDate(matchId, newDate, messageLabel);

                if (updated) {
                    messageLabel.setText("✅ Date mise à jour avec succès !");

                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                            javafx.application.Platform.runLater(() -> {
                                modal.close();
                                loadMatchDetails(matchId); // Recharger les détails
                            });
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }).start();
                }

            } catch (NumberFormatException ex) {
                messageLabel.setText("❌ Heure ou minute invalide");
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setOnAction(e -> modal.close());

        HBox buttonsBox = new HBox(10, saveButton, cancelButton);

        layout.getChildren().addAll(
            titleLabel,
            new Label("Date :"),
            datePicker,
            new Label("Heure :"),
            timeBox,
            buttonsBox,
            messageLabel
        );

        Scene scene = new Scene(layout, 400, 350);
        modal.setScene(scene);
        modal.setMinWidth(400);
        modal.setMinHeight(350);
        modal.show();
    }

    /**
     * Ouvre une modale pour ajouter un arbitre au match
     */
    @FXML
    private void handleAddReferee() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner((Stage) rootContainer.getScene().getWindow());
        modal.setTitle("Ajouter un arbitre au match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Sélectionner un arbitre :");

        TextField emailSearchField = new TextField();
        emailSearchField.setPromptText("Rechercher un arbitre par email...");
        emailSearchField.setPrefWidth(300);

        ListView<User> userListView = new ListView<>();
        userListView.setPrefHeight(150);
        userListView.setVisible(false);
        userListView.setManaged(false);

        Label messageLabel = new Label();

        emailSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
                return;
            }

            List<User> users = matchController.searchUsersByEmail(newVal.trim());
            
            if (users.isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
            } else {
                userListView.getItems().setAll(users);
                userListView.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        setText(empty || user == null ? null : user.getEmail());
                    }
                });
                userListView.setVisible(true);
                userListView.setManaged(true);
            }
        });

        Button addButton = new Button("Ajouter");
        addButton.setDisable(true);

        userListView.setOnMouseClicked(event -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                emailSearchField.setText(selected.getEmail());
                addButton.setDisable(false);
            }
        });

        addButton.setOnAction(e -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                messageLabel.setText("❌ Veuillez sélectionner un arbitre");
                return;
            }

            boolean added = matchController.addRefereeToMatch(matchId, selected.getEmail(), messageLabel);
            
            if (added) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            modal.close();
                            loadMatchDetails(matchId);
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        Button closeButton = new Button("Annuler");
        closeButton.setOnAction(e -> modal.close());

        layout.getChildren().addAll(
            titleLabel,
            emailSearchField,
            userListView,
            addButton,
            closeButton,
            messageLabel
        );

        Scene scene = new Scene(layout, 800, 300);
            modal.setScene(scene);
            modal.setMinWidth(800);
            modal.setMinHeight(300);    
            modal.show();
    }

    /**
     * Ouvre la modale de mise à jour du roster pour l'équipe 1
     */
    @FXML
    private void handleUpdateRosterTeam1() {
        if (team1 != null) {
            matchController.openUpdateRosterModal(team1.getTeamId(), 
                (Stage) rootContainer.getScene().getWindow());
        }
    }

    /**
     * Ouvre la modale de mise à jour du roster pour l'équipe 2
     */
    @FXML
    private void handleUpdateRosterTeam2() {
        if (team2 != null) {
            matchController.openUpdateRosterModal(team2.getTeamId(), 
                (Stage) rootContainer.getScene().getWindow());
        }
    }

    @FXML
private void handleStartMatch() {
    if (matchId == null) {
        System.err.println("❌ Aucun match sélectionné");
        return;
    }

    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirmer le démarrage");
    confirmAlert.setHeaderText("Commencer le match ?");
    confirmAlert.setContentText("Le match passera en statut 'EN COURS' et les scores pourront être mis à jour en temps réel.");

    confirmAlert.showAndWait().ifPresent(response -> {
        if (response == ButtonType.OK) {
            Label tempLabel = new Label();
            boolean success = matchController.startMatch(matchId, tempLabel);
            
            if (success) {
                System.out.println("✅ Match démarré avec succès !");
                
                // Rafraîchir la vue
                loadMatchDetails(matchId);
                
                // Afficher une notification
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("Match démarré");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("Le match est maintenant EN COURS. Les scores peuvent être mis à jour.");
                infoAlert.show();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText("Impossible de démarrer le match");
                errorAlert.setContentText(tempLabel.getText());
                errorAlert.show();
            }
        }
    });
}
}