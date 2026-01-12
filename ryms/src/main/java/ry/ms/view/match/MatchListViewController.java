package ry.ms.view.match;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.match.models.MatchStatus;
import ry.ms.businessLogic.match.models.TeamResult;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.view.user.UserSession;

/**
 * Controller for the Match List view.
 * Displays a list of all matches with their status and details.
 */
public class MatchListViewController {

    @FXML
    private VBox matchesContainer;
    @FXML
    private Button createMatchButton;

    private MatchController matchController;
    private String currentUserEmail;
    private Consumer<Long> navigationController;

    // Logo par défaut
    private static final String DEFAULT_LOGO_URL = "https://i.imgur.com/BMRAx9w.png";
    private Image defaultLogoImage;

    /**
     * Initializes the controller.
     * Loads the default logo, loads matches, and configures the "Create Match"
     * button visibility.
     */
    @FXML
    public void initialize() {
        matchController = new MatchController();
        currentUserEmail = UserSession.getInstance().getUserEmail();

        // Charger le logo par défaut
        try {
            defaultLogoImage = new Image(DEFAULT_LOGO_URL, true);
        } catch (Exception e) {
            System.err.println("⚠️ Impossible de charger le logo par défaut : " + e.getMessage());
        }

        loadMatches();

        // Visibilité du bouton "Créer match" (admin uniquement)
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        createMatchButton.setVisible(isAdmin);
        createMatchButton.setManaged(isAdmin);
    }

    /**
     * Sets the navigation controller callback.
     * 
     * @param navigationController Access to the main navigation for switching
     *                             views.
     */
    public void setNavigationController(Consumer<Long> navigationController) {
        this.navigationController = navigationController;
    }

    /**
     * Charge tous les matchs et génère les cartes dynamiquement
     */
    private void loadMatches() {
        matchesContainer.getChildren().clear();

        List<Match> matches = matchController.getAllMatches();

        if (matches == null || matches.isEmpty()) {
            Label emptyLabel = new Label("Aucun match à afficher");
            matchesContainer.getChildren().add(emptyLabel);
            return;
        }

        // UTILISER LE CACHE pour partager les mêmes instances
        MatchCache cache = MatchCache.getInstance();

        // Générer une carte pour chaque match
        for (Match match : matches) {
            // Récupérer l'instance cachée (ou l'ajouter)
            Match cachedMatch = cache.getOrPut(match);

            VBox matchCard = createMatchCard(cachedMatch);
            if (matchCard != null) {
                matchesContainer.getChildren().add(matchCard);
            }
        }
    }

    /**
     * Crée une carte de match à partir d'un objet Match
     */
    private VBox createMatchCard(Match match) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ry/ms/view/match/fxml/MatchCard.fxml"));
            VBox card = loader.load();

            // Récupérer tous les éléments du template
            Label matchTimeLabel = (Label) card.lookup("#matchTimeLabel");
            Label matchDateLabel = (Label) card.lookup("#matchDateLabel");

            // Labels de statut (au lieu d'un seul)
            Label statusScheduled = (Label) card.lookup("#statusScheduled");
            Label statusInProgress = (Label) card.lookup("#statusInProgress");
            Label statusFinished = (Label) card.lookup("#statusFinished");

            ImageView team1Logo = (ImageView) card.lookup("#team1Logo");
            Label team1NameLabel = (Label) card.lookup("#team1NameLabel");
            Label team1TagLabel = (Label) card.lookup("#team1TagLabel");

            ImageView team2Logo = (ImageView) card.lookup("#team2Logo");
            Label team2NameLabel = (Label) card.lookup("#team2NameLabel");
            Label team2TagLabel = (Label) card.lookup("#team2TagLabel");

            Label scoreLabel = (Label) card.lookup("#scoreLabel");
            Label competitionLabel = (Label) card.lookup("#competitionLabel");
            Label formatLabel = (Label) card.lookup("#formatLabel");
            Button deleteButton = (Button) card.lookup("#deleteButton");

