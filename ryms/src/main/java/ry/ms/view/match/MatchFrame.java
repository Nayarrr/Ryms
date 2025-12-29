package ry.ms.view.match;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MatchFrame {

    private final Stage primaryStage;

    public MatchFrame(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/match/fxml/MatchListView.fxml")
            );
            BorderPane root = loader.load();

            MatchListViewController controller = loader.getController();
            controller.setOwnerStage(primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("RYMS - Gestion des Matchs");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur chargement MatchListView.fxml: " + e.getMessage());
        }
    }
}