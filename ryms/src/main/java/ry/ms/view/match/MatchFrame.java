package ry.ms.view.match;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MatchFrame {

    private final Stage stage;
    private final MatchController controller;
    private VBox matchListContainer; // Pour pouvoir rafraîchir la liste

    public MatchFrame(Stage stage) {
        this.stage = stage;
        this.controller = new MatchController();
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Titre
        Label title = new Label("Gestion des Matchs");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        BorderPane.setAlignment(title, Pos.CENTER);
        BorderPane.setMargin(title, new Insets(0, 0, 20, 0));
        root.setTop(title);

        // Centre : Bouton créer un match
        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.CENTER);

        Button createMatchButton = new Button("Créer un Match");
        createMatchButton.setFont(Font.font(16));
        createMatchButton.setPrefWidth(200);
        createMatchButton.setPrefHeight(50);
        createMatchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        createMatchButton.setOnAction(e -> {
            CreateMatchFrame createFrame = new CreateMatchFrame(stage, this::refreshMatchList);
            createFrame.show();
        });

        centerBox.getChildren().add(createMatchButton);
        root.setCenter(centerBox);

        // Droite : Liste des matchs à venir
        VBox rightPanel = createUpcomingMatchesPanel();
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 1000, 600);
        stage.setScene(scene);
        stage.setTitle("Gestion des Matchs");
        stage.show();
    }

    private VBox createUpcomingMatchesPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label panelTitle = new Label("Matchs à venir");
        panelTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Conteneur pour la liste de matchs
        matchListContainer = new VBox(10);
        loadMatchList();

        ScrollPane scrollPane = new ScrollPane(matchListContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        panel.getChildren().addAll(panelTitle, new Separator(), scrollPane);
        return panel;
    }

    /**
     * Charge la liste des matchs (données fictives pour l'instant)
     */
    private void loadMatchList() {
        matchListContainer.getChildren().clear();
        
        // TODO: Remplacer par des données réelles de la BDD
        // List<Match> matches = controller.getAllUpcomingMatches();
        
        // Données fictives temporaires
        matchListContainer.getChildren().addAll(
            createMatchCard("Team Alpha", "Team Beta", 1L),
            createMatchCard("Team Gamma", "Team Delta", 2L),
            createMatchCard("Team Epsilon", "Team Zeta", 3L)
        );
    }

    /**
     * Rafraîchit la liste des matchs
     */
    private void refreshMatchList() {
        loadMatchList();
        System.out.println("Liste des matchs rafraîchie");
    }

    private VBox createMatchCard(String team1Name, String team2Name, Long matchId) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label matchLabel = new Label(team1Name + " vs " + team2Name);
        matchLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label statusLabel = new Label("À venir");
        statusLabel.setStyle("-fx-text-fill: #ff9800; -fx-font-style: italic;");

        Button detailsButton = new Button("Voir les détails");
        detailsButton.setPrefWidth(Double.MAX_VALUE);
        detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> showMatchDetails(matchId, team1Name, team2Name));

        card.getChildren().addAll(matchLabel, statusLabel, detailsButton);
        return card;
    }

    private void showMatchDetails(Long matchId, String team1Name, String team2Name) {
        MatchDetailsFrame detailsFrame = new MatchDetailsFrame(stage, matchId, team1Name, team2Name);
        detailsFrame.show();
    }
}