            // DATE ET HEURE
            if (match.getMatchDate() != null) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM", Locale.FRENCH);
                matchTimeLabel.setText(timeFormat.format(match.getMatchDate()));
                matchDateLabel.setText(dateFormat.format(match.getMatchDate()));
            } else {
                matchTimeLabel.setText("--:--");
                matchDateLabel.setText("Date non définie");
            }

            // ÉQUIPES
            Team team1 = matchController.getTeamForMatch(match, 1);
            Team team2 = matchController.getTeamForMatch(match, 2);

            populateTeamInfo(team1, team1Logo, team1NameLabel, team1TagLabel);
            populateTeamInfo(team2, team2Logo, team2NameLabel, team2TagLabel);

            // Gérer la visibilité des badges selon le statut
            bindMatchStatusVisibility(match, statusScheduled, statusInProgress, statusFinished);

            // SCORES (affichés uniquement si EN COURS ou TERMINÉ)
            MatchStatus status = match.getStatus();
            List<TeamResult> results = matchController.getMatchResults(match.getMatchId());

            if (results != null && !results.isEmpty() &&
                    (status == MatchStatus.IN_PROGRESS || status == MatchStatus.FINISHED)) {

                int score1 = results.size() > 0 ? results.get(0).getScore() : 0;
                int score2 = results.size() > 1 ? results.get(1).getScore() : 0;
                scoreLabel.setText(score1 + " - " + score2);
                scoreLabel.setVisible(true);
            } else {
                scoreLabel.setVisible(false);
            }

            // COMPÉTITION ET FORMAT
            competitionLabel.setText("Nom du tournoi (à mettre en place)");
            formatLabel.setText("BO1");

            // BOUTON SUPPRIMER (visible admin uniquement)
            boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");

            if (deleteButton != null) {
                deleteButton.setVisible(isAdmin);
                deleteButton.setManaged(isAdmin);

                if (isAdmin) {
                    deleteButton.setOnAction(e -> {
                        e.consume(); // Empêcher la propagation du clic vers la carte
                        handleDeleteMatch(match);
                    });
                }
            }

            // CLIC SUR LA CARTE (ouvrir les détails)
            card.setOnMouseClicked(event -> handleCardClick(match));

            return card;

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de la carte : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Affiche/masque automatiquement le bon badge selon le statut
     */
    private void bindMatchStatusVisibility(Match match, Label statusScheduled, Label statusInProgress,
            Label statusFinished) {
        // Listener qui observe les changements de statut
        match.statusProperty().addListener((observable, oldStatus, newStatus) -> {
            updateStatusVisibility(newStatus, statusScheduled, statusInProgress, statusFinished);
        });

        // Initialiser avec le statut actuel
        updateStatusVisibility(match.getStatus(), statusScheduled, statusInProgress, statusFinished);
    }

    /**
     * Affiche uniquement le bon badge
     */
    private void updateStatusVisibility(MatchStatus status, Label statusScheduled, Label statusInProgress,
            Label statusFinished) {
        // Masquer tous les badges
        statusScheduled.setVisible(false);
        statusScheduled.setManaged(false);
        statusInProgress.setVisible(false);
        statusInProgress.setManaged(false);
        statusFinished.setVisible(false);
        statusFinished.setManaged(false);

        // Afficher le bon badge selon le statut
        if (status == null || status == MatchStatus.SCHEDULED) {
            statusScheduled.setVisible(true);
            statusScheduled.setManaged(true);
        } else {
            switch (status) {
                case IN_PROGRESS -> {
                    statusInProgress.setVisible(true);
                    statusInProgress.setManaged(true);
                }
                case FINISHED -> {
                    statusFinished.setVisible(true);
                    statusFinished.setManaged(true);
                }
                default -> {
                    statusScheduled.setVisible(true);
                    statusScheduled.setManaged(true);
                }
            }
        }
    }

    /**
     * Remplit les infos d'une équipe (logo, nom, tag)
     */
    private void populateTeamInfo(Team team, ImageView logoView, Label nameLabel, Label tagLabel) {
        if (team != null) {
            nameLabel.setText(team.getName());
            tagLabel.setText("[" + team.getTag() + "]");

            // GESTION DES LOGOS
            String logoUrl = team.getAvatar();

            if (logoUrl == null || logoUrl.isBlank()) {
                logoUrl = DEFAULT_LOGO_URL;
            }

            try {
                Image logo = new Image(logoUrl, true);

                if (logo.isError()) {
                    logo = defaultLogoImage;
                }

                logoView.setImage(logo);

            } catch (Exception e) {
                logoView.setImage(defaultLogoImage);
            }
        } else {
            nameLabel.setText("En attente");
            tagLabel.setText("[TBD]");
            logoView.setImage(defaultLogoImage);
        }
    }

    /**
     * Gère le clic sur une carte pour ouvrir les détails
     */
    private void handleCardClick(Match match) {
        if (navigationController != null) {
            navigationController.accept(match.getMatchId());
        } else {
            System.err.println("Navigation controller not set in MatchListViewController.");
        }
    }

    @FXML
    private void handleCreateMatch() {
        CreateMatchFrame createMatchFrame = new CreateMatchFrame();
        createMatchFrame.setOnMatchCreated(this::loadMatches);
        createMatchFrame.show();
    }

    /**
     * Gère la suppression d'un match avec confirmation
     */
    private void handleDeleteMatch(Match match) {
        // Confirmation de suppression
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText("Supprimer le match ?");

        String team1Name = "Équipe 1";
        String team2Name = "Équipe 2";

        Team team1 = matchController.getTeamForMatch(match, 1);
        Team team2 = matchController.getTeamForMatch(match, 2);

        if (team1 != null)
            team1Name = team1.getName();
        if (team2 != null)
            team2Name = team2.getName();

        confirmAlert.setContentText(
                "Êtes-vous sûr de vouloir supprimer le match :\n\n" +
                        team1Name + " vs " + team2Name + "\n\n" +
                        "Cette action est irréversible !");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Créer un label temporaire pour les messages
                Label messageLabel = new Label();

                // Appeler la méthode de suppression du controller
                boolean success = matchController.deleteMatch(match.getMatchId(), messageLabel);

                if (success) {
                    // Afficher un message de succès
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Succès");
                    successAlert.setHeaderText("Match supprimé");
                    successAlert.setContentText("Le match a été supprimé avec succès.");
                    successAlert.showAndWait();

                    // Recharger la liste des matchs
                    loadMatches();
                } else {
                    // Afficher l'erreur
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Erreur");
                    errorAlert.setHeaderText("Impossible de supprimer le match");
                    errorAlert.setContentText(messageLabel.getText());
                    errorAlert.showAndWait();
                }
            }
        });
    }
}