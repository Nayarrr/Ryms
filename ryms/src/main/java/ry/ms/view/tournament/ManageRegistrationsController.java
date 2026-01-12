package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentRegistration;
import ry.ms.businessLogic.team.TeamFacade;
import ry.ms.businessLogic.tournament.TournamentFacade;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Controller for managing tournament registrations.
 * Only team captains can register their teams.
 */
public class ManageRegistrationsController {

    @FXML
    private Label headerLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private ComboBox<Team> teamComboBox;
    @FXML
    private Label competitorsLabel;
    @FXML
    private Label errorLabel;

    private Tournament tournament;
    private TeamFacade teamFacade;
    private TournamentFacade tournamentFacade;
    private String currentUserEmail;

    /**
     * Initializes the controller.
     * Sets up facades, loads current user, and configures UI components.
     */
    @FXML
    public void initialize() {
        teamFacade = TeamFacade.getInstance();
        tournamentFacade = TournamentFacade.getInstance();

        // Récupérer l'email de l'utilisateur connecté depuis la session
        currentUserEmail = ry.ms.view.user.UserSession.getInstance().getUserEmail();

        // Configuration du ComboBox pour afficher le nom de l'équipe
        teamComboBox.setConverter(new javafx.util.StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getName() + " (" + team.getTag() + ")" : "";
            }

            @Override
            public Team fromString(String string) {
                return null;
            }
        });

        // Listener pour afficher les compétiteurs quand une équipe est sélectionnée
        teamComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayCompetitors(newVal);
            } else {
                competitorsLabel.setText("Aucune équipe sélectionnée");
            }
        });

        loadUserTeams();
    }

    /**
     * Sets the tournament for which registrations are being managed.
     * 
     * @param tournament The tournament instance.
     */
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
        if (tournament != null) {
            headerLabel.setText("Gérer les inscriptions - " + tournament.getName());
        }
    }

    /**
     * Charger les équipes dont l'utilisateur est capitaine
     */
    private void loadUserTeams() {
        try {
            List<Team> allTeams = teamFacade.getAllTeams();

            // Filtrer pour ne garder que les équipes dont l'utilisateur est capitaine
            List<Team> userTeams = allTeams.stream()
                    .filter(team -> currentUserEmail.equals(team.getCaptainEmail()))
                    .toList();

            teamComboBox.getItems().setAll(userTeams);

            if (userTeams.isEmpty()) {
                infoLabel.setText(
                        "Vous n'êtes capitaine d'aucune équipe. Seuls les chefs d'équipe peuvent inscrire leur équipe.");
                infoLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
        } catch (SQLException e) {
            showError("Erreur de chargement des équipes: " + e.getMessage());
        }
    }

    /**
     * Afficher les compétiteurs de l'équipe sélectionnée
     */
    private void displayCompetitors(Team team) {
        if (team.getMemberEmails() != null && !team.getMemberEmails().isEmpty()) {
            String competitors = String.join(", ", team.getMemberEmails());
            competitorsLabel.setText("Membres: " + competitors);
        } else {
            competitorsLabel.setText("Aucun membre dans cette équipe");
        }
    }

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);

        if (!validateForm()) {
            return;
        }

        Team selectedTeam = teamComboBox.getValue();

        try {
            // Vérifier si l'équipe est déjà inscrite
            if (tournamentFacade.isTeamRegistered((long) tournament.getTournamentId(), selectedTeam.getTeamId())) {
                showError("Cette équipe est déjà inscrite à ce tournoi");
                return;
            }

            // Créer l'inscription
            TournamentRegistration registration = new TournamentRegistration();
            registration.setTournamentId((long) tournament.getTournamentId());
            registration.setTeamId(selectedTeam.getTeamId());
            registration.setRegistrationDate(new Date());

            // Enregistrer l'inscription
            Long registrationId = tournamentFacade.registerTeam(registration);

            if (registrationId != null) {
                showSuccess("Inscription réussie",
                        "Votre équipe '" + selectedTeam.getName() + "' a été inscrite au tournoi '"
                                + tournament.getName() + "'");

                // Réinitialiser le formulaire
                teamComboBox.setValue(null);
            } else {
                showError("Erreur lors de l'inscription");
            }

        } catch (SQLException e) {
            showError("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) teamComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBackToList() {
        navigateToView("/ry/ms/view/tournament/fxml/TournamentList.fxml", "Liste des tournois");
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
                ((Stage) teamComboBox.getScene().getWindow()).close();
            } catch (IOException e) {
                showError("Impossible d'ouvrir le bracket: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleValidateResults() {
        showInfo("Information", "Fonctionnalité en cours de développement");
    }

    private boolean validateForm() {
        if (teamComboBox.getValue() == null) {
            showError("Veuillez sélectionner une équipe");
            return false;
        }

        if (tournament == null) {
            showError("Aucun tournoi sélectionné");
            return false;
        }

        return true;
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
            ((Stage) teamComboBox.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Impossible de naviguer: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
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
