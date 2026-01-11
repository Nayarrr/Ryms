package ry.ms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ry.ms.businessLogic.match.MatchFacade;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Test d'intégration pour le use case de gestion des matchs
 */
public class MatchUseCaseTest extends TestCase {

    // Constantes de test
    private static final String TEAM1_NAME = "JUnit_Team_Alpha";
    private static final String TEAM1_TAG = "JUTA";
    private static final String TEAM2_NAME = "JUnit_Team_Beta";
    private static final String TEAM2_TAG = "JUTB";
    private static final String REFEREE_EMAIL = "junit_referee@test.com";
    private static final String CAPTAIN1_EMAIL = "junit_captain1@test.com";
    private static final String CAPTAIN2_EMAIL = "junit_captain2@test.com";
    private static final int TOURNAMENT_ID = 1;

    // Composants
    private MatchFacade matchFacade;
    private Long createdMatchId;
    private Long team1Id;
    private Long team2Id;

    public MatchUseCaseTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(MatchUseCaseTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        matchFacade = MatchFacade.getMatchFacade();

        try (Connection conn = DBConfig.getConnection()) {
            // 1. Nettoyer les données de test existantes
            cleanupTestData(conn);

            // 2. Créer les utilisateurs de test
            createTestUser(conn, CAPTAIN1_EMAIL, "Captain1JUnit");
            createTestUser(conn, CAPTAIN2_EMAIL, "Captain2JUnit");
            createTestUser(conn, REFEREE_EMAIL, "RefereeJUnit");

            // 3. Créer les équipes de test
            team1Id = createTestTeam(conn, TEAM1_NAME, TEAM1_TAG, CAPTAIN1_EMAIL);
            team2Id = createTestTeam(conn, TEAM2_NAME, TEAM2_TAG, CAPTAIN2_EMAIL);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        try (Connection conn = DBConfig.getConnection()) {
            cleanupTestData(conn);
        }
        super.tearDown();
    }

    /**
     * Test 1: Créer un match et ajouter des équipes
     */
    public void testCreateMatchAndAddTeams() throws Exception {
        // 1. CRÉER UN MATCH
        Date matchDate = new Date(System.currentTimeMillis() + 86400000); // Demain
        createdMatchId = matchFacade.createMatch(matchDate, TOURNAMENT_ID);

        assertNotNull("Le match devrait être créé", createdMatchId);
        assertTrue("L'ID du match devrait être positif", createdMatchId > 0);

        // 2. AJOUTER LES ÉQUIPES AU MATCH
        boolean team1Added = matchFacade.addTeam(createdMatchId, team1Id);
        boolean team2Added = matchFacade.addTeam(createdMatchId, team2Id);

        assertTrue("L'équipe 1 devrait être ajoutée", team1Added);
        assertTrue("L'équipe 2 devrait être ajoutée", team2Added);

        // 3. VÉRIFIER QUE LE MATCH EXISTE ET CONTIENT LES ÉQUIPES
        Match retrievedMatch = matchFacade.getMatchById(createdMatchId);
        assertNotNull("Le match devrait être récupérable", retrievedMatch);

        List<Team> teams = matchFacade.getTeamsForMatch(createdMatchId);
        assertEquals("Le match devrait avoir 2 équipes", 2, teams.size());
    }

    /**
     * Test 2: Ajouter une date et un arbitre à un match
     */
    public void testAddDateAndReferee() throws Exception {
        // 1. Créer un match
        Date matchDate = new Date(System.currentTimeMillis() + 86400000);
        createdMatchId = matchFacade.createMatch(matchDate, TOURNAMENT_ID);

        // 2. Ajouter les équipes
        matchFacade.addTeam(createdMatchId, team1Id);
        matchFacade.addTeam(createdMatchId, team2Id);

        // 3. AJOUTER UNE NOUVELLE DATE
        Date newDate = new Date(System.currentTimeMillis() + 172800000); // Dans 2 jours
        boolean dateUpdated = matchFacade.addDate(createdMatchId, newDate);

        assertTrue("La date devrait être mise à jour", dateUpdated);

        // Vérifier la mise à jour de la date
        Match matchWithNewDate = matchFacade.getMatchById(createdMatchId);
        assertNotNull("La date du match ne devrait pas être null", matchWithNewDate.getMatchDate());

        // 4. AJOUTER UN ARBITRE
        boolean refereeAdded = matchFacade.addReferee(createdMatchId, REFEREE_EMAIL);

        assertTrue("L'arbitre devrait être ajouté", refereeAdded);

        // 5. VÉRIFIER QUE L'ARBITRE EST BIEN AJOUTÉ
        Match finalMatch = matchFacade.getMatchById(createdMatchId);
        List<User> referees = finalMatch.getReferees();

        assertNotNull("La liste d'arbitres ne devrait pas être null", referees);
        assertEquals("Le match devrait avoir 1 arbitre", 1, referees.size());
        assertEquals("L'email de l'arbitre devrait correspondre", REFEREE_EMAIL, referees.get(0).getEmail());
    }

    /**
     * Test 3: Créer un match complet avec équipes et supprimer
     */
    public void testCreateCompleteMatchAndDelete() throws Exception {
        // 1. Créer un match avec équipes
        Date matchDate = new Date(System.currentTimeMillis() + 86400000);
        createdMatchId = matchFacade.createMatch(team1Id, team2Id, matchDate, TOURNAMENT_ID);

        assertNotNull("Le match devrait être créé", createdMatchId);
        assertTrue("L'ID du match devrait être positif", createdMatchId > 0);

        // 2. Vérifier que les équipes sont bien ajoutées
        List<Team> teams = matchFacade.getTeamsForMatch(createdMatchId);
        assertEquals("Le match devrait avoir 2 équipes", 2, teams.size());

        // 3. Supprimer le match
        boolean deleted = matchFacade.deleteMatch(createdMatchId);
        assertTrue("Le match devrait être supprimé", deleted);

        // 4. Vérifier que le match n'existe plus
        try {
            matchFacade.getMatchById(createdMatchId);
            fail("Le match ne devrait plus exister");
        } catch (Exception e) {
            // Expected - le match a été supprimé
            assertTrue(true);
        }
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private void cleanupTestData(Connection conn) throws SQLException {
        // Supprimer dans l'ordre inverse des dépendances
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM match_referees WHERE match_id IN (SELECT match_id FROM matchs WHERE tournament_id = ?)")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM match_teams WHERE match_id IN (SELECT match_id FROM matchs WHERE tournament_id = ?)")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM matchs WHERE tournament_id = ?")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM team_members WHERE team_id IN (SELECT team_id FROM teams WHERE name IN (?, ?))")) {
            stmt.setString(1, TEAM1_NAME);
            stmt.setString(2, TEAM2_NAME);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM teams WHERE name IN (?, ?)")) {
            stmt.setString(1, TEAM1_NAME);
            stmt.setString(2, TEAM2_NAME);
            stmt.executeUpdate();
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email IN (?, ?, ?)")) {
            stmt.setString(1, CAPTAIN1_EMAIL);
            stmt.setString(2, CAPTAIN2_EMAIL);
            stmt.setString(3, REFEREE_EMAIL);
            stmt.executeUpdate();
        }
    }

    private void createTestUser(Connection conn, String email, String username) throws SQLException {
        try (PreparedStatement stmt = conn
                .prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)")) {
            stmt.setString(1, email);
            stmt.setString(2, username);
            stmt.setString(3, "testpass");
            stmt.executeUpdate();
        }
    }

    private Long createTestTeam(Connection conn, String name, String tag, String captainEmail) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO teams (name, tag, captain_email) VALUES (?, ?, ?) RETURNING team_id")) {
            stmt.setString(1, name);
            stmt.setString(2, tag);
            stmt.setString(3, captainEmail);

            var rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("team_id");
            }
        }
        return null;
    }
}
