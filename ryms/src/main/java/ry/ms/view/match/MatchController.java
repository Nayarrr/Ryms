package ry.ms.view.match;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.User;

public class MatchController {

    private final MatchFacade matchFacade = MatchFacade.getMatchFacade();

    public boolean handleAddRefereeButtonAction(TextField matchIdField, TextField emailField, Label messageLabel) {

        String idText = matchIdField.getText();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();

        if (idText == null || idText.isBlank() || email.isBlank()) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Veuillez renseigner l'ID du match et l'email de l'arbitre.");
            return false;
        }

        Long matchId;
        try {
            matchId = Long.parseLong(idText.trim());
        } catch (NumberFormatException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("ID du match invalide.");
            return false;
        }

        try {
            boolean added = matchFacade.addReferee(matchId, email);
            if (added) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Arbitre ajouté au match.");
                // optionally clear the email field
                emailField.setText("");
                return true;
            } else {
                messageLabel.setTextFill(Color.ORANGE);
                messageLabel.setText("Cet arbitre est déjà assigné à ce match.");
                return false;
            }
        } catch (UserDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Utilisateur introuvable.");
        } catch (MatchDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Match introuvable.");
        } catch (SQLException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Erreur base de données.");
            e.printStackTrace();
        }

        return false;
    }

    public boolean handleAddDateButtonAction(TextField matchIdField, DatePicker datePicker, Label messageLabel) {
        String idText = matchIdField.getText();
        
        if (idText == null || idText.isBlank() || datePicker.getValue() == null) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Veuillez renseigner l'ID du match et la date.");
            return false;
        }

        Long matchId;
        try {
            matchId = Long.parseLong(idText.trim());
        } catch (NumberFormatException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("ID du match invalide.");
            return false;
        }

        // Convertir LocalDate en Date
        Date date = java.sql.Date.valueOf(datePicker.getValue());

        try {
            boolean updated = matchFacade.addDate(matchId, date);
            if (updated) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Date du match mise à jour.");
                return true;
            } else {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Impossible de mettre à jour la date.");
                return false;
            }
        } catch (MatchDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Match introuvable.");
        } catch (SQLException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Erreur base de données.");
            e.printStackTrace();
        }

        return false;
    }

    public boolean handleAddTeamButtonAction(TextField matchIdField, TextField teamIdField, Label messageLabel) {
        String matchIdText = matchIdField.getText();
        String teamIdText = teamIdField.getText();
        
        if (matchIdText == null || matchIdText.isBlank() || teamIdText == null || teamIdText.isBlank()) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Veuillez renseigner l'ID du match et l'ID de l'équipe.");
            return false;
        }

        Long matchId;
        Long teamId;
        
        try {
            matchId = Long.parseLong(matchIdText.trim());
        } catch (NumberFormatException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("ID du match invalide.");
            return false;
        }
        
        try {
            teamId = Long.parseLong(teamIdText.trim());
        } catch (NumberFormatException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("ID de l'équipe invalide.");
            return false;
        }

        try {
            boolean added = matchFacade.addTeam(matchId, teamId);
            if (added) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("Équipe ajoutée au match.");
                teamIdField.setText("");
                return true;
            } else {
                messageLabel.setTextFill(Color.ORANGE);
                messageLabel.setText("Cette équipe est déjà assignée à ce match.");
                return false;
            }
        } catch (TeamDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Équipe introuvable.");
        } catch (MatchDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Match introuvable.");
        } catch (SQLException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Erreur base de données.");
            e.printStackTrace();
        }

        return false;
    }

    public void openUpdateRosterModal(Long teamId, Stage ownerStage) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(ownerStage);
        modal.setTitle("Mise à jour du roster - Équipe #" + teamId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Sélectionnez le joueur à remplacer :");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> currentUserCombo = new ComboBox<>();
        currentUserCombo.setPromptText("Chargement...");
        currentUserCombo.setPrefWidth(300);

        TextField newEmailField = new TextField();
        newEmailField.setPromptText("Email du nouveau joueur");
        newEmailField.setPrefWidth(300);

        Button updateButton = new Button("Remplacer");
        updateButton.setDisable(true);

        Label messageLabel = new Label();

        // Charger automatiquement les membres au démarrage
        loadTeamMembersIntoCombo(teamId, currentUserCombo, messageLabel, updateButton);

        // Activer le bouton quand un joueur est sélectionné
        currentUserCombo.setOnAction(e -> {
            updateButton.setDisable(currentUserCombo.getValue() == null || newEmailField.getText().isBlank());
        });

        newEmailField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateButton.setDisable(currentUserCombo.getValue() == null || newVal.isBlank());
        });

        updateButton.setOnAction(e -> {
            boolean success = performRosterUpdate(teamId, currentUserCombo.getValue(), 
                                                  newEmailField.getText(), messageLabel);
            if (success) {
                // Recharger la liste après mise à jour
                loadTeamMembersIntoCombo(teamId, currentUserCombo, messageLabel, updateButton);
                newEmailField.clear();
            }
        });

        Button closeButton = new Button("Fermer");
        closeButton.setOnAction(e -> modal.close());

        layout.getChildren().addAll(
            titleLabel, 
            new Label("Joueur actuel :"),
            currentUserCombo, 
            new Label("Nouveau joueur :"),
            newEmailField, 
            updateButton, 
            closeButton,
            messageLabel
        );

        Scene scene = new Scene(layout, 400, 350);
        modal.setScene(scene);
        modal.show();
    }

    private void loadTeamMembersIntoCombo(Long teamId, ComboBox<String> combo, Label messageLabel, Button updateButton) {
        try {
            List<User> members = matchFacade.getTeamMembers(teamId);
            
            combo.getItems().clear();
            
            if (members.isEmpty()) {
                messageLabel.setTextFill(Color.ORANGE);
                messageLabel.setText("Aucun membre dans cette équipe.");
                combo.setPromptText("Équipe vide");
                updateButton.setDisable(true);
                return;
            }

            for (User member : members) {
                combo.getItems().add(member.getEmail());
            }
            
            combo.setPromptText("Sélectionner un joueur");
            messageLabel.setTextFill(Color.GREEN);
            messageLabel.setText(members.size() + " membre(s) chargé(s).");
            
        } catch (TeamDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Équipe introuvable.");
            combo.setPromptText("Erreur");
        } catch (SQLException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Erreur base de données.");
            combo.setPromptText("Erreur");
            e.printStackTrace();
        }
    }

    private boolean performRosterUpdate(Long teamId, String currentEmail, String newEmail, Label messageLabel) {
        if (currentEmail == null || newEmail == null || newEmail.isBlank()) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Veuillez remplir tous les champs.");
            return false;
        }

        try {
            boolean updated = matchFacade.updateRoaster(teamId, currentEmail, newEmail.trim());
            if (updated) {
                messageLabel.setTextFill(Color.GREEN);
                messageLabel.setText("✓ Roster mis à jour avec succès !");
                return true;
            } else {
                messageLabel.setTextFill(Color.ORANGE);
                messageLabel.setText("Le nouveau joueur est déjà dans l'équipe.");
                return false;
            }
        } catch (UserDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Utilisateur introuvable.");
        } catch (TeamDoesntExistException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Équipe introuvable.");
        } catch (SQLException e) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Erreur base de données.");
            e.printStackTrace();
        }
        return false;
    }



}
