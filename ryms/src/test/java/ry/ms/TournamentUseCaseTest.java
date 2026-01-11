package ry.ms;

import junit.framework.TestCase;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentStatus;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.tournament.dao.TournamentDAO;
import ry.ms.persistLogic.tournament.postgres.TournamentDAOPostgres;
import ry.ms.businessLogic.tournament.TournamentFacade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Test d'intégration pour le use case de gestion des tournois
 */
public class TournamentUseCaseTest extends TestCase {

    // Constantes de test
    private static final String TOURNAMENT_NAME = "JUnit_Test_Tournament";
    private static final String TOURNAMENT_LOCATION = "JUnit_Location";
    private static final String UPDATED_LOCATION = "JUnit_Updated_Location";
    private static final int MAX_PARTICIPANTS = 16;

    private TournamentFacade tournamentFacade;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tournamentFacade = TournamentFacade.getInstance();
        cleanupTestData();
    }

    @Override
    protected void tearDown() throws Exception {
        cleanupTestData();
        super.tearDown();
    }

    /**
     * Test 1: Créer et récupérer un tournoi
     */
    public void testCreateAndRetrieveTournament() throws SQLException {
        // Créer un tournoi
        Date startDate = new Date(System.currentTimeMillis() + 86400000); // Demain
        Date endDate = new Date(System.currentTimeMillis() + 172800000); // Dans 2 jours

        Tournament tournament = new Tournament(
                0,
                TOURNAMENT_NAME,
                null, // Pas de jeu pour simplifier
                startDate,
                endDate,
                MAX_PARTICIPANTS,
                TOURNAMENT_LOCATION);
        tournament.setStatus(TournamentStatus.PLANNING);

        // Sauvegarder le tournoi
        tournamentFacade.createTournament(tournament);

        // Vérifier que le tournoi a été créé avec un ID
        assertTrue("Le tournoi devrait avoir un ID après création", tournament.getTournamentId() > 0);

        // Récupérer tous les tournois
        List<Tournament> tournaments = tournamentFacade.getAllTournaments();

        // Vérifier que notre tournoi est dans la liste
        Tournament retrievedTournament = tournaments.stream()
                .filter(t -> TOURNAMENT_NAME.equals(t.getName()))
                .findFirst()
                .orElse(null);

        // Vérifications
        assertNotNull("Le tournoi devrait être récupérable", retrievedTournament);
        assertEquals("Le nom du tournoi devrait correspondre", TOURNAMENT_NAME, retrievedTournament.getName());
        assertEquals("Le lieu devrait correspondre", TOURNAMENT_LOCATION, retrievedTournament.getLocation());
        assertEquals("Le nombre max de participants devrait correspondre", MAX_PARTICIPANTS,
                retrievedTournament.getMaxParticipants());
        assertEquals("Le statut devrait être PLANNING", TournamentStatus.PLANNING, retrievedTournament.getStatus());
    }

    /**
     * Test 2: Mettre à jour et supprimer un tournoi
     */
    public void testUpdateAndDeleteTournament() throws SQLException {
        // Créer un tournoi
        Date startDate = new Date(System.currentTimeMillis() + 86400000);
        Date endDate = new Date(System.currentTimeMillis() + 172800000);

        Tournament tournament = new Tournament(
                0,
                TOURNAMENT_NAME,
                null,
                startDate,
                endDate,
                MAX_PARTICIPANTS,
                TOURNAMENT_LOCATION);
        tournament.setStatus(TournamentStatus.PLANNING);

        tournamentFacade.createTournament(tournament);

        // Récupérer le tournoi créé
        List<Tournament> tournaments = tournamentFacade.getAllTournaments();
        Tournament createdTournament = tournaments.stream()
                .filter(t -> TOURNAMENT_NAME.equals(t.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull("Le tournoi devrait exister", createdTournament);

        // Mettre à jour le lieu et le statut
        createdTournament.setLocation(UPDATED_LOCATION);
        createdTournament.setStatus(TournamentStatus.IN_PROGRESS);
        tournamentFacade.updateTournament(createdTournament);

        // Vérifier la mise à jour
        tournaments = tournamentFacade.getAllTournaments();
        Tournament updatedTournament = tournaments.stream()
                .filter(t -> TOURNAMENT_NAME.equals(t.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull("Le tournoi devrait toujours exister", updatedTournament);
        assertEquals("Le lieu devrait être mis à jour", UPDATED_LOCATION, updatedTournament.getLocation());
        assertEquals("Le statut devrait être IN_PROGRESS", TournamentStatus.IN_PROGRESS, updatedTournament.getStatus());

        // Supprimer le tournoi
        tournamentFacade.deleteTournament(updatedTournament);

        // Vérifier la suppression
        tournaments = tournamentFacade.getAllTournaments();
        boolean tournamentExists = tournaments.stream()
                .anyMatch(t -> TOURNAMENT_NAME.equals(t.getName()));

        assertFalse("Le tournoi ne devrait plus exister", tournamentExists);
    }

    /**
     * Test 3: Vérifier les statuts de tournoi
     */
    public void testTournamentStatuses() throws SQLException {
        // Créer un tournoi
        Date startDate = new Date(System.currentTimeMillis() + 86400000);
        Date endDate = new Date(System.currentTimeMillis() + 172800000);

        Tournament tournament = new Tournament(
                0,
                TOURNAMENT_NAME,
                null,
                startDate,
                endDate,
                MAX_PARTICIPANTS,
                TOURNAMENT_LOCATION);

        // Tester différents statuts
        TournamentStatus[] statuses = {
                TournamentStatus.PLANNING,
                TournamentStatus.IN_PROGRESS,
                TournamentStatus.COMPLETED
        };

        for (TournamentStatus status : statuses) {
            tournament.setStatus(status);
            tournamentFacade.createTournament(tournament);

            // Récupérer et vérifier
            List<Tournament> tournaments = tournamentFacade.getAllTournaments();
            Tournament retrieved = tournaments.stream()
                    .filter(t -> TOURNAMENT_NAME.equals(t.getName()))
                    .findFirst()
                    .orElse(null);

            assertNotNull("Le tournoi devrait exister", retrieved);
            assertEquals("Le statut devrait correspondre", status, retrieved.getStatus());

            // Nettoyer pour le prochain test
            tournamentFacade.deleteTournament(retrieved);
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private void cleanupTestData() throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            // Supprimer les inscriptions de test
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM tournament_registrations WHERE tournament_id IN (SELECT tournament_id FROM tournaments WHERE name LIKE ?)")) {
                stmt.setString(1, TOURNAMENT_NAME + "%");
                stmt.executeUpdate();
            }

            // Supprimer les matchs de test
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM matchs WHERE tournament_id IN (SELECT tournament_id FROM tournaments WHERE name LIKE ?)")) {
                stmt.setString(1, TOURNAMENT_NAME + "%");
                stmt.executeUpdate();
            }

            // Supprimer les tournois de test
            try (PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM tournaments WHERE name LIKE ?")) {
                stmt.setString(1, TOURNAMENT_NAME + "%");
                stmt.executeUpdate();
            }
        }
    }
}
