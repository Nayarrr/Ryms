package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ry.ms.businessLogic.match.MatchFacade;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.match.models.TeamResult;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.tournament.TournamentFacade;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.persistLogic.tournament.postgres.TournamentRegistrationDAOPostgres;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TournamentBracketController {

    @FXML
    private Label tournamentNameLabel;
    @FXML
    private Label tournamentGameLabel;
    @FXML
    private Label tournamentDateLabel;
    @FXML
    private Label tournamentLocationLabel;
    @FXML
    private ComboBox<ry.ms.businessLogic.tournament.models.TournamentStatus> statusComboBox;
    @FXML
    private Label participantsLabel;
    @FXML
    private VBox bracketContainer;
    @FXML
    private ScrollPane bracketScrollPane;
    @FXML
    private VBox matchDetailsPanel;
    @FXML
    private VBox matchDetailsContent;
    @FXML
    private Button generateBracketButton;
    @FXML
    private Button generateBracketButtonCenter;
    @FXML
    private Label winnerLabel;
    @FXML
    private VBox generateBracketMessage;

    private Tournament tournament;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private Match selectedMatch;
    private MatchFacade matchFacade = MatchFacade.getMatchFacade();
    private TournamentFacade tournamentFacade = TournamentFacade.getInstance();

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
        loadTournamentInfo();

        // Charger automatiquement le bracket si des matchs existent
        loadBracketIfExists();
    }

    /**
     * Charger le bracket automatiquement s'il existe des matchs
     */
    private void loadBracketIfExists() {
        if (tournament == null)
            return;

        try {
            // Recharger les matchs depuis la base de données
            List<Match> matches = matchFacade.getMatchesByTournament(tournament.getTournamentId());

            if (matches != null && !matches.isEmpty()) {
                tournament.setMatches(matches);
                loadBracket();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des matchs: " + e.getMessage());
            e.printStackTrace();
        }

        // Mettre à jour l'état des boutons et afficher le vainqueur après le chargement
        // potentiel des matchs
        updateGenerateBracketButtons();
        if (tournament.getStatus() == ry.ms.businessLogic.tournament.models.TournamentStatus.COMPLETED) {
            displayWinner();
        }
    }

    private void loadTournamentInfo() {
        tournamentNameLabel.setText(tournament.getName());

        if (tournament.getGame() != null) {
            tournamentGameLabel.setText(tournament.getGame().getName());
        }

        if (tournament.getStartDate() != null) {
            tournamentDateLabel.setText(dateFormat.format(tournament.getStartDate()));
        }

        if (tournament.getLocation() != null) {
            tournamentLocationLabel.setText(tournament.getLocation());
        }

        if (statusComboBox != null) {
            statusComboBox.getItems().setAll(ry.ms.businessLogic.tournament.models.TournamentStatus.values());
            statusComboBox.setValue(tournament.getStatus());
        }

        // Mettre à jour le statut du tournoi selon les dates
        updateTournamentStatus();

        // Afficher le nombre réel de participants inscrits
        if (participantsLabel != null) {
            try {
                int registeredCount = tournamentFacade.getRegisteredTeamsCount((long) tournament.getTournamentId());
                participantsLabel.setText(registeredCount + "/" + tournament.getMaxParticipants() + " participants");

                // Mettre à jour l'état des boutons de génération
                updateGenerateBracketButtons();
            } catch (SQLException e) {
                participantsLabel.setText("?/" + tournament.getMaxParticipants() + " participants");
                e.printStackTrace();
            }
        }
    }

    /**
     * Met à jour le statut du tournoi selon les dates
     */
    private void updateTournamentStatus() {
        if (tournament == null)
            return;

        try {
            java.time.LocalDate now = java.time.LocalDate.now();
            java.time.LocalDate startDate = tournament.getStartDate()
                    .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate endDate = tournament.getEndDate()
                    .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            ry.ms.businessLogic.tournament.models.TournamentStatus currentStatus = tournament.getStatus();
            ry.ms.businessLogic.tournament.models.TournamentStatus newStatus = currentStatus;

            // Déterminer le nouveau statut
            if (now.isBefore(startDate)) {
                newStatus = ry.ms.businessLogic.tournament.models.TournamentStatus.PLANNING;
            } else if (now.isAfter(endDate)) {
                newStatus = ry.ms.businessLogic.tournament.models.TournamentStatus.COMPLETED;
            } else if (!now.isBefore(startDate) && !now.isAfter(endDate)) {
                if (currentStatus == ry.ms.businessLogic.tournament.models.TournamentStatus.PLANNING) {
                    newStatus = ry.ms.businessLogic.tournament.models.TournamentStatus.IN_PROGRESS;
                }
            }

            // Mettre à jour si le statut a changé
            if (newStatus != currentStatus) {
                tournament.setStatus(newStatus);
                if (statusComboBox != null) {
                    statusComboBox.setValue(newStatus);
                }
                System.out.println("Statut du tournoi mis à jour: " + newStatus);

                // Persister le changement via la Facade
                try {
                    tournamentFacade.updateTournament(tournament);
                    System.out.println("Nouveau statut sauvegardé en BDD");
                } catch (SQLException e) {
                    System.err.println("Erreur sauvegarde statut: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Afficher le vainqueur si le tournoi est terminé
            if (newStatus == ry.ms.businessLogic.tournament.models.TournamentStatus.COMPLETED) {
                displayWinner();
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }

    private void displayWinner() {
        if (winnerLabel == null)
            return;

        try {
            List<Match> matches = tournament.getMatches();
            if (matches != null && !matches.isEmpty()) {
                Match finalMatch = matches.get(matches.size() - 1);

                if (finalMatch.getStatus() == ry.ms.businessLogic.match.models.MatchStatus.FINISHED) {
                    List<ry.ms.businessLogic.match.models.TeamResult> results = matchFacade
                            .getMatchResults(finalMatch.getMatchId());

                    if (results != null) {
                        Team winner = null;
                        int maxScore = -1;

                        for (ry.ms.businessLogic.match.models.TeamResult result : results) {
                            if (result.getScore() > maxScore) {
                                maxScore = result.getScore();
                                winner = result.getTeam();
                            }
                        }

                        if (winner != null) {
                            winnerLabel.setText("🏆 Vainqueur : " + winner.getName());
                            winnerLabel.setVisible(true);
                            winnerLabel.setManaged(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Impossible d'afficher le vainqueur: " + e.getMessage());
        }
    }

    /**
     * Vérifier si le bracket peut être généré
     */
    private boolean canGenerateBracket() {
        if (tournament == null) {
            return false;
        }

        try {
            int registeredCount = tournamentFacade.getRegisteredTeamsCount((long) tournament.getTournamentId());
            int maxParticipants = tournament.getMaxParticipants();

            // Le bracket peut être généré seulement si toutes les places sont remplies
            return registeredCount == maxParticipants && maxParticipants > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mettre à jour l'état des boutons de génération de bracket
     */
    private void updateGenerateBracketButtons() {
        boolean canGenerate = canGenerateBracket();
        boolean bracketExists = tournament != null && tournament.getMatches() != null
                && !tournament.getMatches().isEmpty();

        // Cacher les boutons si le bracket existe déjà
        if (generateBracketButton != null) {
            generateBracketButton.setDisable(!canGenerate);
            generateBracketButton.setVisible(!bracketExists);
            generateBracketButton.setManaged(!bracketExists);
        }
        if (generateBracketButtonCenter != null) {
            generateBracketButtonCenter.setDisable(!canGenerate);
            generateBracketButtonCenter.setVisible(!bracketExists);
            generateBracketButtonCenter.setManaged(!bracketExists);
        }

        // Hide/Show message
        if (generateBracketMessage != null) {
            generateBracketMessage.setVisible(!bracketExists);
            generateBracketMessage.setManaged(!bracketExists);
        }
    }

    @FXML
    private void handleStatusChange() {
        if (tournament == null || statusComboBox == null || statusComboBox.getValue() == null)
            return;

        ry.ms.businessLogic.tournament.models.TournamentStatus newStatus = statusComboBox.getValue();

        if (newStatus != tournament.getStatus()) {
            tournament.setStatus(newStatus);
            System.out.println("Statut modifié manuellement: " + newStatus);

            try {
                tournamentFacade.updateTournament(tournament);
                System.out.println("Statut sauvegardé en BDD");

                // Mettre à jour l'affichage du vainqueur si nécessaire
                if (newStatus == ry.ms.businessLogic.tournament.models.TournamentStatus.COMPLETED) {
                    displayWinner();
                } else if (winnerLabel != null) {
                    winnerLabel.setVisible(false);
                    winnerLabel.setManaged(false);
                }
            } catch (SQLException e) {
                System.err.println("Erreur sauvegarde statut: " + e.getMessage());
                e.printStackTrace();
                // Revert si erreur ?
            }
        }
    }

    /**
     * Gérer la génération du bracket
     */
    @FXML
    private void handleGenerateBracket() {
        if (!canGenerateBracket()) {
            try {
                int registeredCount = tournamentFacade.getRegisteredTeamsCount((long) tournament.getTournamentId());
                int maxParticipants = tournament.getMaxParticipants();

                showError("Bracket incomplet",
                        "Impossible de générer le bracket.\n" +
                                "Équipes inscrites: " + registeredCount + "/" + maxParticipants + "\n" +
                                "Toutes les places doivent être remplies avant de générer le bracket.");
            } catch (SQLException e) {
                showError("Erreur", "Impossible de vérifier les inscriptions.");
            }
            return;
        }

        loadBracket();
    }

    private void loadBracket() {
        bracketContainer.getChildren().clear();

        int maxParticipants = tournament.getMaxParticipants();
        int totalRounds = (int) (Math.log(maxParticipants) / Math.log(2));

        HBox bracketLayout = new HBox(50);
        bracketLayout.setAlignment(Pos.CENTER);
        bracketLayout.setPadding(new Insets(20));

        List<Match> allMatches = tournament.getMatches() != null ? tournament.getMatches() : new ArrayList<>();
        int matchCounter = 0;

        for (int r = 0; r < totalRounds; r++) {
            int matchesInThisColumn = (int) (maxParticipants / Math.pow(2, r + 1));

            VBox column = new VBox(20);
            column.setAlignment(Pos.CENTER);

            Label roundTitle = new Label("Étape " + (r + 1));
            roundTitle.setStyle("-fx-font-weight: bold;");
            column.getChildren().add(roundTitle);

            for (int i = 0; i < matchesInThisColumn; i++) {
                if (matchCounter < allMatches.size()) {
                    column.getChildren().add(createMatchBox(allMatches.get(matchCounter)));
                } else {
                    column.getChildren().add(createEmptyMatchSlot(matchCounter));
                }
                matchCounter++;
            }
            bracketLayout.getChildren().add(column);
        }

        bracketContainer.getChildren().add(bracketLayout);

        // Afficher le bracket
        if (bracketScrollPane != null) {
            bracketScrollPane.setVisible(true);
        }
    }

    private VBox createEmptyMatchSlot(int slotIndex) {
        VBox slot = new VBox();
        slot.setAlignment(Pos.CENTER);
        slot.setMinWidth(180);
        slot.setMinHeight(70);
        slot.setStyle("-fx-border-color: #dfe6e9; -fx-border-style: dashed; " +
                "-fx-border-radius: 8; -fx-background-color: #ffffff;");

        Label lbl = new Label("+ Créer Match");
        lbl.setStyle("-fx-text-fill: #b2bec3; -fx-font-size: 11px;");

        slot.getChildren().add(lbl);
        slot.setOnMouseClicked(e -> handleAddMatch(slotIndex));

        slot.setOnMouseEntered(e -> slot.setStyle(slot.getStyle() + "-fx-border-color: #74b9ff;"));
        slot.setOnMouseExited(e -> slot
                .setStyle(slot.getStyle().replace("-fx-border-color: #74b9ff;", "-fx-border-color: #dfe6e9;")));

        return slot;
    }

    private VBox createMatchBox(Match match) {
        VBox matchBox = new VBox(5);
        matchBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; " +
                "-fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; " +
                "-fx-padding: 10; -fx-min-width: 200;");

        Label dateLabel = new Label(
                match.getMatchDate() != null ? dateFormat.format(match.getMatchDate()) : "Date TBD");
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

        VBox teamsBox = new VBox(5);

        // Récupérer les noms des équipes depuis le match
        List<Team> teams = match.getTeams();
        String team1Name = (teams != null && teams.size() > 0) ? teams.get(0).getName() : "Équipe 1";
        String team2Name = (teams != null && teams.size() > 1) ? teams.get(1).getName() : "Équipe 2";

        // Récupérer les scores réels depuis la base de données
        String team1Score = "0";
        String team2Score = "0";

        try {
            List<ry.ms.businessLogic.match.models.TeamResult> results = matchFacade.getMatchResults(match.getMatchId());
            if (results != null && !results.isEmpty()) {
                for (ry.ms.businessLogic.match.models.TeamResult result : results) {
                    if (teams != null && teams.size() > 0
                            && result.getTeam().getTeamId().equals(teams.get(0).getTeamId())) {
                        team1Score = String.valueOf(result.getScore());
                    } else if (teams != null && teams.size() > 1
                            && result.getTeam().getTeamId().equals(teams.get(1).getTeamId())) {
                        team2Score = String.valueOf(result.getScore());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des scores: " + e.getMessage());
        }

        HBox team1Box = createTeamRow(team1Name, team1Score);
        teamsBox.getChildren().add(team1Box);

        Separator separator = new Separator();
        teamsBox.getChildren().add(separator);

        HBox team2Box = createTeamRow(team2Name, team2Score);
        teamsBox.getChildren().add(team2Box);

        Label statusLabel = new Label(match.getStatus().toString());
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666; -fx-padding: 5 0 0 0;");

        matchBox.getChildren().addAll(dateLabel, teamsBox, statusLabel);

        matchBox.setOnMouseClicked(e -> selectMatch(match));
        matchBox.setOnMouseEntered(e -> matchBox.setStyle(matchBox.getStyle() + "-fx-cursor: hand;"));

        return matchBox;
    }

    private HBox createTeamRow(String teamName, String score) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(teamName);
        nameLabel.setStyle("-fx-font-size: 12px;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        Label scoreLabel = new Label(score);
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        row.getChildren().addAll(nameLabel, scoreLabel);
        return row;
    }

    private void selectMatch(Match match) {
        selectedMatch = match;
        if (matchDetailsPanel != null) {
            matchDetailsPanel.setVisible(true);

            matchDetailsContent.getChildren().clear();

            Label matchIdLabel = new Label("Match #" + match.getMatchId());
            matchIdLabel.setStyle("-fx-font-weight: bold;");

            Label dateLabel = new Label("Date: "
                    + (match.getMatchDate() != null ? dateFormat.format(match.getMatchDate()) : "Non définie"));

            Label statusLabel = new Label("Statut: " + match.getStatus());

            matchDetailsContent.getChildren().addAll(matchIdLabel, dateLabel, statusLabel);
        }
    }

    @FXML
    private void handleAddMatch() {
        handleAddMatch(-1);
    }

    private void handleAddMatch(int slotIndex) {
        if (tournament == null) {
            showError("Erreur", "Aucun tournoi sélectionné.");
            return;
        }

        // Si on clique sur un slot spécifique qui nécessite des placeholders
        if (slotIndex >= 0) {
            List<Match> currentMatches = tournament.getMatches();
            int currentSize = currentMatches != null ? currentMatches.size() : 0;

            if (slotIndex > currentSize) {
                // Créer automatiquement les matchs manquants pour maintenir l'alignement
                try {
                    for (int i = currentSize; i < slotIndex; i++) {
                        // Créer un match placeholder
                        java.time.LocalDate placeholderDate = tournament.getStartDate() != null ? tournament
                                .getStartDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                                : java.time.LocalDate.now();

                        matchFacade.createMatch(
                                java.sql.Date.valueOf(placeholderDate),
                                tournament.getTournamentId());
                    }
                    // Rafraîchir la liste après création des placeholders
                    handleRefresh();
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de créer les matchs intermédiaires: " + e.getMessage());
                    return;
                }
            }
        }

        // Ouvrir le formulaire de création pour le match cible
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ry/ms/view/match/fxml/CreateMatchView.fxml"));
            Parent root = loader.load();

            ry.ms.view.match.CreateMatchController controller = loader.getController();

            // Définir le contexte du tournoi
            controller.setTournamentContext(tournament);

            // Définir le callback pour rafraîchir le bracket après création
            controller.setOnMatchCreated(() -> handleRefresh());

            Stage stage = new Stage();
            stage.setTitle("Créer un Match - " + tournament.getName());
            stage.setScene(new Scene(root));
            controller.setModalStage(stage);

            stage.showAndWait();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire de création: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        if (tournament != null) {
            try {
                // Recharger les matchs depuis la base de données
                List<Match> matches = matchFacade.getMatchesByTournament(tournament.getTournamentId());

                if (matches != null && !matches.isEmpty()) {
                    tournament.setMatches(matches);
                    loadBracket();
                } else {
                    System.out.println("Aucun match trouvé pour ce tournoi");
                }
            } catch (Exception e) {
                showError("Erreur", "Impossible de rafraîchir les matchs: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleEditMatch() {
        if (selectedMatch != null) {
            openScoreModificationDialog(selectedMatch);
        } else {
            showError("Erreur", "Veuillez sélectionner un match");
        }
    }

    /**
     * Ouvre une dialog pour modifier les scores du match
     */
    private void openScoreModificationDialog(Match match) {
        try {
            // Récupérer les équipes du match
            List<Team> teams = matchFacade.getTeamsForMatch(match.getMatchId());

            if (teams == null || teams.size() < 2) {
                showError("Erreur", "Impossible de charger les équipes du match");
                return;
            }

            Team team1 = teams.get(0);
            Team team2 = teams.get(1);

            // Créer la dialog
            Stage dialog = new Stage();
            dialog.setTitle("Modifier les scores - Match #" + match.getMatchId());

            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: white;");

            Label titleLabel = new Label("Modification des scores");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            VBox scoresContainer = new VBox(10);

            // Utiliser le ScoreManager existant
            ry.ms.view.match.MatchController matchController = new ry.ms.view.match.MatchController();
            ry.ms.view.match.matchDetail.ScoreManager scoreManager = new ry.ms.view.match.matchDetail.ScoreManager(
                    matchController,
                    () -> true // Toujours autoriser la modification dans le contexte tournoi
            );

            // Charger les scores
            scoreManager.loadScores(scoresContainer, match.getMatchId(), team1, team2, (matchId) -> {
                // Callback de rechargement
                try {
                    handleRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(e -> {
                dialog.close();
                handleRefresh(); // Rafraîchir le bracket après fermeture
            });

            content.getChildren().addAll(titleLabel, scoresContainer, closeButton);

            Scene scene = new Scene(content, 500, 400);
            dialog.setScene(scene);
            dialog.show();

        } catch (Exception e) {
            showError("Erreur", "Impossible d'ouvrir la modification des scores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToList() {
        // Fermer simplement la fenêtre du bracket pour revenir à la liste principale
        if (tournamentNameLabel.getScene() != null && tournamentNameLabel.getScene().getWindow() != null) {
            ((Stage) tournamentNameLabel.getScene().getWindow()).close();
        }
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
                ((Stage) tournamentNameLabel.getScene().getWindow()).close();
            } catch (IOException e) {
                showError("Erreur", "Impossible d'ouvrir les inscriptions: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewRegisteredTeams() {
        if (tournament != null) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ry/ms/view/tournament/fxml/RegisteredTeams.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Équipes inscrites - " + tournament.getName());
                stage.setScene(new Scene(root, 800, 600));

                RegisteredTeamsController controller = loader.getController();
                controller.setTournament(tournament);

                stage.show();

                // Fermer la fenêtre actuelle
                ((Stage) tournamentNameLabel.getScene().getWindow()).close();
            } catch (IOException e) {
                showError("Erreur", "Impossible d'ouvrir la liste des équipes: " + e.getMessage());
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
            ((Stage) tournamentNameLabel.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Erreur", "Impossible de naviguer: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}