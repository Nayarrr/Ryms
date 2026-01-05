package ry.ms.view.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainFrame {

    private final Stage stage;
    private Runnable onMatchManagementClick;
    private Runnable onTeamManagementClick;
    private Runnable onTournamentClick;
    private Runnable onProfileClick;

    public MainFrame(Stage stage) {
        this.stage = stage;
    }

    /**
     * Affiche la page principale après connexion
     */
    public void show() {
        Label welcomeTitle = new Label("Connexion Réussie ! 🎉");
        welcomeTitle.setFont(new Font("System Bold", 24));

        Label welcomeText = new Label("Bienvenue dans l'application principale.");

        // Boutons de navigation vers les différentes sections
        Button matchManagementButton = new Button("Gestion des Matchs");
        matchManagementButton.setPrefWidth(200);
        matchManagementButton.setOnAction(e -> {
            if (onMatchManagementClick != null) {
                onMatchManagementClick.run();
            }
        });

        Button teamManagementButton = new Button("Gestion des Équipes");
        teamManagementButton.setPrefWidth(200);
        teamManagementButton.setOnAction(e -> {
            if (onTeamManagementClick != null) {
                onTeamManagementClick.run();
            }
        });

        Button tournamentButton = new Button("Tournois");
        tournamentButton.setPrefWidth(200);
        tournamentButton.setOnAction(e -> {
            if (onTournamentClick != null) {
                onTournamentClick.run();
            }
        });

        Button profileButton = new Button("Mon Profil");
        profileButton.setPrefWidth(200);
        profileButton.setOnAction(e -> {
            if (onProfileClick != null) {
                onProfileClick.run();
            }
        });

        VBox mainRoot = new VBox(20);
        mainRoot.setPadding(new Insets(50));
        mainRoot.setAlignment(Pos.CENTER);
        mainRoot.getChildren().addAll(
            welcomeTitle, 
            welcomeText,
            matchManagementButton,
            teamManagementButton,
            tournamentButton,
            profileButton
        );

        Scene mainScene = new Scene(mainRoot, 800, 600);
        stage.setScene(mainScene);
        stage.setTitle("Ryms - Application Principale");
        stage.show();
    }

    // Setters pour les callbacks
    public void setOnMatchManagementClick(Runnable callback) {
        this.onMatchManagementClick = callback;
    }

    public void setOnTeamManagementClick(Runnable callback) {
        this.onTeamManagementClick = callback;
    }

    public void setOnTournamentClick(Runnable callback) {
        this.onTournamentClick = callback;
    }

    public void setOnProfileClick(Runnable callback) {
        this.onProfileClick = callback;
    }
}