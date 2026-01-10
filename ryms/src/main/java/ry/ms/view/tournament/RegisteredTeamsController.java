package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentRegistration;
import ry.ms.businessLogic.tournament.TournamentFacade;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;
import ry.ms.persistLogic.tournament.postgres.TournamentRegistrationDAOPostgres;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contrôleur pour afficher la liste des équipes inscrites à un tournoi
 */
public class RegisteredTeamsController {

    @FXML
    private Label tournamentNameLabel;
    @FXML
    private Label registeredCountLabel;
    @FXML
    private TableView<TeamRegistrationInfo> registeredTeamsTable;
    @FXML
    private TableColumn<TeamRegistrationInfo, String> teamNameColumn;
    @FXML
    private TableColumn<TeamRegistrationInfo, String> teamTagColumn;
    @FXML
    private TableColumn<TeamRegistrationInfo, String> captainColumn;
    @FXML
    private TableColumn<TeamRegistrationInfo, Date> registrationDateColumn;
    @FXML
    private Button refreshButton;

    private Tournament tournament;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private TournamentFacade tournamentFacade = TournamentFacade.getInstance();
    private ry.ms.businessLogic.team.TeamFacade teamFacade = ry.ms.businessLogic.team.TeamFacade.getInstance();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("teamName"));
        teamTagColumn.setCellValueFactory(new PropertyValueFactory<>("teamTag"));
        captainColumn.setCellValueFactory(new PropertyValueFactory<>("captainName"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        registrationDateColumn.setCellFactory(column -> new TableCell<TeamRegistrationInfo, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
        if (tournament != null) {
            tournamentNameLabel.setText("Tournoi : " + tournament.getName());
            loadRegisteredTeams();
        }
    }

    private void loadRegisteredTeams() {
        if (tournament == null)
            return;

        try {
            List<TournamentRegistration> registrations = tournamentFacade
                    .getRegistrationsByTournament((long) tournament.getTournamentId());

            // Transformer les registrations en format d'affichage
            List<TeamRegistrationInfo> displayList = new ArrayList<>();
            int confirmedCount = 0;

            for (TournamentRegistration reg : registrations) {
                confirmedCount++;

                Team team = teamFacade.getTeamById(reg.getTeamId());
                String teamName = team != null ? team.getName() : "Unknown";
                String teamTag = team != null ? team.getTag() : "";
                String captainName = (team != null) ? team.getCaptainEmail() : "N/A";

                displayList.add(new TeamRegistrationInfo(
                        teamName,
                        teamTag,
                        captainName,
                        reg.getRegistrationDate()));
            }

            registeredTeamsTable.getItems().setAll(displayList);
            registeredCountLabel.setText(confirmedCount + "/" + tournament.getMaxParticipants() + " équipes inscrites");

        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les équipes inscrites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        loadRegisteredTeams();
    }

    @FXML
    private void handleBackToBracket() {
        navigateToView("/ry/ms/view/tournament/fxml/TournamentBracket.fxml", "Arbre du tournoi");
    }

    @FXML
    private void handleManageRegistrations() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ry/ms/view/tournament/fxml/ManageRegistrations.fxml"));
            Parent root = loader.load();

            ManageRegistrationsController controller = loader.getController();
            controller.setTournament(tournament);

            Stage stage = new Stage();
            stage.setTitle("Gérer les inscriptions - " + tournament.getName());
            stage.setScene(new Scene(root, 600, 500));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) registeredTeamsTable.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible d'ouvrir la gestion des inscriptions: " + e.getMessage());
        }
    }

    private void navigateToView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            TournamentBracketController controller = loader.getController();
            controller.setTournament(tournament);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1200, 800));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) registeredTeamsTable.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de naviguer: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Classe interne pour représenter les informations d'inscription d'une équipe
     */
    public static class TeamRegistrationInfo {
        private final String teamName;
        private final String teamTag;
        private final String captainName;
        private final Date registrationDate;

        public TeamRegistrationInfo(String teamName, String teamTag, String captainName,
                Date registrationDate) {
            this.teamName = teamName;
            this.teamTag = teamTag;
            this.captainName = captainName;
            this.registrationDate = registrationDate;
        }

        public String getTeamName() {
            return teamName;
        }

        public String getTeamTag() {
            return teamTag;
        }

        public String getCaptainName() {
            return captainName;
        }

        public Date getRegistrationDate() {
            return registrationDate;
        }
    }
}
