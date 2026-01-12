package ry.ms.view.team;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ry.ms.view.user.UserSession;

/**
 * Controller for the Create Team view.
 */
public class CreateTeamController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField tagField;
    @FXML
    private TextField avatarField;
    @FXML
    private Label errorLabel;

    private final TeamController teamController = new TeamController();
    private Runnable onTeamCreated;

    /**
     * Sets the callback to be executed after a successful team creation.
     * 
     * @param callback The callback runnable.
     */
    public void setOnTeamCreated(Runnable callback) {
        this.onTeamCreated = callback;
    }

    @FXML
    private void handleCreateTeam() {
        String name = nameField.getText();
        String tag = tagField.getText();
        String avatar = avatarField.getText();

        // Validations
        if (name == null || name.trim().isEmpty()) {
            errorLabel.setText("Le nom de l'équipe est obligatoire.");
            return;
        }

        if (tag == null || tag.trim().isEmpty()) {
            errorLabel.setText("Le tag de l'équipe est obligatoire.");
            return;
        }

        if (tag.length() < 3 || tag.length() > 5) {
            errorLabel.setText("Le tag doit contenir entre 3 et 5 caractères.");
            return;
        }

        // Avatar par défaut si vide
        if (avatar == null || avatar.trim().isEmpty()) {
            avatar = "/images/default-team-avatar.png";
        }

        String userEmail = UserSession.getInstance().getUserEmail();
        if (userEmail == null) {
            errorLabel.setText("Erreur : utilisateur non connecté.");
            return;
        }

        try {
            // Créer l'équipe
            teamController.createTeam(name.trim(), tag.trim().toUpperCase(), avatar.trim(), userEmail);

            // Afficher confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Équipe créée !");
            alert.setContentText("L'équipe " + name + " [" + tag.toUpperCase() + "] a été créée avec succès.");
            alert.showAndWait();

            // Callback pour rafraîchir la liste
            if (onTeamCreated != null) {
                onTeamCreated.run();
            }

            // Fermer la modale
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Erreur : " + e.getMessage());
        }
    }
}