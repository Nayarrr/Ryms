package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.models.Team;
import ry.ms.view.user.UserSession;

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
    @FXML private Button createTeamButton;

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
                // L'utilisateur a une équipe
                teamNameLabel.setText(currentTeam.getName());
                teamTagLabel.setText("[" + currentTeam.getTag() + "]");
                
                // Afficher les membres avec * pour le capitaine
                List<String> members = new ArrayList<>(currentTeam.getMemberEmails());
                String captain = currentTeam.getCaptainEmail();
                List<String> displayMembers = members.stream()
                    .map(email -> email.equalsIgnoreCase(captain) ? email + " ⭐" : email)
                    .collect(java.util.stream.Collectors.toList());
                membersList.getItems().setAll(displayMembers);
                
                // Cacher le bouton "Créer une équipe"
                createTeamButton.setVisible(false);
                createTeamButton.setManaged(false);
                
            } else {
                // L'utilisateur n'a pas d'équipe
                teamNameLabel.setText("Aucune équipe");
                teamTagLabel.setText("");
                membersList.getItems().clear();
                
                // Afficher le bouton "Créer une équipe"
                createTeamButton.setVisible(true);
                createTeamButton.setManaged(true);
                
                msgLabel.setText("Vous n'appartenez à aucune équipe. Créez-en une ou attendez une invitation.");
            }
            
        } catch (Exception e) {
            msgLabel.setText("Erreur chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bouton "Créer une équipe"
     */
    @FXML
    private void handleCreateTeam() {
        try {
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Créer une équipe");

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/team/fxml/CreateTeam.fxml")
            );
            VBox root = loader.load();

            CreateTeamController createController = loader.getController();
            createController.setOnTeamCreated(() -> {
                loadTeamData(); // Rafraîchir après création
            });

            Scene scene = new Scene(root);
            modal.setScene(scene);
            modal.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            msgLabel.setText("Erreur lors de l'ouverture de la création d'équipe: " + e.getMessage());
        }
    }

    @FXML
    private void handleInvite() {
        if (currentTeam == null) {
            msgLabel.setText("Vous devez appartenir à une équipe pour inviter des membres.");
            return;
        }

        String target = inviteEmailField.getText();
        if (target == null || target.trim().isEmpty()) {
            msgLabel.setText("Veuillez saisir une adresse e-mail.");
            inviteEmailField.requestFocus();
            return;
        }
        
        if (!isValidEmail(target.trim())) {
            msgLabel.setText("Veuillez saisir une adresse e-mail valide.");
            inviteEmailField.requestFocus();
            return;
        }
        
        try {
            controller.inviteMember(currentTeam.getTeamId(), myEmail, target.trim());
            msgLabel.setText("✅ Invitation envoyée à " + target.trim());
            inviteEmailField.clear();
        } catch (Exception e) {
            msgLabel.setText("❌ Erreur invitation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @FXML
    private void handleLeave() {
        if (currentTeam == null) {
            msgLabel.setText("Vous n'appartenez à aucune équipe.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Quitter l'équipe " + currentTeam.getName() + " ?");
        confirm.setContentText("Êtes-vous sûr de vouloir quitter cette équipe ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.leaveTeam(currentTeam.getTeamId(), myEmail);
                    msgLabel.setText("✅ Vous avez quitté l'équipe.");
                    loadTeamData(); // Rafraîchir
                } catch (Exception e) {
                    msgLabel.setText("❌ Erreur: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void handleDissolve() {
        if (currentTeam == null) {
            msgLabel.setText("Vous n'appartenez à aucune équipe.");
            return;
        }

        if (!myEmail.equalsIgnoreCase(currentTeam.getCaptainEmail())) {
            msgLabel.setText("❌ Seul le capitaine peut dissoudre l'équipe.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.WARNING);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Dissoudre l'équipe " + currentTeam.getName() + " ?");
        confirm.setContentText("Cette action est IRRÉVERSIBLE et supprimera définitivement l'équipe.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    controller.dissolveTeam(currentTeam.getTeamId(), myEmail);
                    msgLabel.setText("✅ Équipe dissoute.");
                    loadTeamData(); // Rafraîchir
                } catch (Exception e) {
                    msgLabel.setText("❌ Erreur: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}