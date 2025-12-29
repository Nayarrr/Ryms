package ry.ms.view.match;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CreateMatchFrame {

    private final Stage ownerStage;
    private final Runnable onMatchCreated;

    public CreateMatchFrame(Stage ownerStage, Runnable onMatchCreated) {
        this.ownerStage = ownerStage;
        this.onMatchCreated = onMatchCreated;
    }

    public void show() {
        try {
            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(ownerStage);
            modal.setTitle("Créer un nouveau match");

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/match/fxml/CreateMatchView.fxml")
            );
            VBox root = loader.load();

            CreateMatchController controller = loader.getController();
            controller.setModalStage(modal);
            controller.setOnMatchCreated(onMatchCreated);

            Scene scene = new Scene(root);
            modal.setScene(scene);
            modal.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors du chargement de CreateMatchView.fxml");
            System.err.println("   Vérifiez que le fichier existe à : /ry/ms/view/match/fxml/CreateMatchView.fxml");
            System.err.println("   Message: " + e.getMessage());
        }
    }
}