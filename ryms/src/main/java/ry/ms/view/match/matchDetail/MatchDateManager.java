package ry.ms.view.match.matchDetail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.view.match.MatchController;

/**
 * Manager pour la modification de la date d'un match
 */
public class MatchDateManager {

    private final MatchController matchController;

    public MatchDateManager(MatchController matchController) {
        this.matchController = matchController;
    }

    /**
     * Ouvre la modale de modification de date
     */
    public void openEditDateModal(Long matchId, Stage owner, Consumer<Long> reloadCallback) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(owner);
        modal.setTitle("Modifier la date du match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Nouvelle date et heure du match :");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Sélectionner une date");
        datePicker.setPrefWidth(250);

        HBox timeBox = createTimeInput();
        TextField hourField = (TextField) timeBox.getChildren().get(0);
        TextField minuteField = (TextField) timeBox.getChildren().get(2);

        Label messageLabel = new Label();

        Button saveButton = new Button("Enregistrer");
        saveButton.setOnAction(e -> handleSaveDate(matchId, datePicker, hourField, minuteField, messageLabel, modal, reloadCallback));

        Button cancelButton = new Button("Annuler");
        cancelButton.setOnAction(e -> modal.close());

        HBox buttonsBox = new HBox(10, saveButton, cancelButton);

        layout.getChildren().addAll(titleLabel, new Label("Date :"), datePicker, new Label("Heure :"), timeBox, buttonsBox, messageLabel);

        Scene scene = new Scene(layout, 400, 350);
        modal.setScene(scene);
        modal.show();
    }

    /**
     * Crée les champs de saisie de l'heure
     */
    private HBox createTimeInput() {
        HBox timeBox = new HBox(10);

        TextField hourField = new TextField();
        hourField.setPromptText("HH");
        hourField.setPrefWidth(60);
        hourField.setMaxWidth(60);

        Label separator = new Label(":");

        TextField minuteField = new TextField();
        minuteField.setPromptText("MM");
        minuteField.setPrefWidth(60);
        minuteField.setMaxWidth(60);

        Label format24h = new Label("(format 24h)");

        // Validation numérique
        hourField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                hourField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                hourField.setText(newVal.substring(0, 2));
            }
        });

        minuteField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                minuteField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 2) {
                minuteField.setText(newVal.substring(0, 2));
            }
        });

        timeBox.getChildren().addAll(hourField, separator, minuteField, format24h);
        return timeBox;
    }

    /**
     * Gère la sauvegarde de la nouvelle date
     */
    private void handleSaveDate(Long matchId, DatePicker datePicker, TextField hourField, TextField minuteField, 
                                Label messageLabel, Stage modal, Consumer<Long> reloadCallback) {
        if (datePicker.getValue() == null) {
            messageLabel.setText("Veuillez sélectionner une date");
            return;
        }

        if (hourField.getText().isBlank() || minuteField.getText().isBlank()) {
            messageLabel.setText("Veuillez spécifier l'heure");
            return;
        }

        try {
            int hour = Integer.parseInt(hourField.getText());
            int minute = Integer.parseInt(minuteField.getText());

            if (hour < 0 || hour > 23) {
                messageLabel.setText("L'heure doit être entre 0 et 23");
                return;
            }

            if (minute < 0 || minute > 59) {
                messageLabel.setText("Les minutes doivent être entre 0 et 59");
                return;
            }

            LocalDate localDate = datePicker.getValue();
            LocalDateTime localDateTime = localDate.atTime(hour, minute);
            Date newDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

            boolean updated = matchController.updateMatchDate(matchId, newDate, messageLabel);

            if (updated) {
                messageLabel.setText("Date mise à jour avec succès !");

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Platform.runLater(() -> {
                            modal.close();
                            reloadCallback.accept(matchId);
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }

        } catch (NumberFormatException ex) {
            messageLabel.setText("Heure ou minute invalide");
        }
    }
}