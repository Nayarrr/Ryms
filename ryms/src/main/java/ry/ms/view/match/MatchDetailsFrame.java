package ry.ms.view.match;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    private final String team1Name;
    private final String team2Name;

    public MatchDetailsFrame(Stage stage, Long matchId, String team1Name, String team2Name) {
        this.stage = stage;
        this.matchId = matchId;
        this.team1Name = team1Name;
        this.team2Name = team2Name;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Button backButton = new Button("← Retour");
        backButton.setOnAction(e -> {
            MatchFrame matchFrame = new MatchFrame(stage);
            matchFrame.show();
        });
        HBox backBox = new HBox(backButton);
        backBox.setAlignment(Pos.TOP_LEFT);

        // Titre : Team1 vs Team2
        Label matchTitle = new Label(team1Name + " vs " + team2Name);
        matchTitle.setFont(Font.font("System", FontWeight.BOLD, 32));

        // Date et heure
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm");
        Label dateLabel = new Label("Date : " + dateFormat.format(new Date())); // TODO: Récupérer depuis la BDD
        dateLabel.setFont(Font.font(16));

        // Liste des arbitres
        VBox refereesBox = new VBox(5);
        Label refereesTitle = new Label("Arbitres :");
        refereesTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // TODO: Récupérer depuis la BDD
        Label ref1 = new Label("• referee1@ryms.com");
        Label ref2 = new Label("• referee2@ryms.com");
        
        refereesBox.getChildren().addAll(refereesTitle, ref1, ref2);

        // Compositions des équipes
        HBox teamsComposition = new HBox(20);
        teamsComposition.setAlignment(Pos.CENTER);

        VBox team1Box = createTeamCompositionBox(team1Name, 1L);
        VBox team2Box = createTeamCompositionBox(team2Name, 2L);

        teamsComposition.getChildren().addAll(team1Box, team2Box);

        // Assemblage
        root.getChildren().addAll(
            backBox,
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

    private VBox createTeamCompositionBox(String teamName, Long teamId) {
        VBox teamBox = new VBox(10);
        teamBox.setPadding(new Insets(15));
        teamBox.setPrefWidth(400);
        teamBox.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #f9f9f9;");

        // Nom de l'équipe
        Label teamNameLabel = new Label(teamName);
        teamNameLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        teamNameLabel.setStyle("-fx-text-fill: #2196F3;");

        // Coach
        HBox coachBox = new HBox(5);
        Label coachLabel = new Label("Coach :");
        coachLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label coachName = new Label("coach@ryms.com"); // TODO: Récupérer depuis BDD
        coachBox.getChildren().addAll(coachLabel, coachName);

        // Roster
        Label rosterTitle = new Label("Roster :");
        rosterTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox rosterList = new VBox(5);
        rosterList.setPadding(new Insets(5, 0, 0, 15));
        
        // TODO: Récupérer depuis la BDD via getTeamMembers()
        rosterList.getChildren().addAll(
            new Label("• player1@ryms.com"),
            new Label("• player2@ryms.com"),
            new Label("• player3@ryms.com"),
            new Label("• player4@ryms.com"),
            new Label("• player5@ryms.com")
        );

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