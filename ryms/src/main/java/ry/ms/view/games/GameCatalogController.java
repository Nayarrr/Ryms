package ry.ms.view.games;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import ry.ms.businessLogic.games.GameCatalogFacade;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class GameCatalogController {

    @FXML
    private FlowPane gameGrid;
    @FXML
    private Button addGameBtn; // Assumes fx:id="addGameBtn" is in FXML
    private final GameCatalogFacade facade = GameCatalogFacade.getGameCatalogFactory();

    @FXML
    public void initialize() {
        User currentUser = SessionFacade.getSessionFactory().getCurrentUser();
        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

        if (addGameBtn != null) {
            addGameBtn.setVisible(isAdmin);
            addGameBtn.setManaged(isAdmin);
        }

        renderAllCards(isAdmin);

        facade.loadGameCatalog().addListener((ListChangeListener<Game>) change -> {
            Platform.runLater(() -> renderAllCards(isAdmin));
        });
    }

    private void renderAllCards(boolean isAdmin) {
        gameGrid.getChildren().clear();
        for (Game game : facade.loadGameCatalog()) {
            gameGrid.getChildren().add(createGameCard(game, isAdmin));
        }
    }

    private VBox createGameCard(Game game, boolean isAdmin) {
        // 1. Conteneur principal
        VBox card = new VBox(15);
        card.setPrefSize(220, 320);
        card.setMinSize(220, 320);
        card.setMaxSize(220, 320);
        card.setAlignment(Pos.TOP_CENTER);

        String baseStyle = "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15;";
        String hoverStyle = "-fx-background-color: white; -fx-border-color: #3498db; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 15; -fx-cursor: hand;";
        card.setStyle(baseStyle);

        // Effet Hover
        DropShadow shadow = new DropShadow(10, Color.rgb(0, 0, 0, 0.15));
        card.setOnMouseEntered(e -> {
            card.setStyle(hoverStyle);
            card.setEffect(shadow);
            card.setTranslateY(-5);
        });
        card.setOnMouseExited(e -> {
            card.setStyle(baseStyle);
            card.setEffect(null);
            card.setTranslateY(0);
        });

        ImageView imageView = new ImageView();
        double imgW = 190, imgH = 110;
        if (game.getLogo() != null && game.getLogo().length > 0) {
            imageView.setImage(new Image(new ByteArrayInputStream(game.getLogo())));
        }
        imageView.setFitWidth(imgW);
        imageView.setFitHeight(imgH);
        imageView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(imgW, imgH);
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        imageView.setClip(clip);

        // 3. Infos
        Label nameLabel = new Label(game.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 4. Boutons (Only show logic if Admin)
        if (isAdmin) {
            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER);

            Button editBtn = new Button("Edit");
            editBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
            editBtn.setOnAction(e -> handleOpenEditGame(game));

            Button deleteBtn = new Button("Delete");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> handleDeleteGame(game));

            actions.getChildren().addAll(editBtn, deleteBtn);
            card.getChildren().addAll(imageView, nameLabel, spacer, actions);
        } else {
            // For non-admin, just show image and name (and maybe a "Details" button if
            // needed later)
            card.getChildren().addAll(imageView, nameLabel, spacer);
        }

        return card;
    }

    @FXML
    private void handleOpenAddGame() {
        openWindow("/ry/ms/view/game/fxml/addGameForm.fxml", "Ajouter un jeu", null);
    }

    private void handleOpenEditGame(Game game) {
        openWindow("/ry/ms/view/game/fxml/editGameForm.fxml", "Modifier : " + game.getName(), game);
    }

    private void openWindow(String fxmlPath, String title, Game game) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            if (game != null) {
                EditGameController controller = loader.getController();
                controller.initData(game);
            }
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteGame(Game game) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + game.getName() + " ?", ButtonType.YES,
                ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES)
                facade.deleteGame(game);
        });
    }
}