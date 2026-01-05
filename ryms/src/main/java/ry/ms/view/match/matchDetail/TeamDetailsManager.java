package ry.ms.view.match.matchDetail;

import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;
import ry.ms.view.match.MatchController;

/**
 * Manager pour l'affichage des détails des équipes
 */
public class TeamDetailsManager {

    private final MatchController matchController;
    private final String currentUserEmail;

    public TeamDetailsManager(MatchController matchController, String currentUserEmail) {
        this.matchController = matchController;
        this.currentUserEmail = currentUserEmail;
    }

    /**
     * Charge les détails d'une équipe (nom, coach, roster)
     */
    public void loadTeamDetails(Team team, Label nameLabel, Label coachLabel, VBox rosterContainer, Button updateButton) {
        if (team == null) {
            displayEmptyTeam(nameLabel, coachLabel, updateButton);
            return;
        }

        nameLabel.setText(team.getName() + " [" + team.getTag() + "]");
        
        String coachEmail = team.getCaptainEmail() != null ? team.getCaptainEmail() : "Non assigné";
        coachLabel.setText("Coach : " + coachEmail);

        loadRoster(rosterContainer, team.getTeamId());
        
        configureUpdateButton(updateButton, coachEmail);
    }

    /**
     * Affiche une équipe vide
     */
    private void displayEmptyTeam(Label nameLabel, Label coachLabel, Button updateButton) {
        nameLabel.setText("En attente");
        coachLabel.setText("Coach : Non assigné");
        updateButton.setVisible(false);
        updateButton.setManaged(false);
    }

    /**
     * Charge le roster de l'équipe
     */
    private void loadRoster(VBox container, Long teamId) {
        container.getChildren().clear();

        try {
            List<User> members = matchController.getTeamMembers(teamId);

            if (members.isEmpty()) {
                container.getChildren().add(new Label("• Aucun membre"));
            } else {
                for (User member : members) {
                    container.getChildren().add(new Label("• " + member.getEmail()));
                }
            }
        } catch (Exception e) {
            container.getChildren().add(new Label("• Erreur de chargement"));
            e.printStackTrace();
        }
    }

    /**
     * Configure le bouton "Update Roster"
     */
    private void configureUpdateButton(Button updateButton, String coachEmail) {
        boolean canUpdate = currentUserEmail != null && 
                           (currentUserEmail.equalsIgnoreCase(coachEmail) || 
                            currentUserEmail.equalsIgnoreCase("admin@ryms.com"));
        updateButton.setVisible(canUpdate);
        updateButton.setManaged(canUpdate);
    }
}