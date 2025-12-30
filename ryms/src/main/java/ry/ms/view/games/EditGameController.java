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

public class EditGameController {
    @FXML private TextField nameField;
    @FXML private TextField editorField;
    @FXML private DatePicker releaseDatePicker;

    private Game gameToEdit;
    private byte[] newLogoData = null;
    private GameCatalogFacade facade = GameCatalogFacade.getGameCatalogFactory();

    /** * Méthode appelée par le catalogue AVANT d'afficher la fenêtre
     */
    public void initData(Game game) {
        this.gameToEdit = game;
        nameField.setText(game.getName());
        editorField.setText(game.getEditor());

        if (game.getReleaseDate() != null) {
            // Correction ici : On cast en java.sql.Date pour utiliser .toLocalDate()
            // car game.getReleaseDate() renvoie un java.util.Date qui est l'ancêtre
            if (game.getReleaseDate() instanceof java.sql.Date) {
                java.sql.Date sqlDate = (java.sql.Date) game.getReleaseDate();
                releaseDatePicker.setValue(sqlDate.toLocalDate());
            } else {
                // Au cas où c'est un java.util.Date standard
                releaseDatePicker.setValue(game.getReleaseDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
            }
        }
    }

    @FXML
    private void handleSave() {
        if (editorField.getText().isEmpty() || releaseDatePicker.getValue() == null) {
            return; // Ajoutez une alerte d'erreur ici
        }

        // Mise à jour de l'objet
        gameToEdit.setEditor(editorField.getText());
        gameToEdit.setName(nameField.getText());
        Date date = Date.from(releaseDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        gameToEdit.setReleaseDate(date);

        if (newLogoData != null) {
            gameToEdit.setLogo(newLogoData);
        }

        facade.updateGame(gameToEdit);
        ((Stage) nameField.getScene().getWindow()).close();
    }

    @FXML
    private void handleSelectLogo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un nouveau logo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(nameField.getScene().getWindow());

        if (selectedFile != null) {
            try {
                // On stocke les nouvelles données dans la variable temporaire
                this.newLogoData = Files.readAllBytes(selectedFile.toPath());
                //fileNameLabel.setText(selectedFile.getName()); // Si vous avez gardé le label
            } catch (IOException e) {
                System.err.println("Erreur de lecture du fichier : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}