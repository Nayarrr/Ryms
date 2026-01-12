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
                mainPane.setTop(navbar);
            }

            // Wire generic navbar actions (always available or re-wired)
            navbarController.setOnLoginClick(this::showLoginView);
            navbarController.setOnRegisterClick(this::showRegisterView);
            navbarController.setOnProfileClick(this::showProfileView);
            navbarController.setOnAdminClick(this::showAdminView);
            navbarController.setOnLogoutClick(() -> {
                SessionFacade.getSessionFactory().logout();
                showLoginView();
            });

            navbarController.updateNavbarState(false, null);

        } catch (IOException e) {
            e.printStackTrace();
            mainPane.setTop(new Label("Error loading navbar."));
        }

        LoginFrame loginFrame = new LoginFrame();
        loginFrame.setOnSuccess(this::showDashboardView);
        loginFrame.setOnRegisterRequest(this::showRegisterView);
        loginFrame.setOnForgotPasswordRequest(this::showForgotPasswordView); // Wired Forgot Password
        setContent(loginFrame.getView());
    }

    private void showForgotPasswordView() {
        loadView("/ry/ms/view/user/fxml/ForgotPassword.fxml",
                (ry.ms.view.user.login.ForgotPasswordController controller) -> {
                    controller.setOnBackRequest(this::showLoginView);
                });
    }

    private void showDashboardView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/MainLayout.fxml"));
            Parent dashboardView = loader.load();
            MainLayoutController layoutController = loader.getController();

            // Wire Navbar Navigation to MainLayoutController actions
            navbarController.setOnTeamsClick(layoutController::loadTeamDashboard);
            navbarController.setOnMatchesClick(layoutController::loadMatchList);
            navbarController.setOnGamesClick(layoutController::showGameCatalog);
            navbarController.setOnTournamentsClick(layoutController::showTournamentList);
            navbarController.setOnProductsClick(layoutController::handleOpenProducts);
            navbarController.setOnShopClick(layoutController::loadShopView);
            navbarController.setOnInvitationsClick(layoutController::handleOpenInvitations);
            // navbarController.setOnInvitationsClick(...) // If needed, expose
            // handleOpenInvitations in MainLayoutController

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
        if (navbarController != null) {
            navbarController.updateNavbarState(false, null);
        }

        RegisterFrame registerFrame = new RegisterFrame();
        registerFrame.setOnSuccess(this::showDashboardView);
        registerFrame.setOnLoginRequest(this::showLoginView);
        setContent(registerFrame.getView());
    }

    private void showProfileView() {
        ProfileFrame profileFrame = new ProfileFrame();
        profileFrame.setOnBackRequest(this::showDashboardView);
        profileFrame.setOnLogoutRequest(() -> {
            SessionFacade.getSessionFactory().logout();
            showLoginView();
        });
        setContent(profileFrame.getView());
    }

    private void showAdminView() {
        loadView("/ry/ms/view/user/fxml/AdminDashboard.fxml",
                (ry.ms.view.user.admin.AdminDashboardController controller) -> {
                    controller.setOnBackRequest(this::showDashboardView);
                });
    }

    private <T> void loadView(String fxmlPath, java.util.function.Consumer<T> controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            T controller = loader.getController();
            if (controllerSetup != null) {
                controllerSetup.accept(controller);
            }
            setContent(view);
        } catch (IOException e) {
            e.printStackTrace();
            ry.ms.view.utils.AlertManager.showError("Erreur de chargement",
                    "Impossible de charger la vue : " + fxmlPath + "\n" + e.getMessage());
        }
    }
}