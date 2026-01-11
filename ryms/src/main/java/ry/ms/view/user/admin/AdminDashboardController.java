package ry.ms.view.user.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import ry.ms.businessLogic.user.login.SessionFacade;
import ry.ms.businessLogic.user.models.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
    private Label messageLabel;

    private Runnable onBackRequest;

    public void setOnBackRequest(Runnable onBackRequest) {
        this.onBackRequest = onBackRequest;
    }

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        // Setup Context Menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem makeAdminItem = new MenuItem("Make Admin");
        MenuItem makeUserItem = new MenuItem("Make Employee");
        MenuItem toggleActiveItem = new MenuItem("Toggle Active Status");

        makeAdminItem.setOnAction(e -> changeRole("Admin"));
        makeUserItem.setOnAction(e -> changeRole("Employee"));
        toggleActiveItem.setOnAction(e -> toggleActiveStatus());

        contextMenu.getItems().addAll(makeAdminItem, makeUserItem, toggleActiveItem);
        userTable.setContextMenu(contextMenu);

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = SessionFacade.getSessionFactory().getAllUsers();
            ObservableList<User> observableUsers = FXCollections.observableArrayList(users);
            userTable.setItems(observableUsers);
        } catch (SQLException e) {
            messageLabel.setText("Error loading users: " + e.getMessage());
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
                messageLabel.setText("Error updating role: " + e.getMessage());
            }
        }
    }

    private void toggleActiveStatus() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                boolean newStatus = !selectedUser.isActive();
                // We need a method to update status of ANY user, not just current.
                // SessionFacade.deactivateAccount() only does current.
                // UserManager has setAccountStatus(email, status).
                // We need to expose this in SessionFacade for arbitrary users or access
                // UserManager?
                // SessionFacade is the gateway. Let's check if we exposed it.
                // We currently only have reactivateAccount/deactivateAccount for current user.

                // Let's implement a generic updateStatus in SessionFacade or just use
                // updateUser?
                // `updateUser` updates all fields including `isActive` if User model has it?
                // UserDAO.updateUser typically updates profile fields.
                // Let's check UserDAO.updateUser implementation.

                // UserDAOPostgres.updateUser updates username, password, role. NOT is_active.
                // It has updateStatus(email, status).

                // So we need to expose updateStatus(email, status) in SessionFacade for generic
                // use OR
                // add it to AdminController to call UserManager directly?
                // Ideally SessionFacade.

                // For now, I will use a new method in SessionFacade: updateUserStatus(email,
                // status).

                SessionFacade.getSessionFactory().updateUserStatus(selectedUser.getEmail(), newStatus);
                selectedUser.setActive(newStatus);
                userTable.refresh();
                messageLabel.setText("User status updated.");

            } catch (Exception e) {
                messageLabel.setText("Error updating status: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        if (onBackRequest != null) {
            onBackRequest.run();
        }
    }
}
