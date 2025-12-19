package ry.ms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader; // Important pour charger la suite en FXML
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ry.ms.view.team.UserSession;
import ry.ms.view.user.login.LoginController;

import java.io.IOException;

public class App extends Application {
    
    private TextField loginField;
    private PasswordField passField;
    private Label messageLabel;
    private Stage primaryStage;
    private LoginController lg = new LoginController();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // --- Ton code de construction d'interface existant ---
        // (Je remets juste l'essentiel pour le contexte)
        Image image = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView imageView = new ImageView(image);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        this.messageLabel = new Label("Veuillez entrer vos identifiants");
        this.loginField = new TextField();
        this.passField = new PasswordField();
        
        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> handleLoginAttempt()); // Note la correction typo: handle

        root.getChildren().addAll(imageView, new Label("User:"), loginField, new Label("Pass:"), passField, loginButton, messageLabel);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("RYMS Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void handleLoginAttempt() {
        // 1. Appel au contrôleur pour vérifier la BDD
        boolean success = lg.handleLoginButtonAction(loginField, passField, messageLabel);
        
        if (success) {
            // 2. IMPORTANT : Sauvegarder l'email dans la session
            String email = loginField.getText();
            UserSession.getInstance().setUserEmail(email);
            
            // 3. Charger la nouvelle interface FXML
            showMainPage();
        }
    }

    private void showMainPage() {
        try {
            // Charge le fichier FXML principal (Barre de navigation + Contenu dynamique)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/team/fxml/MainLayout.fxml"));
            Parent root = loader.load();

            // Crée la nouvelle scène
            Scene mainScene = new Scene(root, 800, 600);

            // Change la scène sur la fenêtre principale
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("RYMS - Team Manager");
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur critique : Impossible de charger l'application.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}