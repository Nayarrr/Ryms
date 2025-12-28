package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ry.ms.businessLogic.team.models.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TeamDashboardController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
            this.currentTeam = controller.getTeamByMemberEmail(myEmail);
            if (currentTeam != null) {
                teamNameLabel.setText(currentTeam.getName());
                teamTagLabel.setText("[" + currentTeam.getTag() + "]");
                
                // Mark the captain with an asterisk
                List<String> members = new ArrayList<>(currentTeam.getMemberEmails());
                String captain = currentTeam.getCaptainEmail();
                List<String> displayMembers = members.stream()
                    .map(email -> email.equalsIgnoreCase(captain) ? email + " *" : email)
                    .collect(java.util.stream.Collectors.toList());
                membersList.getItems().setAll(displayMembers);
            }
        } catch (Exception e) {
            msgLabel.setText("Erreur chargement: " + e.getMessage());
        }
    }

    @FXML
    private void handleInvite() {
        String target = inviteEmailField.getText();
        if (target == null || target.trim().isEmpty()) {
            msgLabel.setText("Veuillez saisir une adresse e-mail pour envoyer une invitation.");
            inviteEmailField.requestFocus();
            return;
        }
        
        // Basic email validation
        if (!isValidEmail(target.trim())) {
            msgLabel.setText("Veuillez saisir une adresse e-mail valide.");
            inviteEmailField.requestFocus();
            return;
        }
        
        try {
            controller.inviteMember(currentTeam.getTeamId(), myEmail, target.trim());
            msgLabel.setText("Invitation envoyée à " + target.trim());
            inviteEmailField.clear();
        } catch (Exception e) {
            msgLabel.setText("Erreur invitation: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation using pre-compiled pattern
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @FXML
    private void handleLeave() {
        try {
            controller.leaveTeam(currentTeam.getTeamId(), myEmail);
            // Clear the UI state to reflect the user has left the team
            currentTeam = null;
            teamNameLabel.setText("");
            teamTagLabel.setText("");
            membersList.getItems().clear();
            msgLabel.setText("Vous avez quitté l'équipe.");
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