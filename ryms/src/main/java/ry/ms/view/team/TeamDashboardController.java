package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;

public class TeamDashboardController {

    @FXML private Label teamNameLabel;
    @FXML private Label teamTagLabel;
    @FXML private TextField inviteEmailField;
    @FXML private ListView<String> membersList;
    @FXML private Label msgLabel;

    private Team currentTeam;
    private final TeamController controller = new TeamController();
    private final String myEmail = UserSession.getInstance().getUserEmail();

    public void initialize() {
        loadTeamData();
    }

    private void loadTeamData() {
        try {
            // Recharger l'équipe depuis la BDD
            this.currentTeam = new TeamDAOPostgres().getTeamByMemberEmail(myEmail);
            if (currentTeam != null) {
                teamNameLabel.setText(currentTeam.getName());
                teamTagLabel.setText("[" + currentTeam.getTag() + "]");
                membersList.getItems().setAll(currentTeam.getMemberEmails());
                
                // Indiquer le capitaine
                String captain = currentTeam.getCaptainEmail();
                // (Optionnel: ajouter une étoile à côté du capitaine dans la liste)
            }
        } catch (Exception e) {
            msgLabel.setText("Erreur chargement: " + e.getMessage());
        }
    }

    @FXML
    private void handleInvite() {
        String target = inviteEmailField.getText();
        if (target.isEmpty()) return;
        try {
            controller.inviteMember(currentTeam.getTeamId(), myEmail, target);
            msgLabel.setText("Invitation envoyée à " + target);
            inviteEmailField.clear();
        } catch (Exception e) {
            msgLabel.setText("Erreur invitation: " + e.getMessage());
        }
    }

    @FXML
    private void handleLeave() {
        try {
            controller.leaveTeam(currentTeam.getTeamId(), myEmail);
            // Ici, il faudrait notifier le MainLayout pour qu'il rebascule sur la vue "CreateTeam"
            msgLabel.setText("Vous avez quitté l'équipe. (Redémarrez pour voir)");
        } catch (Exception e) {
            msgLabel.setText("Erreur leave: " + e.getMessage());
        }
    }

    @FXML
    private void handleDissolve() {
        try {
            controller.dissolveTeam(currentTeam.getTeamId(), myEmail);
             msgLabel.setText("Équipe dissoute.");
        } catch (Exception e) {
             msgLabel.setText("Erreur: " + e.getMessage());
        }
    }
}