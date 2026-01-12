package ry.ms.view.match;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Helper class to display the Create Match modal window.
 */
public class CreateMatchFrame {

    private Runnable onMatchCreated;

    /**
     * Default constructor.
     */
    public CreateMatchFrame() {
    }

    /**
     * Sets the callback to be executed when a match is created.
     * 
     * @param callback The callback runnable.
     */
    public void setOnMatchCreated(Runnable callback) {
        this.onMatchCreated = callback;
    }

    /**
     * Loads the FXML and shows the modal window.
     */
    public void show() {
        try {
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Créer un nouveau match");

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ry/ms/view/match/fxml/CreateMatchView.fxml"));
            VBox root = loader.load();

            CreateMatchController controller = loader.getController();
            controller.setModalStage(modal);
            controller.setOnMatchCreated(onMatchCreated);

            Scene scene = new Scene(root, 900, 700);
            modal.setScene(scene);
            modal.setMinWidth(800);
            modal.setMinHeight(650);
            modal.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur : " + e.getMessage());
        }
    }
}