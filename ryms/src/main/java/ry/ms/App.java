package ry.ms;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;

/**
 * Hello world!
 *
 */
public class App extends Application
{

    private TextField loginField;
    private PasswordField passField;
    private Label messageLabel;
    private Stage primaryStage;
    private LoginController lg = new LoginController();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;


        VBox root = new VBox(10); // 10 est l'espacement vertical entre les éléments
        root.setPadding(new Insets(20)); // Marge intérieure

        this.messageLabel = new Label("Veuillez entrer vos identifiants");
        messageLabel.setTextFill(Color.BLACK);

        this.loginField = new TextField();
        loginField.setPromptText("Enter your login");
        this.passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Label nomLabel = new Label("Nom d'utilisateur :");
        Label passLabel = new Label("Mot de passe :");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> handeLoginAttempt());

        root.getChildren().addAll(nomLabel, loginField, passLabel, passField, loginButton, messageLabel);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Login Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void handeLoginAttempt() {
        boolean res = lg.handleLoginButtonAction(loginField, passField, messageLabel);
        if (res) {
            showMainPage();
        }
    }

    private void showMainPage() {

        // --- 1. Création des composants de la nouvelle page ---
        Label welcomeTitle = new Label("Connexion Réussie ! 🎉");
        welcomeTitle.setFont(new Font("System Bold", 24));

        Label welcomeText = new Label("Bienvenue dans l'application principale.");

        // --- 2. Configuration du conteneur VBox pour la page principale ---
        VBox mainRoot = new VBox(20); // Espacement de 20
        mainRoot.setPadding(new Insets(50));
        mainRoot.setAlignment(Pos.CENTER); // Centrer les éléments dans la VBox

        mainRoot.getChildren().addAll(welcomeTitle, welcomeText);

        // --- 3. Remplacement de la Scene sur le Stage ---
        Scene mainScene = new Scene(mainRoot, 800, 600); // Nouvelle taille pour la page principale

        primaryStage.setScene(mainScene); // Change le contenu de la fenêtre
        primaryStage.setTitle("Application Principale"); // Change le titre de la fenêtre
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
