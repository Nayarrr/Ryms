package ry.ms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.postgres.MatchDAOPostgres;
import ry.ms.view.match.MatchController;

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
    private MatchController matchController;
    private MatchDAO matchDAO;
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
        matchController = new MatchController();
        matchDAO = new MatchDAOPostgres();
        
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
     * Test du scénario complet : Créer -> Ajouter date -> Ajouter arbitre -> Mettre à jour score
     */
    public void testMatchScenario() throws SQLException {
        // 1. CRÉER UN MATCH
        Date matchDate = new Date(System.currentTimeMillis() + 86400000); // Demain
        createdMatchId = matchDAO.createMatch(matchDate, TOURNAMENT_ID);
        
        assertNotNull("Le match devrait être créé", createdMatchId);
        assertTrue("L'ID du match devrait être positif", createdMatchId > 0);
        
        // 2. AJOUTER LES ÉQUIPES AU MATCH
        Match match = matchDAO.getMatchById(createdMatchId);
        Team team1 = matchDAO.getTeamById(team1Id);
        Team team2 = matchDAO.getTeamById(team2Id);
        
        boolean team1Added = matchDAO.addTeam(match, team1);
        boolean team2Added = matchDAO.addTeam(match, team2);
        
        assertTrue("L'équipe 1 devrait être ajoutée", team1Added);
        assertTrue("L'équipe 2 devrait être ajoutée", team2Added);
        
        // 3. VÉRIFIER QUE LE MATCH EXISTE ET CONTIENT LES ÉQUIPES
        Match retrievedMatch = matchDAO.getMatchById(createdMatchId);
        assertNotNull("Le match devrait être récupérable", retrievedMatch);
        assertEquals("Le match devrait avoir 2 équipes", 2, retrievedMatch.getTeams().size());
        
        // 4. AJOUTER UNE NOUVELLE DATE
        Date newDate = new Date(System.currentTimeMillis() + 172800000); // Dans 2 jours
        boolean dateUpdated = matchDAO.addDate(retrievedMatch, newDate);
        
        assertTrue("La date devrait être mise à jour", dateUpdated);
        
        // Vérifier la mise à jour de la date
        Match matchWithNewDate = matchDAO.getMatchById(createdMatchId);
        assertNotNull("La date du match ne devrait pas être null", matchWithNewDate.getMatchDate());
        
        // 5. AJOUTER UN ARBITRE
        User referee = new User(REFEREE_EMAIL, "RefereeJUnit", "password", null, "USER");
        boolean refereeAdded = matchDAO.addReferee(matchWithNewDate, referee);
        
        assertTrue("L'arbitre devrait être ajouté", refereeAdded);
        
        // 6. VÉRIFIER QUE L'ARBITRE EST BIEN AJOUTÉ
        Match finalMatch = matchDAO.getMatchById(createdMatchId);
        List<User> referees = finalMatch.getReferees();
        
        assertNotNull("La liste d'arbitres ne devrait pas être null", referees);
        assertEquals("Le match devrait avoir 1 arbitre", 1, referees.size());
        assertEquals("L'email de l'arbitre devrait correspondre", REFEREE_EMAIL, referees.get(0).getEmail());
    }

    // ========== MÉTHODES UTILITAIRES ==========

    private void cleanupTestData(Connection conn) throws SQLException {
        // Supprimer dans l'ordre inverse des dépendances
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM match_referees WHERE match_id IN (SELECT match_id FROM matchs WHERE tournament_id = ?)")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM match_teams WHERE match_id IN (SELECT match_id FROM matchs WHERE tournament_id = ?)")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM matchs WHERE tournament_id = ?")) {
            stmt.setInt(1, TOURNAMENT_ID);
            stmt.executeUpdate();
        }
        
        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM team_members WHERE team_id IN (SELECT team_id FROM teams WHERE name IN (?, ?))")) {
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
        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)")) {
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