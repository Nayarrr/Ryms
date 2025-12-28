package ry.ms.view.match;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.models.Team;

import java.util.List;

public class AssignTeamFrame {

    private final Stage ownerStage;
    private final MatchController controller;
    private final Long matchId;
    private final Runnable onTeamAssigned;

    public AssignTeamFrame(Stage ownerStage, Long matchId, Runnable onTeamAssigned) {
        this.ownerStage = ownerStage;
        this.controller = new MatchController();
        this.matchId = matchId;
        this.onTeamAssigned = onTeamAssigned;
    }

    public void show() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(ownerStage);
        modal.setTitle("Assigner une équipe au match #" + matchId);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        // Titre
        Label title = new Label("Assigner une équipe");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Info match
        Label matchInfo = new Label("Match ID: " + matchId);
        matchInfo.setFont(Font.font(14));
        matchInfo.setStyle("-fx-text-fill: gray;");

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        // Sélection d'équipe
        Label teamLabel = new Label("Sélectionner une équipe :");
        teamLabel.setFont(Font.font(14));
        
        ComboBox<Team> teamComboBox = new ComboBox<>();
        teamComboBox.setPrefWidth(300);
        teamComboBox.setPromptText("Choisir une équipe...");
        
        // Charger les équipes disponibles
        List<Team> teams = controller.getAllTeams();
        teamComboBox.getItems().addAll(teams);
        
        // Affichage personnalisé pour les équipes
        teamComboBox.setCellFactory(param -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getName() + " (" + team.getTag() + ")");
                }
            }
        });
        
        teamComboBox.setButtonCell(new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getName() + " (" + team.getTag() + ")");
                }
            }
        });

        // Message
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(300);

        // Boutons
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button assignButton = new Button("Assigner");
        assignButton.setPrefWidth(150);
        assignButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        assignButton.setOnAction(e -> {
            Team selectedTeam = teamComboBox.getValue();
            if (selectedTeam == null) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Veuillez sélectionner une équipe.");
                return;
            }
            
            boolean success = controller.handleAddTeamToMatch(matchId, selectedTeam.getTeamId(), messageLabel);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Équipe assignée !");
                alert.setContentText(selectedTeam.getName() + " a été assignée au match.");
                alert.showAndWait();
                
                if (onTeamAssigned != null) {
                    onTeamAssigned.run();
                }
                modal.close();
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setPrefWidth(150);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> modal.close());

        buttons.getChildren().addAll(assignButton, cancelButton);

        // Assemblage
        form.add(teamLabel, 0, 0);
        form.add(teamComboBox, 1, 0);

        root.getChildren().addAll(title, matchInfo, new Separator(), form, buttons, messageLabel);

        Scene scene = new Scene(root, 500, 350);
        modal.setScene(scene);
        modal.show();
    }
}