package ry.ms;

import javafx.application.Application;
import javafx.stage.Stage;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.view.main.MainFrame;
import ry.ms.view.match.MatchFrame;
import ry.ms.view.user.login.LoginFrame;

/**
 * Point d'entrée de l'application Ryms
 */
public class App extends Application {
    
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
        
        // Test de connexion (optionnel, pour debug)
        try {
            SessionFacade facade = SessionFacade.getSessionFactory();
            System.out.println("SessionFacade initialized successfully");
            
            // Test de login
            boolean loginSuccess = facade.login("admin@ryms.com", "password_123");
            if (loginSuccess) {
                System.out.println("Test login successful");
            }
        } catch (Exception e) {
            System.err.println("Error initializing: " + e.getMessage());
            e.printStackTrace();
        }
        
        launch(args);
    }
}