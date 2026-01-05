package ry.ms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
import ry.ms.view.user.UserSession;
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
        System.out.println("--- Starting Ryms Application ---");

        // Interface de login
        Image image = new Image(getClass().getResourceAsStream("/images/logo.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-alignment: center; -fx-background-color: #ecf0f1;");

        this.messageLabel = new Label("Veuillez entrer vos identifiants");
        messageLabel.setStyle("-fx-font-size: 14px;");
        
        this.loginField = new TextField();
        loginField.setPromptText("Email");
        loginField.setMaxWidth(300);
        
        this.passField = new PasswordField();
        passField.setPromptText("Mot de passe");
        passField.setMaxWidth(300);
        
        Button loginButton = new Button("Se connecter");
        loginButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 30;");
        loginButton.setOnAction(e -> handleLoginAttempt());

        root.getChildren().addAll(
            imageView, 
            new Label("Email:"), loginField, 
            new Label("Mot de passe:"), passField, 
            loginButton, 
            messageLabel
        );

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("RYMS - Connexion");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void handleLoginAttempt() {
        boolean success = lg.handleLoginButtonAction(loginField, passField, messageLabel);
        
        if (success) {
            String email = loginField.getText();
            UserSession.getInstance().setUserEmail(email);
            showMainPage();
            showGameCatalog(); //A changer mieciubzefiyb
        }
    }

    private void showMainPage() {
        try {
            // ✅ Charger MainLayout.fxml depuis /ry/ms/view/
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ry/ms/view/MainLayout.fxml")
            );
            Parent root = loader.load();

            Scene mainScene = new Scene(root, 1200, 800);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("RYMS - Team Manager");
            primaryStage.centerOnScreen();
            
            System.out.println("✅ MainLayout chargé avec succès");
            
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Erreur critique : Impossible de charger l'application.");
            messageLabel.setTextFill(Color.RED);
        }
    }

    public void showGameCatalog() {
        try {
            // 1. Charger le fichier FXML de la liste des jeux
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/game/fxml/gameCatalogLayout.fxml"));
            Parent catalogRoot = loader.load();

            // 2. Si vous voulez remplacer toute la fenêtre :
            Scene catalogScene = new Scene(catalogRoot, 900, 700);
            primaryStage.setScene(catalogScene);
            primaryStage.setTitle("RYMS - Catalogue des jeux");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}