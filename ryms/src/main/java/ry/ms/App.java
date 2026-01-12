package ry.ms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main class for the Ryms application.
 * Initializes and launches the JavaFX application.
 */
public class App extends Application {

    /**
     * Default constructor for the App class.
     * Required by JavaFX Application.
     */
    public App() {
        // Default constructor
    }

    /**
     * Starts the JavaFX application.
     * Loads the main view from FXML and sets up the primary stage.
     * 
     * @param primaryStage The primary stage for this application.
     * @throws IOException If the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/MainView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Ryms");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/R.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main entry point of the application.
     * Launches the JavaFX runtime.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}