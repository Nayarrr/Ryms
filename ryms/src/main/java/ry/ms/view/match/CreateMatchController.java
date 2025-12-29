package ry.ms.view.match;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ry.ms.models.Team;
import ry.ms.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CreateMatchController {

    @FXML private TextField team1SearchField;
    @FXML private ListView<Team> team1ListView;
    @FXML private TextField team2SearchField;
    @FXML private ListView<Team> team2ListView;
    @FXML private TextField userSearchField;
    @FXML private ListView<User> userListView;
    @FXML private DatePicker matchDatePicker;
    @FXML private TextField gameIdField;
    @FXML private Label messageLabel;

    private MatchController matchController;
    private Stage modalStage;
    private Runnable onMatchCreated;

    private List<Team> allTeams;
    private List<User> allUsers;
    
    private Team selectedTeam1;
    private Team selectedTeam2;
    private User selectedUser;

    @FXML
    public void initialize() {
        matchController = new MatchController();
        
        matchDatePicker.setValue(LocalDate.now());
        gameIdField.setText("1");
        
        loadTeams();
        loadUsers();
        
        setupTeamAutocomplete(team1SearchField, team1ListView, true);
        setupTeamAutocomplete(team2SearchField, team2ListView, false);
        setupUserAutocomplete();
    }

    private void loadTeams() {
        try {
            allTeams = matchController.getAllTeams();
            System.out.println("🔍 DEBUG: Nombre d'équipes chargées = " + (allTeams == null ? "NULL" : allTeams.size()));
            
            if (allTeams != null && !allTeams.isEmpty()) {
                System.out.println("🔍 DEBUG: Première équipe = " + allTeams.get(0).getName());
            }
            
            if (allTeams == null || allTeams.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: orange;");
                messageLabel.setText("⚠️  Aucune équipe disponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des équipes: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            allUsers = matchController.getAllUsers();
            System.out.println("🔍 DEBUG: Nombre d'utilisateurs chargés = " + (allUsers == null ? "NULL" : allUsers.size()));
            
            if (allUsers != null && !allUsers.isEmpty()) {
                System.out.println("🔍 DEBUG: Premier utilisateur = " + allUsers.get(0).getEmail());
            }
            
            if (allUsers == null || allUsers.isEmpty()) {
                messageLabel.setStyle("-fx-text-fill: orange;");
                messageLabel.setText("⚠️  Aucun utilisateur disponible.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des utilisateurs: " + e.getMessage());
        }
    }

    private void setupTeamAutocomplete(TextField searchField, ListView<Team> listView, boolean isTeam1) {
        System.out.println("🔍 DEBUG: Configuration autocomplétion pour " + (isTeam1 ? "équipe 1" : "équipe 2"));
        
        // Configuration du ListView
        listView.setCellFactory(lv -> new ListCell<Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setText(null);
                } else {
                    setText(team.getName() + " [" + team.getTag() + "]");
                }
            }
        });

        // Recherche en temps réel
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("🔍 DEBUG: Texte saisi = '" + newVal + "'");
            System.out.println("🔍 DEBUG: allTeams est null ? " + (allTeams == null));
            
            if (allTeams == null) {
                System.err.println("❌ ERREUR: allTeams est NULL !");
                return;
            }
            
            if (newVal == null || newVal.trim().isEmpty()) {
                listView.setVisible(false);
                listView.setManaged(false);
                return;
            }

            String query = newVal.toLowerCase().trim();
            System.out.println("🔍 DEBUG: Recherche pour '" + query + "'");
            
            ObservableList<Team> filtered = FXCollections.observableArrayList(
                allTeams.stream()
                    .filter(t -> {
                        boolean match = t.getName().toLowerCase().contains(query) 
                                     || t.getTag().toLowerCase().contains(query);
                        if (match) {
                            System.out.println("🔍 DEBUG: Équipe trouvée = " + t.getName());
                        }
                        return match;
                    })
                    .limit(5)
                    .collect(Collectors.toList())
            );

            System.out.println("🔍 DEBUG: Nombre de résultats filtrés = " + filtered.size());

            if (filtered.isEmpty()) {
                listView.setVisible(false);
                listView.setManaged(false);
                System.out.println("🔍 DEBUG: Aucun résultat, liste cachée");
            } else {
                listView.setItems(filtered);
                listView.setVisible(true);
                listView.setManaged(true);
                System.out.println("🔍 DEBUG: Liste affichée avec " + filtered.size() + " résultats");
            }
        });

        // Sélection d'une équipe
        listView.setOnMouseClicked(event -> {
            Team selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                System.out.println("🔍 DEBUG: Équipe sélectionnée = " + selected.getName());
                searchField.setText(selected.getName() + " [" + selected.getTag() + "]");
                if (isTeam1) {
                    selectedTeam1 = selected;
                } else {
                    selectedTeam2 = selected;
                }
                listView.setVisible(false);
                listView.setManaged(false);
            }
        });
    }

    private void setupUserAutocomplete() {
        userListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUsername() + " (" + user.getEmail() + ")");
                }
            }
        });

        userSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
                return;
            }

            String query = newVal.toLowerCase().trim();
            ObservableList<User> filtered = FXCollections.observableArrayList(
                allUsers.stream()
                    .filter(u -> u.getEmail().toLowerCase().contains(query) 
                              || u.getUsername().toLowerCase().contains(query))
                    .limit(5)
                    .collect(Collectors.toList())
            );

            if (filtered.isEmpty()) {
                userListView.setVisible(false);
                userListView.setManaged(false);
            } else {
                userListView.setItems(filtered);
                userListView.setVisible(true);
                userListView.setManaged(true);
            }
        });

        userListView.setOnMouseClicked(event -> {
            User selected = userListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                userSearchField.setText(selected.getEmail());
                selectedUser = selected;
                userListView.setVisible(false);
                userListView.setManaged(false);
            }
        });
    }

    public void setModalStage(Stage stage) {
        this.modalStage = stage;
    }

    public void setOnMatchCreated(Runnable callback) {
        this.onMatchCreated = callback;
    }

    @FXML
    private void handleCreateMatch() {
        if (selectedTeam1 == null) {
            showError("Veuillez sélectionner l'équipe 1 dans la liste.");
            return;
        }

        if (selectedTeam2 == null) {
            showError("Veuillez sélectionner l'équipe 2 dans la liste.");
            return;
        }

        if (selectedTeam1.getTeamId().equals(selectedTeam2.getTeamId())) {
            showError("Les deux équipes doivent être différentes.");
            return;
        }

        LocalDate matchDate = matchDatePicker.getValue();
        if (matchDate == null) {
            showError("Veuillez sélectionner une date.");
            return;
        }

        if (matchDate.isBefore(LocalDate.now())) {
            showError("La date du match ne peut pas être dans le passé.");
            return;
        }

        int gameId;
        try {
            gameId = Integer.parseInt(gameIdField.getText().trim());
        } catch (NumberFormatException e) {
            showError("L'ID du jeu doit être un nombre valide.");
            return;
        }

        if (selectedUser == null) {
            showError("Veuillez sélectionner un utilisateur dans la liste.");
            return;
        }

        try {
            boolean success = matchController.handleCreateCompleteMatch(
                selectedTeam1,
                selectedTeam2,
                matchDate,
                gameId,
                selectedUser,
                messageLabel
            );

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Match créé !");
                alert.setContentText(
                    "Le match entre " + selectedTeam1.getName() + " et " + selectedTeam2.getName() + 
                    " a été créé avec " + selectedUser.getUsername() + " comme arbitre."
                );
                alert.showAndWait();

                if (onMatchCreated != null) {
                    onMatchCreated.run();
                }

                if (modalStage != null) {
                    modalStage.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la création du match: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        if (modalStage != null) {
            modalStage.close();
        }
    }

    private void showError(String message) {
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        messageLabel.setText("❌ " + message);
    }
}