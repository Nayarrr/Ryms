package ry.ms;

import javafx.application.Application;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ry.ms.view.main.MainFrame; // Important pour charger la suite en FXML
import ry.ms.view.match.MatchFrame;
import ry.ms.view.user.login.LoginFrame;


/**
 * Point d'entrée de l'application Ryms
 */
public class App extends Application {
    
    private TextField loginField;
    private PasswordField passField;
    private Label messageLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Démarrer avec l'écran de login
        showLoginFrame();
    }

    /**
     * Affiche le frame de connexion
     */
    private void showLoginFrame() {
        LoginFrame loginFrame = new LoginFrame(primaryStage);
        loginFrame.show(this::showMainFrame);
    }

    /**
     * Affiche le frame principal après connexion
     */
    private void showMainFrame() {
        MainFrame mainFrame = new MainFrame(primaryStage);
        
        // Configurer les callbacks pour la navigation
        mainFrame.setOnMatchManagementClick(this::showMatchFrame);
        mainFrame.setOnTeamManagementClick(() -> System.out.println("Team management - TODO"));
        mainFrame.setOnTournamentClick(() -> System.out.println("Tournament - TODO"));
        mainFrame.setOnProfileClick(() -> System.out.println("Profile - TODO"));
        
        mainFrame.show();
    }

    /**
     * Affiche le frame de gestion des matchs
     */
    private void showMatchFrame() {
        MatchFrame matchFrame = new MatchFrame(primaryStage);
        matchFrame.show();
    }

    public static void main(String[] args) {
        System.out.println("--- Starting Ryms Application ---");
        
        launch(args);
    }
}