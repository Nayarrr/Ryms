package ry.ms.view.tournament;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentStatus;
import ry.ms.persistLogic.tournament.postgres.TournamentDAOPostgres;
import ry.ms.businessLogic.tournament.TournamentFacade;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controller for the Tournament List view.
 * Displays a list of tournaments, allows filtering, searching, and navigation
 * to details.
 */
public class TournamentListController {

    @FXML
    private TableView<Tournament> tournamentTableView;
    @FXML
    private TableColumn<Tournament, String> nameColumn;
    @FXML
    private TableColumn<Tournament, String> gameColumn;
    @FXML
    private TableColumn<Tournament, Date> startDateColumn;
    @FXML
    private TableColumn<Tournament, Date> endDateColumn;
    @FXML
    private TableColumn<Tournament, String> locationColumn;
    @FXML
    private TableColumn<Tournament, Void> actionColumn;
    @FXML
    private TableColumn<Tournament, String> statusColumn;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<TournamentStatus> statusFilterCombo;
    @FXML
    private Button addTournamentBtn;

    private TournamentFacade tournamentFacade;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Initializes the controller.
     * Sets up the facade, table columns, and loads the initial list of tournaments.
     */
    @FXML
    public void initialize() {
        tournamentFacade = TournamentFacade.getInstance();

        // Check Admin Status
        ry.ms.businessLogic.user.models.User currentUser = ry.ms.businessLogic.user.login.SessionFacade
                .getSessionFactory().getCurrentUser();
        boolean isAdmin = currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());

        if (addTournamentBtn != null) {
            addTournamentBtn.setVisible(isAdmin);
            addTournamentBtn.setManaged(isAdmin);
        }

        // Configuration du ComboBox pour les statuts
        statusFilterCombo.getItems().addAll(TournamentStatus.values());

        // Configuration des colonnes du TableView
        setupTableColumns(isAdmin);

        // Événement de double-clic sur une ligne
        tournamentTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Tournament selected = tournamentTableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openTournamentBracket(selected);
                }
            }
        });

        // Recherche en temps réel
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTournaments());
        statusFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> filterTournaments());

        // Charger les tournois
        loadTournaments();
    }

    private void setupTableColumns(boolean isAdmin) {
        // Colonne Nom
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Colonne Jeu
        gameColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getGame() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGame().getName());
            }
            return new javafx.beans.property.SimpleStringProperty("-");
        });

        // Colonne Date de début
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startDateColumn.setCellFactory(column -> new TableCell<Tournament, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Colonne Date de fin
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endDateColumn.setCellFactory(column -> new TableCell<Tournament, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });

        // Colonne Lieu
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Colonne Statut
        statusColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus().toString());
        });
        statusColumn.setCellFactory(column -> new TableCell<Tournament, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    // Appliquer un style selon le statut
                    String styleClass = "status-" + item.toLowerCase();
                    setStyle("-fx-padding: 5 10; -fx-background-radius: 3;");
                    getStyleClass().add(styleClass);
                }
            }
        });

        // Colonne Actions (Only for Admin)
        if (isAdmin) {
            actionColumn.setCellFactory(param -> new TableCell<>() {
                private final Button deleteButton = new Button("Supprimer");

                {
                    deleteButton.setOnAction(event -> {
                        Tournament tournament = getTableView().getItems().get(getIndex());
                        handleDeleteTournament(tournament);
                    });
                    deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(deleteButton);
                    }
                }
            });
        } else {
            // Hide or clear action column for non-admins?
            // Hiding might be better, or just empty cells.
            actionColumn.setVisible(false);
        }
    }

    private void handleDeleteTournament(Tournament tournament) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le tournoi " + tournament.getName() + " ?");
        alert.setContentText(
                "Cette action est irréversible. Les matchs et inscriptions associés seront également supprimés.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                tournamentFacade.deleteTournament(tournament);
                loadTournaments(); // Refresh list
            } catch (SQLException e) {
                showError("Erreur de suppression", "Impossible de supprimer le tournoi: " + e.getMessage());
            }
        }
    }

    private void loadTournaments() {
        try {
            List<Tournament> tournaments = tournamentFacade.getAllTournaments();
            tournamentTableView.getItems().setAll(tournaments);
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les tournois: " + e.getMessage());
        }
    }

    private void filterTournaments() {
        try {
            List<Tournament> allTournaments = tournamentFacade.getAllTournaments();
            String searchText = searchField.getText().toLowerCase();
            TournamentStatus selectedStatus = statusFilterCombo.getValue();

            List<Tournament> filtered = allTournaments.stream()
                    .filter(t -> searchText.isEmpty() ||
                            t.getName().toLowerCase().contains(searchText) ||
                            (t.getLocation() != null && t.getLocation().toLowerCase().contains(searchText)))
                    .filter(t -> selectedStatus == null || t.getStatus() == selectedStatus)
                    .toList();

            tournamentTableView.getItems().setAll(filtered);
        } catch (SQLException e) {
            showError("Erreur de filtrage", "Impossible de filtrer les tournois: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddTournament() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ry/ms/view/tournament/fxml/AddTournament.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Nouveau Tournoi");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            AddTournamentController controller = loader.getController();
            controller.setOnTournamentSaved(() -> {
                loadTournaments();
                stage.close();
            });

            stage.showAndWait();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadTournaments();
    }

    @FXML
    private void handleViewInCreation() {
        statusFilterCombo.setValue(TournamentStatus.PLANNING);
        filterTournaments();
    }

    @FXML
    private void handleViewInRegistration() {
        // TODO: Filtrer par statut "INSCRIPTION" quand il sera ajouté
        showInfo("Information", "Fonctionnalité en cours de développement");
    }

    @FXML
    private void handleViewInProgress() {
        // TODO: Utiliser le bon statut quand il sera disponible
        showInfo("Information", "Fonctionnalité en cours de développement");
    }

    private void openTournamentBracket(Tournament tournament) {
        try {
            String fxmlPath = "/ry/ms/view/tournament/fxml/TournamentBracket.fxml";
            java.net.URL resourceUrl = getClass().getResource(fxmlPath);

            if (resourceUrl == null) {
                showError("Erreur", "Le fichier FXML n'a pas été trouvé: " + fxmlPath +
                        "\nVérifiez que le fichier est présent dans les resources.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Bracket - " + tournament.getName());
            stage.setScene(new Scene(root, 1200, 800));

            TournamentBracketController controller = loader.getController();
            controller.setTournament(tournament);

            stage.show();

            // Rafraîchir la liste quand la fenêtre du bracket est fermée
            stage.setOnHidden(e -> handleRefresh());

            // Ne PAS fermer la fenêtre actuelle pour garder la navigation principale !
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Impossible d'ouvrir le bracket:\n" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur", "Erreur inattendue:\n" + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
