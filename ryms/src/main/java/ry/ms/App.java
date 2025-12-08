package ry.ms;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Hello world!
 *
 */
public class App extends Application
{
    @Override
    public void start(Stage primaryStage) {

        LoginController lg = new LoginController();
        VBox root = new VBox(10); // 10 est l'espacement vertical entre les éléments
        root.setPadding(new Insets(20)); // Marge intérieure

        Label messageLabel = new Label("Veuillez entrer vos identifiants");
        messageLabel.setTextFill(Color.BLACK);

        TextField loginField = new TextField();
        loginField.setPromptText("Enter your login");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Label nomLabel = new Label("Nom d'utilisateur :");
        Label passLabel = new Label("Mot de passe :");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> lg.handleLoginButtonAction(loginField, passField, messageLabel));

        root.getChildren().addAll(nomLabel, loginField, passLabel, passField, loginButton, messageLabel);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Login Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
