package ry.ms.view.match;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MatchDetailsFrame {

    private final Stage stage;
    private final Long matchId;
    private final MatchController controller;

    public MatchDetailsFrame(Stage stage, Long matchId, String team1Name, String team2Name) {
        this.stage = stage;
        this.matchId = matchId;
        this.controller = new MatchController();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Bouton retour et bouton assigner équipe
        HBox topButtons = new HBox(15);
        topButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button backButton = new Button("← Retour");
        backButton.setOnAction(e -> {
            MatchFrame matchFrame = new MatchFrame(stage);
            matchFrame.show();
        });
        
        Button assignTeamButton = new Button("+ Assigner une équipe");
        assignTeamButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        assignTeamButton.setOnAction(e -> {
            AssignTeamFrame assignFrame = new AssignTeamFrame(stage, matchId, this::show);
            assignFrame.show();
        });
        
        topButtons.getChildren().addAll(backButton, assignTeamButton);

        // Récupérer les données du match
        Match match = controller.getMatchById(matchId);
        
        if (match == null) {
            Label errorLabel = new Label("Match introuvable");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px;");
            root.getChildren().addAll(backButton, errorLabel);
            
            Scene scene = new Scene(root, 900, 700);
            stage.setScene(scene);
            stage.setTitle("Erreur");
            stage.show();
            return;
        }

        // Titre : Team1 vs Team2
        String team1Name = "En attente";
        String team2Name = "En attente";
        
        if (match.getTeams() != null && !match.getTeams().isEmpty()) {
            if (match.getTeams().size() >= 1) {
                team1Name = match.getTeams().get(0).getName();
            }
            if (match.getTeams().size() >= 2) {
                team2Name = match.getTeams().get(1).getName();
            }
        }
        
        Label matchTitle = new Label(team1Name + " vs " + team2Name);
        matchTitle.setFont(Font.font("System", FontWeight.BOLD, 32));

        // Date et heure
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        String dateStr = match.getMatchDate() != null ? dateFormat.format(match.getMatchDate()) : "Date non définie";
        Label dateLabel = new Label("Date : " + dateStr);
        dateLabel.setFont(Font.font(16));

        // Liste des arbitres
        VBox refereesBox = new VBox(5);
        Label refereesTitle = new Label("Arbitres :");
        refereesTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        refereesBox.getChildren().add(refereesTitle);
        
        if (match.getReferees() != null && !match.getReferees().isEmpty()) {
            for (User referee : match.getReferees()) {
                Label refLabel = new Label("• " + referee.getEmail());
                refereesBox.getChildren().add(refLabel);
            }
        } else {
            Label noRefLabel = new Label("Aucun arbitre assigné");
            noRefLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            refereesBox.getChildren().add(noRefLabel);
        }

        // Compositions des équipes
        HBox teamsComposition = new HBox(20);
        teamsComposition.setAlignment(Pos.CENTER);

        if (match.getTeams() != null && !match.getTeams().isEmpty()) {
            for (Team team : match.getTeams()) {
                VBox teamBox = createTeamCompositionBox(team);
                teamsComposition.getChildren().add(teamBox);
            }
        } else {
            Label noTeamsLabel = new Label("Aucune équipe assignée");
            noTeamsLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 16px;");
            teamsComposition.getChildren().add(noTeamsLabel);
        }

        // Assemblage
        root.getChildren().addAll(
            topButtons,
            matchTitle,
            dateLabel,
            new Separator(),
            refereesBox,
            new Separator(),
            new Label("Compositions des équipes"),
            teamsComposition
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 900, 700);
        stage.setScene(scene);
        stage.setTitle("Détails du Match #" + matchId);
        stage.show();
    }

    private VBox createTeamCompositionBox(Team team) {
        VBox teamBox = new VBox(10);
        teamBox.setPadding(new Insets(15));
        teamBox.setPrefWidth(400);
        teamBox.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");

        // Nom de l'équipe
        Label teamNameLabel = new Label(team.getName());
        teamNameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        teamNameLabel.setStyle("-fx-text-fill: #2196F3;");

        // Coach
        HBox coachBox = new HBox(5);
        Label coachLabel = new Label("Coach :");
        coachLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        String coachEmail = team.getCaptainEmail() != null ? team.getCaptainEmail() : "Non assigné";
        Label coachName = new Label(coachEmail);
        coachBox.getChildren().addAll(coachLabel, coachName);

        // Roster
        Label rosterTitle = new Label("Roster :");
        rosterTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox rosterList = new VBox(5);
        rosterList.setPadding(new Insets(5, 0, 0, 15));
        
        try {
            List<User> members = controller.getTeamMembers(team.getTeamId());
            
            if (members.isEmpty()) {
                rosterList.getChildren().add(new Label("• Aucun membre"));
            } else {
                for (User member : members) {
                    rosterList.getChildren().add(new Label("• " + member.getEmail()));
                }
            }
        } catch (Exception e) {
            rosterList.getChildren().add(new Label("• Erreur de chargement"));
            e.printStackTrace();
        }

        teamBox.getChildren().addAll(
            teamNameLabel,
            new Separator(),
            coachBox,
            new Separator(),
            rosterTitle,
            rosterList
        );

        return teamBox;
    }
}