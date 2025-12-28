package ry.ms.view.team;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;

public class CreateTeamController {

    @FXML private TextField nameField;
    @FXML private TextField tagField;
    @FXML private TextField avatarField;
    @FXML private Label errorLabel;

    private final TeamController teamController = new TeamController();

    @FXML
    private void handleCreateTeam() {
        String name = nameField.getText();
        String tag = tagField.getText();
        String avatar = avatarField.getText();
        String userEmail = UserSession.getInstance().getUserEmail();

        try {
            teamController.createTeam(name, tag, avatar, userEmail);
            var scene = nameField.getScene();
            if (scene != null) {
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/ry/ms/view/team/fxml/MainLayout.fxml"));
                    scene.setRoot(root);
                } catch (IOException ioEx) {
                    errorLabel.setText("Erreur : " + ioEx.getMessage());
                }
            }

        } catch (Exception e) {
            errorLabel.setText("Erreur : " + e.getMessage());
        }
    }
}