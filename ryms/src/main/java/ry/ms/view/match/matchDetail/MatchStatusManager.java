package ry.ms.view.match.matchDetail;

import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ry.ms.models.match.MatchStatus;
import ry.ms.view.match.MatchCache;
import ry.ms.view.match.MatchController;

/**
 * Manager pour la gestion du statut d'un match (démarrage)
 */
public class MatchStatusManager {

    private final MatchController matchController;

    public MatchStatusManager(MatchController matchController) {
        this.matchController = matchController;
    }

    /**
     * Gère le démarrage d'un match
     */
    public void handleStartMatch(Long matchId, Stage owner, Consumer<Long> reloadCallback) {
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
                    
                    // MISE À JOUR DU CACHE (déclenche l'Observable)
                    MatchCache.getInstance().updateStatus(matchId, MatchStatus.IN_PROGRESS);
                    
                    reloadCallback.accept(matchId);

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