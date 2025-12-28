package ry.ms.view.match;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateMatchFrame {

    private final Stage ownerStage;
    private final MatchController controller;
    private final Runnable onMatchCreated;

    public CreateMatchFrame(Stage ownerStage, Runnable onMatchCreated) {
        this.ownerStage = ownerStage;
        this.controller = new MatchController();
        this.onMatchCreated = onMatchCreated;
    }

    public void show() {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(ownerStage);
        modal.setTitle("Créer un nouveau match");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        // Titre
        Label title = new Label("Créer un Match");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);

        Label dateLabel = new Label("Date du match :");
        dateLabel.setFont(Font.font(14));
        DatePicker datePicker = new DatePicker();
        datePicker.setPrefWidth(250);
        datePicker.setPromptText("Sélectionner une date");

        Label gameIdLabel = new Label("ID du jeu :");
        gameIdLabel.setFont(Font.font(14));
        TextField gameIdField = new TextField();
        gameIdField.setPrefWidth(250);
        gameIdField.setPromptText("Ex: 1");

        Label messageLabel = new Label();
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(300);

        // Boutons
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button createButton = new Button("Créer le match");
        createButton.setPrefWidth(150);
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        createButton.setOnAction(e -> {
            boolean success = controller.handleCreateMatchButtonAction(datePicker, gameIdField, messageLabel);
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Match créé !");
                alert.setContentText("Le match a été créé avec succès.");
                alert.showAndWait();
                
                if (onMatchCreated != null) {
                    onMatchCreated.run();
                }
                modal.close();
            }
        });

        Button cancelButton = new Button("Annuler");
        cancelButton.setPrefWidth(150);
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setOnAction(e -> modal.close());

        buttons.getChildren().addAll(createButton, cancelButton);

        // Assemblage du formulaire
        form.add(dateLabel, 0, 0);
        form.add(datePicker, 1, 0);
        form.add(gameIdLabel, 0, 1);
        form.add(gameIdField, 1, 1);

        root.getChildren().addAll(title, new Separator(), form, buttons, messageLabel);

        Scene scene = new Scene(root, 500, 400);
        modal.setScene(scene);
        modal.show();
    }
}