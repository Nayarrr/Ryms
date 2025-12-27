package ry.ms.view.match;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MatchFrame {

    private final Stage stage;
    private final MatchController controller;

    public MatchFrame(Stage stage) {
        this.stage = stage;
        this.controller = new MatchController();
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Titre
        Label title = new Label("Gestion des Matchs");
        title.setFont(new Font("System Bold", 24));

        // Message global
        Label messageLabel = new Label();
        messageLabel.setWrapText(true);

        // --- Section 1: Ajouter un arbitre ---
        VBox refereeSection = createRefereeSection(messageLabel);

        // --- Section 2: Ajouter une date ---
        VBox dateSection = createDateSection(messageLabel);

        // --- Section 3: Ajouter une équipe ---
        VBox teamSection = createTeamSection(messageLabel);

        // --- Section 4: Mise à jour du roster ---
        VBox rosterSection = createRosterSection();

        // Bouton retour
        Button backButton = new Button("← Retour au menu principal");
        backButton.setOnAction(e -> {
            // TODO: Retourner au MainFrame
            System.out.println("Retour au menu principal");
        });

        // Séparateurs
        Separator sep1 = new Separator();
        Separator sep2 = new Separator();
        Separator sep3 = new Separator();

        root.getChildren().addAll(
            title,
            new Separator(),
            refereeSection,
            sep1,
            dateSection,
            sep2,
            teamSection,
            sep3,
            rosterSection,
            messageLabel,
            backButton
        );

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 700, 700);
        stage.setScene(scene);
        stage.setTitle("Gestion des Matchs");
        stage.show();
    }

    private VBox createRefereeSection(Label messageLabel) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label sectionTitle = new Label("Ajouter un arbitre au match");
        sectionTitle.setFont(new Font("System Bold", 16));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label matchIdLabel = new Label("ID du match:");
        TextField matchIdField = new TextField();
        matchIdField.setPromptText("Ex: 1");

        Label emailLabel = new Label("Email de l'arbitre:");
        TextField emailField = new TextField();
        emailField.setPromptText("Ex: referee@ryms.com");

        Button addButton = new Button("Ajouter l'arbitre");
        addButton.setOnAction(e -> 
            controller.handleAddRefereeButtonAction(matchIdField, emailField, messageLabel)
        );

        grid.add(matchIdLabel, 0, 0);
        grid.add(matchIdField, 1, 0);
        grid.add(emailLabel, 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(addButton, 1, 2);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createDateSection(Label messageLabel) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label sectionTitle = new Label("Définir la date du match");
        sectionTitle.setFont(new Font("System Bold", 16));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label matchIdLabel = new Label("ID du match:");
        TextField matchIdField = new TextField();
        matchIdField.setPromptText("Ex: 1");

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();

        Button setDateButton = new Button("Définir la date");
        setDateButton.setOnAction(e -> 
            controller.handleAddDateButtonAction(matchIdField, datePicker, messageLabel)
        );

        grid.add(matchIdLabel, 0, 0);
        grid.add(matchIdField, 1, 0);
        grid.add(dateLabel, 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(setDateButton, 1, 2);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createTeamSection(Label messageLabel) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label sectionTitle = new Label("Ajouter une équipe au match");
        sectionTitle.setFont(new Font("System Bold", 16));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label matchIdLabel = new Label("ID du match:");
        TextField matchIdField = new TextField();
        matchIdField.setPromptText("Ex: 1");

        Label teamIdLabel = new Label("ID de l'équipe:");
        TextField teamIdField = new TextField();
        teamIdField.setPromptText("Ex: 5");

        Button addTeamButton = new Button("Ajouter l'équipe");
        addTeamButton.setOnAction(e -> 
            controller.handleAddTeamButtonAction(matchIdField, teamIdField, messageLabel)
        );

        grid.add(matchIdLabel, 0, 0);
        grid.add(matchIdField, 1, 0);
        grid.add(teamIdLabel, 0, 1);
        grid.add(teamIdField, 1, 1);
        grid.add(addTeamButton, 1, 2);

        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private VBox createRosterSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label sectionTitle = new Label("Mise à jour du roster d'une équipe");
        sectionTitle.setFont(new Font("System Bold", 16));

        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        Label teamIdLabel = new Label("ID de l'équipe:");
        TextField teamIdField = new TextField();
        teamIdField.setPromptText("Ex: 5");
        teamIdField.setPrefWidth(100);

        Button updateRosterButton = new Button("Ouvrir la gestion du roster");
        updateRosterButton.setOnAction(e -> {
            try {
                Long teamId = Long.parseLong(teamIdField.getText().trim());
                controller.openUpdateRosterModal(teamId, stage);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("ID invalide");
                alert.setContentText("Veuillez entrer un ID d'équipe valide.");
                alert.showAndWait();
            }
        });

        hbox.getChildren().addAll(teamIdLabel, teamIdField, updateRosterButton);
        section.getChildren().addAll(sectionTitle, hbox);
        return section;
    }
}