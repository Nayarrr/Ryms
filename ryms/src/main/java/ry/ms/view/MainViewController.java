package ry.ms.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import ry.ms.view.main.MainLayoutController;
import ry.ms.view.user.login.LoginFrame;
import ry.ms.view.user.register.RegisterFrame;
import ry.ms.view.utils.NavbarController;

import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;
import ry.ms.view.user.profile.ProfileFrame;
import java.io.IOException;

public class MainViewController {

    @FXML
    private BorderPane mainPane;

    // This injects the controller of the included Navbar.fxml
    @FXML
    private NavbarController navbarController;

    private Runnable onLogout;

    @FXML
    public void initialize() {
        // Set up handlers for navbar buttons
        navbarController.setOnLoginClick(this::showLoginView);
        navbarController.setOnRegisterClick(this::showRegisterView);
        navbarController.setOnProfileClick(this::showProfileView);
        navbarController.setOnLogoutClick(() -> {
            SessionFacade.getSessionFactory().logout();
            showLoginView();
        });

        // Show the login view by default when the app starts
        showLoginView();
    }

    private void setContent(Node content) {
        mainPane.setCenter(content);
    }

    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    private void showLoginView() {
        // Ensure navbar is visible and in Auth mode
        try {
            if (mainPane.getTop() == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/utils/fxml/Navbar.fxml"));
                Parent navbar = loader.load();
                navbarController = loader.getController();
                // Re-wire because controller might be new
                navbarController.setOnLoginClick(this::showLoginView);
                navbarController.setOnRegisterClick(this::showRegisterView);
                navbarController.setOnProfileClick(this::showProfileView);
                navbarController.setOnLogoutClick(() -> {
                    SessionFacade.getSessionFactory().logout();
                    showLoginView();
                });
                mainPane.setTop(navbar);
            }
            navbarController.updateNavbarState(false, null);

        } catch (IOException e) {
            e.printStackTrace();
            mainPane.setTop(new Label("Error loading navbar."));
        }

        LoginFrame loginFrame = new LoginFrame();
        // When login is successful, show the dashboard
        loginFrame.setOnSuccess(this::showDashboardView);
        // Switch to register view
        loginFrame.setOnRegisterRequest(this::showRegisterView);
        setContent(loginFrame.getView());
    }

    private void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/MainLayout.fxml"));
            Parent dashboardView = loader.load();
            MainLayoutController layoutController = loader.getController();

            // Set a callback for the logout button in the dashboard (if any?)
            // layoutController.setOnLogout(this::showLoginView);
            // Dashboard likely doesn't have logout button anymore if we use Navbar?
            // But MainLayoutController might still expect it. I'll check
            // MainLayoutController later if needed.

            // mainPane.setTop(null); // DO NOT REMOVE NAVBAR

            // Update Navbar State
            User currentUser = SessionFacade.getSessionFactory().getCurrentUser();
            String username = (currentUser != null) ? currentUser.getUsername() : "User";
            navbarController.updateNavbarState(true, username);

            setContent(dashboardView);
        } catch (IOException e) {
            e.printStackTrace();
            setContent(new Label("Error: Could not load dashboard view."));
        }
    }

    private void showRegisterView() {
        // Ensure navbar is in Auth mode
        navbarController.updateNavbarState(false, null);

        RegisterFrame registerFrame = new RegisterFrame();
        // On successful registration, navigate back to the login view
        registerFrame.setOnSuccess(this::showLoginView); // Or dashboard? Register usually -> Login or Auto-login ->
                                                         // Dashboard
        // My SessionFacade plan says register auto-logins.
        // But the original RegisterFrame.onSuccess just went to Login.
        // I will keep it as showLoginView for now, or change to showDashboardView if I
        // auto-login.
        // Let's stick to showing Login for consistency with previous flow, or Upgrade?
        // UserManager register returns User, SessionFacade register sets currentUser.
        // So we ARE logged in. We should go to Dashboard.
        registerFrame.setOnSuccess(this::showDashboardView);

        // Switch back to login view if requested
        registerFrame.setOnLoginRequest(this::showLoginView);
        setContent(registerFrame.getView());
    }

    private void showProfileView() {
        ProfileFrame profileFrame = new ProfileFrame();
        profileFrame.setOnBackRequest(this::showDashboardView);
        profileFrame.setOnLogoutRequest(() -> {
            SessionFacade.getSessionFactory().logout();
            showLoginView(); // Logic already handled in deleteUser? NO, logic in Controller calls
                             // onLogoutRequest.
        });
        setContent(profileFrame.getView());
    }
}