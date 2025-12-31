package ry.ms.view.match;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import ry.ms.view.main.MainLayoutController;
import ry.ms.view.user.UserSession;

import java.text.SimpleDateFormat;
import java.util.List;

public class MatchDetailsController {

    @FXML private VBox rootContainer;
    @FXML private Label matchTitleLabel;
    @FXML private Label matchDateLabel;
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

    public void loadMatchDetails(Long matchId) {
        this.matchId = matchId;
        
        Match match = matchController.getMatchById(matchId);
        
        if (match == null) {
            matchTitleLabel.setText("❌ Match introuvable");
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

        // Équipes
        loadTeamDetails(team1, team1NameLabel, team1CoachLabel, team1RosterContainer, team1UpdateButton);
        loadTeamDetails(team2, team2NameLabel, team2CoachLabel, team2RosterContainer, team2UpdateButton);

        // Visibilité du bouton "Ajouter Arbitre"
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        addRefereeButton.setVisible(isAdmin);
        addRefereeButton.setManaged(isAdmin);
    }

    private void loadReferees(Match match) {
        refereesListContainer.getChildren().clear();

        if (match.getReferees() != null && !match.getReferees().isEmpty()) {
            for (User referee : match.getReferees()) {
                Label refLabel = new Label("• " + referee.getEmail());
                refLabel.setStyle("-fx-font-size: 14px;");
                refereesListContainer.getChildren().add(refLabel);
            }
        } else {
            Label noRefLabel = new Label("Aucun arbitre assigné");
            noRefLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            refereesListContainer.getChildren().add(noRefLabel);
        }
    }

    private void loadTeamDetails(Team team, Label nameLabel, Label coachLabel, VBox rosterContainer, Button updateButton) {
        if (team == null) {
            nameLabel.setText("En attente");
            coachLabel.setText("⭐ Coach : Non assigné");
            updateButton.setVisible(false);
            updateButton.setManaged(false);
            return;
        }

        nameLabel.setText(team.getName() + " [" + team.getTag() + "]");
        
        String coachEmail = team.getCaptainEmail() != null ? team.getCaptainEmail() : "Non assigné";
        coachLabel.setText("⭐ Coach : " + coachEmail);

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
    private void handleAddReferee() {
        // TODO: Implémenter la modale d'ajout d'arbitre
        System.out.println("TODO: Ajouter un arbitre au match #" + matchId);
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