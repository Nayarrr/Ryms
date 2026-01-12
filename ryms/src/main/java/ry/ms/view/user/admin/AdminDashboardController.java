package ry.ms.view.user.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ry.ms.AbsFactory;
import ry.ms.businessLogic.team.TeamManager;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;

import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> usernameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Boolean> activeColumn;

    @FXML
    private TableView<Team> teamTable;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, String> teamTagColumn;
    @FXML
    private TableColumn<Team, String> teamCaptainColumn;

    @FXML
    private Label messageLabel;

    private Runnable onBackRequest;
    private final TeamManager teamManager;

    public AdminDashboardController() {
        AbsFactory factory = AbsFactory.getInstance();
        this.teamManager = new TeamManager(factory);
    }

    public void setOnBackRequest(Runnable onBackRequest) {
        this.onBackRequest = onBackRequest;
    }

    @FXML
    public void initialize() {
        setupUserTable();
        setupTeamTable();
        loadUsers();
        loadTeams();
    }

    private void setupUserTable() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem makeAdminItem = new MenuItem("Make Admin");
        MenuItem makeUserItem = new MenuItem("Make Employee");
        MenuItem toggleActiveItem = new MenuItem("Toggle Active Status");

        makeAdminItem.setOnAction(e -> changeRole("Admin"));
        makeUserItem.setOnAction(e -> changeRole("Employee"));
        toggleActiveItem.setOnAction(e -> toggleActiveStatus());

        contextMenu.getItems().addAll(makeAdminItem, makeUserItem, toggleActiveItem);
        userTable.setContextMenu(contextMenu);
    }

    private void setupTeamTable() {
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamTagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        teamCaptainColumn.setCellValueFactory(new PropertyValueFactory<>("captainEmail"));
    }

    private void loadUsers() {
        try {
            List<User> users = SessionFacade.getSessionFactory().getAllUsers();
            ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
            userTable.setItems(observableUsers);
        } catch (SQLException e) {
            showError("Error loading users: " + e.getMessage());
        }
    }

    private void loadTeams() {
        try {
            List<Team> teams = teamManager.getAllTeams(); // Need to ensure TeamManager has getAllTeams
            ObservableList<Team> observableTeams = FXCollections.observableArrayList(teams);
            teamTable.setItems(observableTeams);
        } catch (Exception e) { // Catch generic exception if getAllTeams fails or throws specialized
            showError("Error loading teams: " + e.getMessage());
        }
    }

    private void changeRole(String newRole) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                selectedUser.setRole(newRole);
                SessionFacade.getSessionFactory().updateUser(selectedUser);
                userTable.refresh();
                messageLabel.setText("User role updated to " + newRole);
            } catch (SQLException e) {
                showError("Error updating role: " + e.getMessage());
            }
        }
    }

    private void toggleActiveStatus() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                boolean newStatus = !selectedUser.isActive();
                SessionFacade.getSessionFactory().updateUserStatus(selectedUser.getEmail(), newStatus);
                selectedUser.setActive(newStatus);
                userTable.refresh();
                messageLabel.setText("User status updated.");

            } catch (Exception e) {
                showError("Error updating status: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        if (onBackRequest != null) {
            onBackRequest.run();
        }
    }

    private void showError(String message) {
        messageLabel.setText(message);
        ry.ms.view.utils.AlertManager.showError("Erreur Admin", message);
    }
}
