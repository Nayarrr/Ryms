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
import javafx.scene.control.Button;
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

    private MatchController matchController;
    private String currentUserEmail;
    private Long matchId;
    private Team team1;
    private Team team2;

    @FXML
    public void initialize() {
        matchController = new MatchController();
        currentUserEmail = UserSession.getInstance().getUserEmail();
    }

    private boolean isAdminOrReferee(){
        return currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
    }

    public void loadMatchDetails(Long matchId) {
        this.matchId = matchId;
        
        Match match = matchController.getMatchById(matchId);
        
        if (match == null) {
            matchTitleLabel.setText("Match introuvable");
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
        matchDateLabel.setText("Date : " + dateStr);

        // Arbitres
        loadReferees(match);

        // Équipes
        loadTeamDetails(team1, team1NameLabel, team1CoachLabel, team1RosterContainer, team1UpdateButton);
        loadTeamDetails(team2, team2NameLabel, team2CoachLabel, team2RosterContainer, team2UpdateButton);

        //Score
        loadScores(matchId);

        // Visibilité du bouton "Ajouter Arbitre"
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        addRefereeButton.setVisible(isAdmin);
        addRefereeButton.setManaged(isAdmin);
        editDateButton.setVisible(isAdmin);
        editDateButton.setManaged(isAdmin);
    }

    private void loadReferees(Match match) {
        refereesListContainer.getChildren().clear();

        if (match.getReferees() != null && !match.getReferees().isEmpty()) {
            for (User referee : match.getReferees()) {
                Label refLabel = new Label("• " + referee.getUsername());
                refLabel.setStyle("-fx-font-size: 14px;");
                refereesListContainer.getChildren().add(refLabel);
            }
        } else {
            Label noRefLabel = new Label("Aucun arbitre assigné");
            noRefLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            refereesListContainer.getChildren().add(noRefLabel);
        }
    }

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
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            scoresContainer.getChildren().add(titleLabel);

            Label noScoresLabel = new Label("Les scores n'ont pas encore été initialisés pour ce match.");
            noScoresLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 10 0 10 0;");
            scoresContainer.getChildren().add(noScoresLabel);

            // Bouton pour initialiser les scores (admin/arbitre uniquement)
            if (isAdminOrReferee()) {
                Button initButton = new Button("🎮 Initialiser les scores");
                initButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                initButton.setOnAction(e -> handleInitializeScores(matchId));
                scoresContainer.getChildren().add(initButton);
            }
            
            return;
        }

        // Titre
        Label titleLabel = new Label("📊 Scores");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
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
                Button finalizeButton = new Button("🏁 Finaliser le match");
                finalizeButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-margin-top: 10;");
                finalizeButton.setOnAction(e -> handleFinalizeMatch(matchId));
                
                VBox.setMargin(finalizeButton, new javafx.geometry.Insets(10, 0, 0, 0));
                scoresContainer.getChildren().add(finalizeButton);
            } else {
                Label finalizedLabel = new Label("✅ Match finalisé");
                finalizedLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
                scoresContainer.getChildren().add(finalizedLabel);
            }
        }
    }

    private HBox createScoreBox(Long matchId, TeamResult result) {
        HBox box = new HBox(15);
        box.setStyle("-fx-alignment: center-left; -fx-padding: 10; -fx-background-color: white; " +
                    "-fx-border-radius: 5; -fx-background-radius: 5;");

        // Nom de l'équipe
        Label teamLabel = new Label(result.getTeam().getName() + " [" + result.getTeam().getTag() + "]");
        teamLabel.setPrefWidth(200);
        teamLabel.setStyle("-fx-font-weight: bold;");

        // Score actuel
        Label scoreLabel = new Label("Score: " + result.getScore());
        scoreLabel.setPrefWidth(100);

        // Résultat (WIN/Loss/Draw ou "En cours")
        String resultText = result.getResult() != null ? result.getResult().toString() : "En cours";
        Label resultLabel = new Label(resultText);
        resultLabel.setStyle(getResultStyle(result.getResult()));

        box.getChildren().addAll(teamLabel, scoreLabel, resultLabel);

        // Si admin/arbitre ET match non finalisé : permettre la modification du score
        if (isAdminOrReferee() && result.getResult() == null) {
            TextField scoreField = new TextField(String.valueOf(result.getScore()));
            scoreField.setPrefWidth(60);
            scoreField.setPromptText("Score");

            Button updateButton = new Button("✓");
            updateButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
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

    private String getResultStyle(ry.ms.models.MatchResult result) {
        if (result == null) {
            return "-fx-text-fill: gray;";
        }
        
        switch(result) {
            case WIN:
                return "-fx-text-fill: green; -fx-font-weight: bold;";
            case LOSS:
                return "-fx-text-fill: red;";
            case DRAW:
                return "-fx-text-fill: orange;";
            default:
                return "-fx-text-fill: gray;";
        }
    }

    private void handleFinalizeMatch(Long matchId) {
        Label tempLabel = new Label();
        boolean success = matchController.finalizeMatch(matchId, tempLabel);
        
        if (success) {
            loadMatchDetails(matchId); // Recharger pour afficher les résultats
        } else {
            System.err.println("❌ Erreur finalisation : " + tempLabel.getText());
        }
    }

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

    @FXML
    private void handleEditDate() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner((Stage) rootContainer.getScene().getWindow());
        modal.setTitle("Modifier la date du match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Nouvelle date et heure du match :");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

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
        format24h.setStyle("-fx-text-fill: gray;");

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

        Button saveButton = new Button("💾 Enregistrer");
        saveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        saveButton.setOnAction(e -> {
            if (datePicker.getValue() == null) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ Veuillez sélectionner une date");
                return;
            }

            if (hourField.getText().isBlank() || minuteField.getText().isBlank()) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("❌ Veuillez spécifier l'heure");
                return;
            }

            try {
                int hour = Integer.parseInt(hourField.getText());
                int minute = Integer.parseInt(minuteField.getText());

                if (hour < 0 || hour > 23) {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("❌ L'heure doit être entre 0 et 23");
                    return;
                }

                if (minute < 0 || minute > 59) {
                    messageLabel.setStyle("-fx-text-fill: red;");
                    messageLabel.setText("❌ Les minutes doivent être entre 0 et 59");
                    return;
                }

                LocalDate localDate = datePicker.getValue();
                LocalDateTime localDateTime = localDate.atTime(hour, minute);
                Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                boolean updated = matchController.updateMatchDate(matchId, newDate, messageLabel);

                if (updated) {
                    messageLabel.setStyle("-fx-text-fill: green;");
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
                messageLabel.setStyle("-fx-text-fill: red;");
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

    @FXML
    private void handleAddReferee() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner((Stage) rootContainer.getScene().getWindow());
        modal.setTitle("Ajouter un arbitre au match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Sélectionner un arbitre :");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

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
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

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
                messageLabel.setStyle("-fx-text-fill: red;");
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

    @FXML
    private void handleUpdateRosterTeam1() {
        if (team1 != null) {
            matchController.openUpdateRosterModal(team1.getTeamId(), 
                (Stage) rootContainer.getScene().getWindow());
        }
    }

    @FXML
    private void handleUpdateRosterTeam2() {
        if (team2 != null) {
            matchController.openUpdateRosterModal(team2.getTeamId(), 
                (Stage) rootContainer.getScene().getWindow());
        }
    }





}