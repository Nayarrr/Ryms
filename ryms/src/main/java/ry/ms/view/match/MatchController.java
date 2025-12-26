package ry.ms.view.match;

import java.sql.SQLException;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import ry.ms.businessLogic.match.MatchFacade;
import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;

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

}
