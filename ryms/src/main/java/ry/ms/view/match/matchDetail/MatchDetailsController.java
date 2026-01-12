package ry.ms.view.match.matchDetail;

import java.text.SimpleDateFormat;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.match.models.MatchStatus;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;
import ry.ms.view.match.MatchController;
import ry.ms.view.user.UserSession;

/**
 * Contrôleur principal de la vue détaillée d'un match.
 * Orchestre les différents managers pour afficher les informations du match.
 */
public class MatchDetailsController {

    @FXML
    private VBox rootContainer;
    @FXML
    private Label matchTitleLabel;
    @FXML
    private Label matchDateLabel;
    @FXML
    private Button editDateButton;
    @FXML
    private VBox refereesListContainer;
    @FXML
    private Button addRefereeButton;
    @FXML
    private Label team1NameLabel;
    @FXML
    private Label team1CoachLabel;
    @FXML
    private VBox team1RosterContainer;
    @FXML
    private Button team1UpdateButton;
    @FXML
    private Label team2NameLabel;
    @FXML
    private Label team2CoachLabel;
    @FXML
    private VBox team2RosterContainer;
    @FXML
    private Button team2UpdateButton;
    @FXML
    private VBox scoresContainer;
    @FXML
    private Button startMatchButton;

    private MatchController matchController;
    private String currentUserEmail;
    private Long matchId;
    private Match currentMatch;
    private Team team1;
    private Team team2;
    private List<User> matchReferees;
    private Runnable navigationController;

    // Managers dédiés
    private ScoreManager scoreManager;
    private TeamDetailsManager teamDetailsManager;
    private RefereeManager refereeManager;
    private MatchDateManager matchDateManager;
    private MatchStatusManager matchStatusManager;

    /**
     * Initializes the controller.
     * Sets up the managers (Score, TeamDetails, Referee, etc.) and instantiates the
     * MatchController.
     */
    @FXML
    public void initialize() {
        matchController = new MatchController();
        currentUserEmail = UserSession.getInstance().getUserEmail();

        // Initialiser les managers
        scoreManager = new ScoreManager(matchController, this::isAdminOrReferee);
        teamDetailsManager = new TeamDetailsManager(matchController, currentUserEmail);
        refereeManager = new RefereeManager(matchController);
        matchDateManager = new MatchDateManager(matchController);
        matchStatusManager = new MatchStatusManager(matchController);
    }

    public void setNavigationController(Runnable navigationController) {
        this.navigationController = navigationController;
    }

    /**
     * Charge et affiche tous les détails d'un match
     */
    public void loadMatchDetails(Long matchId) {
        this.matchId = matchId;

        Match match = matchController.getMatchById(matchId);
        if (match == null) {
            System.err.println("❌ Match introuvable");
            return;
        }

        // UTILISER LE CACHE pour obtenir l'instance partagée
        this.currentMatch = ry.ms.view.match.MatchCache.getInstance().getOrPut(match);

        // Charger les équipes
        team1 = matchController.getTeamForMatch(currentMatch, 1);
        team2 = matchController.getTeamForMatch(currentMatch, 2);
        matchReferees = currentMatch.getReferees();

        // Afficher le titre
        displayMatchTitle(team1, team2);

        // Afficher la date
        displayMatchDate(currentMatch);

        // Charger les différentes sections via les managers
        refereeManager.loadReferees(refereesListContainer, currentMatch.getReferees());
        teamDetailsManager.loadTeamDetails(team1, team1NameLabel, team1CoachLabel, team1RosterContainer,
                team1UpdateButton);
        teamDetailsManager.loadTeamDetails(team2, team2NameLabel, team2CoachLabel, team2RosterContainer,
                team2UpdateButton);
        scoreManager.loadScores(scoresContainer, matchId, team1, team2, this::loadMatchDetails);

        // Gérer la visibilité des boutons
        configureButtonsVisibility(currentMatch.getStatus());
    }

    /**
     * Affiche le titre du match (équipe1 vs équipe2)
     */
    private void displayMatchTitle(Team team1, Team team2) {
        String team1Name = team1 != null ? team1.getName() + " [" + team1.getTag() + "]" : "En attente";
        String team2Name = team2 != null ? team2.getName() + " [" + team2.getTag() + "]" : "En attente";
        matchTitleLabel.setText(team1Name + " vs " + team2Name);
    }

    /**
     * Affiche la date du match
     */
    private void displayMatchDate(Match match) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm");
        String dateStr = match.getMatchDate() != null ? dateFormat.format(match.getMatchDate()) : "Date non définie";
        matchDateLabel.setText("Date : " + dateStr);
    }

    /**
     * Configure la visibilité des boutons selon le rôle et le statut
     */
    private void configureButtonsVisibility(MatchStatus status) {
        boolean isAdmin = currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com");
        boolean isReferee = matchReferees != null && currentUserEmail != null &&
                matchReferees.stream().anyMatch(ref -> ref.getEmail().equalsIgnoreCase(currentUserEmail));

        // Bouton "Commencer le match"
        boolean canStartMatch = (isAdmin || isReferee) && status == MatchStatus.SCHEDULED;
        startMatchButton.setVisible(canStartMatch);
        startMatchButton.setManaged(canStartMatch);

        // Boutons admin
        addRefereeButton.setVisible(isAdmin);
        addRefereeButton.setManaged(isAdmin);
        editDateButton.setVisible(isAdmin);
        editDateButton.setManaged(isAdmin);
    }

    /**
     * Vérifie si l'utilisateur est admin ou arbitre
     */
    private boolean isAdminOrReferee() {
        if (currentUserEmail != null && currentUserEmail.equalsIgnoreCase("admin@ryms.com")) {
            return true;
        }

        if (matchReferees != null && currentUserEmail != null) {
            return matchReferees.stream().anyMatch(ref -> ref.getEmail().equalsIgnoreCase(currentUserEmail));
        }

        return false;
    }

    // ========== HANDLERS ==========

    @FXML
    private void handleBackToList() {
        if (navigationController != null) {
            navigationController.run();
        } else {
            System.err.println("Navigation controller not set in MatchDetailsController.");
        }
    }

    @FXML
    private void handleEditDate() {
        Stage ownerStage = (Stage) rootContainer.getScene().getWindow();
        matchDateManager.openEditDateModal(matchId, ownerStage, this::loadMatchDetails);
    }

    @FXML
    private void handleAddReferee() {
        Stage ownerStage = (Stage) rootContainer.getScene().getWindow();
        refereeManager.openAddRefereeModal(matchId, ownerStage, this::loadMatchDetails);
    }

    @FXML
    private void handleUpdateRosterTeam1() {
        if (team1 != null) {
            Stage ownerStage = (Stage) rootContainer.getScene().getWindow();
            matchController.openUpdateRosterModal(team1.getTeamId(), ownerStage);
        }
    }

    @FXML
    private void handleUpdateRosterTeam2() {
        if (team2 != null) {
            Stage ownerStage = (Stage) rootContainer.getScene().getWindow();
            matchController.openUpdateRosterModal(team2.getTeamId(), ownerStage);
        }
    }

    @FXML
    private void handleStartMatch() {
        Stage ownerStage = (Stage) rootContainer.getScene().getWindow();
        matchStatusManager.handleStartMatch(matchId, ownerStage, this::loadMatchDetails);
    }
}