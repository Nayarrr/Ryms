package ry.ms.view.match.matchDetail;

import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.businessLogic.user.models.User;
import ry.ms.view.match.MatchController;

/**
 * Manager pour la gestion des arbitres d'un match
 */
public class RefereeManager {

    private final MatchController matchController;

    public RefereeManager(MatchController matchController) {
        this.matchController = matchController;
    }

    /**
     * Charge la liste des arbitres dans le conteneur
     */
    public void loadReferees(VBox container, List<User> referees) {
        container.getChildren().clear();

        if (referees != null && !referees.isEmpty()) {
            for (User referee : referees) {
                Label refLabel = new Label("• " + referee.getUsername());
                container.getChildren().add(refLabel);
            }
        } else {
            Label noRefLabel = new Label("Aucun arbitre assigné");
            container.getChildren().add(noRefLabel);
        }
    }

    /**
     * Ouvre la modale d'ajout d'arbitre
     */
    public void openAddRefereeModal(Long matchId, Stage owner, Consumer<Long> reloadCallback) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(owner);
        modal.setTitle("Ajouter un arbitre au match #" + matchId);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label titleLabel = new Label("Sélectionner un arbitre :");

        TextField emailSearchField = new TextField();
        emailSearchField.setPromptText("Rechercher un arbitre par email...");
        emailSearchField.setPrefWidth(300);

        ListView<User> userListView = new ListView<>();
        userListView.setPrefHeight(150);
        userListView.setVisible(false);
        userListView.setManaged(false);

        Label messageLabel = new Label();

        emailSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
                return;
            }

            List<User> users = matchController.searchUsersByEmail(newVal.trim());

            if (users.isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
            } else {
                userListView.getItems().setAll(users);
                userListView.setCellFactory(lv -> new ListCell<>() {
                    @Override
                    protected void updateItem(User user, boolean empty) {
                        super.updateItem(user, empty);
                        setText(empty || user == null ? null : user.getEmail());
                    }
                });
                userListView.setVisible(true);
                userListView.setManaged(true);
            }
        });

        Button addButton = new Button("Ajouter");
        addButton.setDisable(true);

        userListView.setOnMouseClicked(event -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                emailSearchField.setText(selected.getEmail());
                addButton.setDisable(false);
            }
        });

        addButton.setOnAction(e -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                messageLabel.setText("❌ Veuillez sélectionner un arbitre");
                return;
            }

            boolean added = matchController.addRefereeToMatch(matchId, selected.getEmail(), messageLabel);

            if (added) {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            modal.close();
                            reloadCallback.accept(matchId);
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        });

        Button closeButton = new Button("Annuler");
        closeButton.setOnAction(e -> modal.close());

        layout.getChildren().addAll(titleLabel, emailSearchField, userListView, addButton, closeButton, messageLabel);

        Scene scene = new Scene(layout, 800, 300);
        modal.setScene(scene);
        modal.show();
    }
}