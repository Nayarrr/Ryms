package ry.ms.view.user.login;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginFrame {

    private final LoginController controller;
    private final Stage stage;

    public LoginFrame(Stage stage) {
        this.stage = stage;
        this.controller = new LoginController();
    }

    /**
     * Construit et affiche la scène de login
     * @param onSuccess Callback appelé quand la connexion réussit
     */
    public void show(Runnable onSuccess) {
        Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
        Image icon = new Image(getClass().getResourceAsStream("/images/R.png"));
        ImageView logoView = new ImageView(logo);
        stage.getIcons().add(icon);

        Label messageLabel = new Label("Veuillez entrer vos identifiants");
        messageLabel.setTextFill(Color.BLACK);
        messageLabel.setFont(Font.font(14));

        TextField loginField = new TextField();
        loginField.setPromptText("Enter your login");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter your password");

        Label nomLabel = new Label("Nom d'utilisateur :");
        Label passLabel = new Label("Mot de passe :");

        Button loginButton = new Button("Se connecter");
        loginButton.setOnAction(e -> {
            boolean success = controller.handleLoginButtonAction(loginField, passField, messageLabel);
            if (success && onSuccess != null) {
                onSuccess.run();
            }
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(logoView, nomLabel, loginField, passLabel, passField, loginButton, messageLabel);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Login - Ryms");
        stage.show();
    }
}