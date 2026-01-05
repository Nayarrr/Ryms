package ry.ms.view.games;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ry.ms.businessLogic.games.GameCatalogFacade;
import ry.ms.businessLogic.games.models.Game;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.util.Date;

public class AddGameController {

    @FXML private TextField nameField;
    @FXML private TextField editorField;
    @FXML private DatePicker releaseDatePicker;
    @FXML private Label fileNameLabel;

    private byte[] logoData = null;
    private GameCatalogFacade facade = GameCatalogFacade.getGameCatalogFactory();

    @FXML
    private void handleSelectLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir le logo du jeu");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(nameField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                this.logoData = Files.readAllBytes(selectedFile.toPath());
                this.fileNameLabel.setText(selectedFile.getName());
            } catch (IOException e) {
                showError("Erreur lors de la lecture du fichier.");
            }
        }
    }

    @FXML
    private void handleSave() {
        // Validation simple
        if (nameField.getText().isEmpty() || editorField.getText().isEmpty() || releaseDatePicker.getValue() == null) {
            showError("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Conversion LocalDate -> Date
        Date date = Date.from(releaseDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Création de l'objet Game (le logo peut être null)
        Game newGame = new Game(nameField.getText(), editorField.getText(), date, logoData);

        try {
            facade.addGame(newGame); // Vous devez ajouter cette méthode dans votre Facade
            closeWindow();
        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}