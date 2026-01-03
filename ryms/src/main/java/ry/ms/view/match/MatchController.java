package ry.ms.view.match;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.businessLogic.match.MatchFacade;
import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.team.TeamFacade;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.User;
import ry.ms.models.match.Match;
import ry.ms.models.match.TeamResult;
import ry.ms.models.team.Team;

public class MatchController {

    private final MatchFacade matchFacade;
    private final TeamFacade teamFacade;

    public MatchController() {
        this.matchFacade = MatchFacade.getMatchFacade();
        this.teamFacade = TeamFacade.getInstance();
    }

    /**
     * Crée un nouveau match complet avec deux équipes, une date, un jeu et des arbitres
     */
    public boolean createMatch(Team team1, Team team2, Date matchDate, int gameId, List<User> referees) {
        try {
            Long matchId = matchFacade.createMatch(team1.getTeamId(), team2.getTeamId(), matchDate, gameId);
            
            if (matchId == null) {
                System.err.println("❌ Erreur : Match non créé");
                return false;
            }

            if (referees != null && !referees.isEmpty()) {
                for (User referee : referees) {
                    try {
                        matchFacade.addReferee(matchId, referee.getEmail());
                        System.out.println("✅ Arbitre ajouté: " + referee.getEmail());
                    } catch (Exception e) {
                        System.err.println("⚠️ Erreur ajout arbitre " + referee.getEmail() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("✅ Match " + matchId + " créé entre " + team1.getName() + " et " + team2.getName());
            return true;

        } catch (Exception e) {
            System.err.println("❌ Erreur création match: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute un arbitre à un match existant via son email
     */
    public boolean addRefereeToMatch(Long matchId, String refereeEmail, Label messageLabel) {
        return executeWithExceptionHandling(
            () -> matchFacade.addReferee(matchId, refereeEmail),
            messageLabel,
            "✅ Arbitre ajouté avec succès !",
            "❌ Erreur lors de l'ajout de l'arbitre."
        );
    }

    /**
     * Gère l'action du bouton d'ajout d'arbitre en validant les champs de formulaire
     */
    public boolean handleAddRefereeButtonAction(TextField matchIdField, TextField emailField, Label messageLabel) {
        Long matchId = parseMatchId(matchIdField.getText(), messageLabel);
        if (matchId == null) return false;

        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        if (email.isBlank()) {
            showError(messageLabel, "Veuillez renseigner l'email de l'arbitre.");
            return false;
        }

        boolean success = addRefereeToMatch(matchId, email, messageLabel);
        if (success) emailField.clear();
        return success;
    }

    /**
     * Gère l'action du bouton de définition de date en récupérant la valeur du DatePicker
     */
    public boolean handleAddDateButtonAction(TextField matchIdField, DatePicker datePicker, Label messageLabel) {
        Long matchId = parseMatchId(matchIdField.getText(), messageLabel);
        if (matchId == null) return false;

        if (datePicker.getValue() == null) {
            showError(messageLabel, "Veuillez sélectionner une date.");
            return false;
        }

        Date date = java.sql.Date.valueOf(datePicker.getValue());
        return executeWithExceptionHandling(
            () -> matchFacade.addDate(matchId, date),
            messageLabel,
            "✅ Date du match mise à jour.",
            "❌ Impossible de mettre à jour la date."
        );
    }

    /**
     * Met à jour la date d'un match existant avec une nouvelle date
     */
    public boolean updateMatchDate(Long matchId, Date newDate, Label messageLabel) {
    return executeWithExceptionHandling(
        () -> matchFacade.addDate(matchId, newDate),
        messageLabel,
        "✅ Date du match mise à jour avec succès !",
        "❌ Erreur lors de la mise à jour de la date."
    );
}

    /**
     * Gère l'action du bouton d'ajout d'équipe à un match
     */
    public boolean handleAddTeamButtonAction(TextField matchIdField, TextField teamIdField, Label messageLabel) {
        Long matchId = parseMatchId(matchIdField.getText(), messageLabel);
        Long teamId = parseTeamId(teamIdField.getText(), messageLabel);
        
        if (matchId == null || teamId == null) return false;

        boolean success = executeWithExceptionHandling(
            () -> matchFacade.addTeam(matchId, teamId),
            messageLabel,
            "✅ Équipe ajoutée au match.",
            "❌ Erreur lors de l'ajout de l'équipe."
        );
        
        if (success) teamIdField.clear();
        return success;
    }

    /**
     * Ouvre une fenêtre modale pour remplacer un joueur d'une équipe par un autre
     */
    public void openUpdateRosterModal(Long teamId, Stage ownerStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(ownerStage);
        modal.setTitle("Mise à jour du roster - Équipe #" + teamId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        ComboBox<String> currentUserCombo = new ComboBox<>();
        currentUserCombo.setPromptText("Chargement...");
        currentUserCombo.setPrefWidth(300);

        TextField newEmailField = new TextField();
        newEmailField.setPromptText("Email du nouveau joueur");
        newEmailField.setPrefWidth(300);

        Button updateButton = new Button("Remplacer");
        updateButton.setDisable(true);

        Label messageLabel = new Label();

        loadTeamMembersIntoCombo(teamId, currentUserCombo, messageLabel, updateButton);

        currentUserCombo.setOnAction(e -> 
            updateButton.setDisable(currentUserCombo.getValue() == null || newEmailField.getText().isBlank())
        );

        newEmailField.textProperty().addListener((obs, oldVal, newVal) -> 
            updateButton.setDisable(currentUserCombo.getValue() == null || newVal.isBlank())
        );

        updateButton.setOnAction(e -> {
            if (performRosterUpdate(teamId, currentUserCombo.getValue(), newEmailField.getText(), messageLabel)) {
                loadTeamMembersIntoCombo(teamId, currentUserCombo, messageLabel, updateButton);
                newEmailField.clear();
            }
        });

        layout.getChildren().addAll(
            new Label("Sélectionnez le joueur à remplacer :"),
            new Label("Joueur actuel :"), currentUserCombo,
            new Label("Nouveau joueur :"), newEmailField,
            updateButton,
            createButton("Fermer", e -> modal.close()),
            messageLabel
        );

        modal.setScene(new Scene(layout, 400, 350));
        modal.show();
    }

    /**
     * Charge la liste des membres d'une équipe dans une ComboBox
     */
    private void loadTeamMembersIntoCombo(Long teamId, ComboBox<String> combo, Label messageLabel, Button updateButton) {
        try {
            List<User> members = matchFacade.getTeamMembers(teamId);
            combo.getItems().clear();
            
            if (members.isEmpty()) {
                showWarning(messageLabel, "Aucun membre dans cette équipe.");
                combo.setPromptText("Équipe vide");
                updateButton.setDisable(true);
                return;
            }

            members.forEach(m -> combo.getItems().add(m.getEmail()));
            combo.setPromptText("Sélectionner un joueur");
            showSuccess(messageLabel, members.size() + " membre(s) chargé(s).");
            
        } catch (SQLException e) {
            showError(messageLabel, "❌ Erreur base de données.");
            e.printStackTrace();
            updateButton.setDisable(true);
        }
    }

    /**
     * Effectue le remplacement d'un joueur dans le roster d'une équipe
     */
    private boolean performRosterUpdate(Long teamId, String currentEmail, String newEmail, Label messageLabel) {
        if (currentEmail == null || newEmail == null || newEmail.isBlank()) {
            showError(messageLabel, "Veuillez remplir tous les champs.");
            return false;
        }

        return executeWithExceptionHandling(
            () -> matchFacade.updateRoaster(teamId, currentEmail, newEmail.trim()),
            messageLabel,
            "✓ Roster mis à jour avec succès !",
            "❌ Erreur lors de la mise à jour."
        );
    }

    /**
     * Récupère la liste complète de toutes les équipes
     */
    public List<Team> getAllTeams() {
        return executeWithFallback(() -> teamFacade.getAllTeams(), "équipes");
    }

    /**
     * Récupère la liste complète de tous les matchs
     */
    public List<Match> getAllMatches() {
        return executeWithFallback(() -> matchFacade.getAllMatches(), "matchs");
    }

    /**
     * Récupère la liste complète de tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return executeWithFallback(() -> matchFacade.getAllUsers(), "utilisateurs");
    }

    /**
     * Récupère la liste des membres d'une équipe spécifique
     */
    public List<User> getTeamMembers(Long teamId) {
        return executeWithFallback(() -> matchFacade.getTeamMembers(teamId), "membres");
    }

    /**
     * Recherche des équipes par leur nom
     */
    public List<Team> searchTeamsByName(String searchTerm) {
        return executeWithFallback(() -> teamFacade.searchTeamsByName(searchTerm), "équipes");
    }

    /**
     * Recherche des utilisateurs par leur email
     */
    public List<User> searchUsersByEmail(String searchTerm) {
        return executeWithFallback(() -> matchFacade.searchUsersByEmail(searchTerm), "utilisateurs");
    }

    /**
     * Récupère un match spécifique par son identifiant
     */
    public Match getMatchById(Long matchId) {
        try {
            return matchFacade.getMatchById(matchId);
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération match " + matchId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère une équipe spécifique d'un match (1 = équipe 1, 2 = équipe 2)
     */
    public Team getTeamForMatch(Match match, int teamNumber) {
        if (match == null || match.getMatchId() == null) return null;

        try {
            List<Team> teams = matchFacade.getTeamsForMatch(match.getMatchId());
            return (teams != null && teams.size() > teamNumber - 1) ? teams.get(teamNumber - 1) : null;
        } catch (SQLException e) {
            System.err.println("❌ Erreur récupération équipe " + teamNumber + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Exécute une action avec gestion centralisée des exceptions et messages utilisateur
     */
    private boolean executeWithExceptionHandling(
            SupplierWithException<Boolean> action,
            Label messageLabel,
            String successMessage,
            String failureMessage) {
        try {
            boolean result = action.get();
            
            if (result) {
                showSuccess(messageLabel, successMessage);
            } else {
                showError(messageLabel, failureMessage);
            }
            return result;
            
        } catch (UserDoesntExistException e) {
            showError(messageLabel, "❌ Utilisateur introuvable.");
            return false;
        } catch (MatchDoesntExistException e) {
            showError(messageLabel, "❌ Match introuvable.");
            return false;
        } catch (TeamDoesntExistException e) {
            showError(messageLabel, "❌ Équipe introuvable.");
            return false;
        } catch (SQLException e) {
            showError(messageLabel, "❌ Erreur base de données: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            showError(messageLabel, "❌ Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Exécute une action retournant une liste et renvoie une liste vide en cas d'erreur
     */
    private <T> List<T> executeWithFallback(SupplierWithException<List<T>> action, String resourceName) {
        try {
            return action.get();
        } catch (Exception e) {
            System.err.println("❌ Erreur récupération " + resourceName + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Convertit une chaîne de caractères en identifiant de match avec validation
     */
    private Long parseMatchId(String text, Label messageLabel) {
        if (text == null || text.isBlank()) {
            showError(messageLabel, "Veuillez renseigner l'ID du match.");
            return null;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            showError(messageLabel, "ID du match invalide.");
            return null;
        }
    }

    /**
     * Convertit une chaîne de caractères en identifiant d'équipe avec validation
     */
    private Long parseTeamId(String text, Label messageLabel) {
        if (text == null || text.isBlank()) {
            showError(messageLabel, "Veuillez renseigner l'ID de l'équipe.");
            return null;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            showError(messageLabel, "ID de l'équipe invalide.");
            return null;
        }
    }

    /**
     * Affiche un message de succès en vert dans un label
     */
    private void showSuccess(Label label, String message) {
        label.setTextFill(Color.GREEN);
        label.setText(message);
    }

    /**
     * Affiche un message d'avertissement en orange dans un label
     */
    private void showWarning(Label label, String message) {
        label.setTextFill(Color.ORANGE);
        label.setText(message);
    }

    /**
     * Affiche un message d'erreur en rouge dans un label
     */
    private void showError(Label label, String message) {
        label.setTextFill(Color.RED);
        label.setText(message);
    }

    /**
     * Crée un bouton avec un texte et une action associée
     */
    private Button createButton(String text, EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(text);
        button.setOnAction(action);
        return button;
    }

    /**
     * Met à jour le score d'une équipe pour un match donné
     */
    public boolean updateScore(Long matchId, Long teamId, int score, Label messageLabel) {
        return executeWithExceptionHandling(
            () -> matchFacade.updateScore(matchId, teamId, score),
            messageLabel,
            "✅ Score mis à jour !",
            "❌ Erreur lors de la mise à jour du score."
        );
    }

    /**
     * Récupère les résultats (scores et statuts) de toutes les équipes d'un match
     */
    public List<TeamResult> getMatchResults(Long matchId) {
        return executeWithFallback(
            () -> matchFacade.getMatchResults(matchId), 
            "résultats"
        );
    }

    /**
     * Finalise un match en calculant automatiquement les résultats Win/Loss/Draw
     */
    public boolean finalizeMatch(Long matchId, Label messageLabel) {
        return executeWithExceptionHandling(
            () -> matchFacade.finalizeMatch(matchId),
            messageLabel,
            "✅ Match finalisé avec succès !",
            "❌ Erreur lors de la finalisation."
        );
    }

    /**
     * Supprime définitivement un match de la base de données
     */
    public boolean deleteMatch(Long matchId, Label messageLabel) {
        return executeWithExceptionHandling(
            () -> matchFacade.deleteMatch(matchId),
            messageLabel,
            "✅ Match supprimé avec succès !",
            "❌ Erreur lors de la suppression du match."
        );
    }

    public boolean startMatch(Long matchId, Label messageLabel) {
        return executeWithExceptionHandling(
            () -> matchFacade.startMatch(matchId),
            messageLabel,
            "✅ Match démarré avec succès !",
            "❌ Erreur lors du démarrage du match."
        );
    }
    
    //Interface avec une seule méthode abstraite (utilisable dans les lambdas) pour eviter les duplications de catch, message label, etc etc
    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }
}