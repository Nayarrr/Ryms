package ry.ms.view.match.matchDetail;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ry.ms.businessLogic.match.models.TeamResult;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.view.match.MatchCache;
import ry.ms.view.match.MatchController;

/**
 * Manager pour la gestion des scores d'un match
 */
public class ScoreManager {

    private final MatchController matchController;
    private final BooleanSupplier isAdminOrReferee;

    public ScoreManager(MatchController matchController, BooleanSupplier isAdminOrReferee) {
        this.matchController = matchController;
        this.isAdminOrReferee = isAdminOrReferee;
    }

    /**
     * Charge et affiche les scores dans le conteneur
     */
    public void loadScores(VBox container, Long matchId, Team team1, Team team2, Consumer<Long> reloadCallback) {
        container.getChildren().clear();

        List<TeamResult> results = matchController.getMatchResults(matchId);

        if (results == null || results.isEmpty()) {
            displayNoScoresMessage(container, matchId, team1, team2, reloadCallback);
            return;
        }

        Label titleLabel = new Label("Scores");
        container.getChildren().add(titleLabel);

        for (TeamResult result : results) {
            HBox scoreBox = createScoreBox(matchId, result, reloadCallback);
            container.getChildren().add(scoreBox);
        }

        addFinalizeButton(container, matchId, results, reloadCallback);
    }

    /**
     * Affiche le message "Aucun score" avec bouton d'initialisation
     */
    private void displayNoScoresMessage(VBox container, Long matchId, Team team1, Team team2, Consumer<Long> reloadCallback) {
        Label titleLabel = new Label("Scores");
        container.getChildren().add(titleLabel);

        Label noScoresLabel = new Label("Les scores n'ont pas encore été initialisés pour ce match.");
        container.getChildren().add(noScoresLabel);

        if (isAdminOrReferee.getAsBoolean()) {
            Button initButton = new Button("Initialiser les scores");
            initButton.setOnAction(e -> handleInitializeScores(matchId, team1, team2, reloadCallback));
            container.getChildren().add(initButton);
        }
    }

    /**
     * Crée une boîte d'affichage de score pour une équipe
     */
    private HBox createScoreBox(Long matchId, TeamResult result, Consumer<Long> reloadCallback) {
        HBox box = new HBox(15);

        Label teamLabel = new Label(result.getTeam().getName() + " [" + result.getTeam().getTag() + "]");
        teamLabel.setPrefWidth(200);

        Label scoreLabel = new Label("Score: " + result.getScore());
        scoreLabel.setPrefWidth(100);

        String resultText = result.getResult() != null ? result.getResult().toString() : "En cours";
        Label resultLabel = new Label(resultText);

        box.getChildren().addAll(teamLabel, scoreLabel, resultLabel);

        if (isAdminOrReferee.getAsBoolean() && result.getResult() == null) {
            addScoreEditControls(box, matchId, result, reloadCallback);
        }

        return box;
    }

    /**
     * Ajoute les contrôles de modification de score
     */
    private void addScoreEditControls(HBox box, Long matchId, TeamResult result, Consumer<Long> reloadCallback) {
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
                    reloadCallback.accept(matchId);
                }
            } catch (NumberFormatException ex) {
                System.err.println("❌ Score invalide");
            }
        });

        box.getChildren().addAll(scoreField, updateButton);
    }

    /**
     * Ajoute le bouton de finalisation si nécessaire
     */
    private void addFinalizeButton(VBox container, Long matchId, List<TeamResult> results, Consumer<Long> reloadCallback) {
        if (!isAdminOrReferee.getAsBoolean()) {
            return;
        }

        boolean isFinalized = results.stream().anyMatch(r -> r.getResult() != null);

        if (!isFinalized) {
            Button finalizeButton = new Button("Finaliser le match");
            finalizeButton.setOnAction(e -> handleFinalizeMatch(matchId, reloadCallback));
            VBox.setMargin(finalizeButton, new Insets(10, 0, 0, 0));
            container.getChildren().add(finalizeButton);
        } else {
            Label finalizedLabel = new Label("Match finalisé");
            container.getChildren().add(finalizedLabel);
        }
    }

    /**
     * Initialise les scores à 0
     */
    private void handleInitializeScores(Long matchId, Team team1, Team team2, Consumer<Long> reloadCallback) {
        if (team1 == null || team2 == null) {
            System.err.println("❌ Impossible d'initialiser les scores : équipes non chargées");
            return;
        }

        Label messageLabel = new Label();
        boolean success1 = matchController.updateScore(matchId, team1.getTeamId(), 0, messageLabel);
        boolean success2 = matchController.updateScore(matchId, team2.getTeamId(), 0, messageLabel);

        if (success1 && success2) {
            System.out.println("✅ Scores initialisés pour le match " + matchId);
            reloadCallback.accept(matchId);
        } else {
            System.err.println("❌ Erreur lors de l'initialisation des scores");
        }
    }

    /**
     * Finalise le match
     */
    private void handleFinalizeMatch(Long matchId, Consumer<Long> reloadCallback) {
        Label tempLabel = new Label();
        boolean success = matchController.finalizeMatch(matchId, tempLabel);

        if (success) {
            // MISE À JOUR DU CACHE (déclenche l'Observable)
            MatchCache.getInstance().updateStatus(matchId, ry.ms.businessLogic.match.models.MatchStatus.FINISHED);
            
            reloadCallback.accept(matchId);
        } else {
            System.err.println("❌ Erreur finalisation : " + tempLabel.getText());
        }
    }
}