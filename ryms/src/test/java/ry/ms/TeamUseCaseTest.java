package ry.ms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.postgres.InvitationDAOPostgres;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;
import ry.ms.view.team.TeamController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Integration test for the Team Management Use Case.
 */
public class TeamUseCaseTest extends TestCase {

    // Test Data Constants
    private static final String CAPTAIN_EMAIL = "junit_captain@test.com";
    private static final String MEMBER_EMAIL = "junit_member@test.com";
    private static final String TEAM_NAME = "JUnit_Team_Alpha";
    
    // Components
    private TeamController controller;
    private InvitationDAO invitationDAO;
    private TeamDAO teamDAO;

    /**
     * Create the test case
     * @param testName name of the test case
     */
    public TeamUseCaseTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TeamUseCaseTest.class);
    }

    /**
     * Sets up the fixture, for example, open a network connection.
     * This method is called before a test is executed.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        controller = new TeamController();
        invitationDAO = new InvitationDAOPostgres();
        teamDAO = new TeamDAOPostgres();
        
        // Prepare DB: Clean old data and create users
        try (Connection conn = DBConfig.getConnection()) {
            // 1. Clean existing team if exists
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM teams WHERE name = ?")) {
                stmt.setString(1, TEAM_NAME);
                stmt.executeUpdate();
            }
            // 2. Clean users
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email IN (?, ?)")) {
                stmt.setString(1, CAPTAIN_EMAIL);
                stmt.setString(2, MEMBER_EMAIL);
                stmt.executeUpdate();
            }
            // 3. Create Captain
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)")) {
                stmt.setString(1, CAPTAIN_EMAIL);
                stmt.setString(2, "CaptainJUnit");
                stmt.setString(3, "pass");
                stmt.executeUpdate();
            }
            // 4. Create Member
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?)")) {
                stmt.setString(1, MEMBER_EMAIL);
                stmt.setString(2, "MemberJUnit");
                stmt.setString(3, "pass");
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Tears down the fixture, for example, close a network connection.
     * This method is called after a test is executed.
     */
    @Override
    protected void tearDown() throws Exception {
        // Clean up the created team
        try (Connection conn = DBConfig.getConnection()) {
             try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM teams WHERE name = ?")) {
                stmt.setString(1, TEAM_NAME);
                stmt.executeUpdate();
            }
             // Clean users
             try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email IN (?, ?)")) {
                 stmt.setString(1, CAPTAIN_EMAIL);
                 stmt.setString(2, MEMBER_EMAIL);
                 stmt.executeUpdate();
             }
        }
        super.tearDown();
    }

    /**
     * Test the full flow: Create -> Invite -> Accept -> Verify Roster
     */
    public void testTeamScenario() throws SQLException {
        // 1. CREATE TEAM
        Team team = controller.createTeam(TEAM_NAME, "JUNIT", "logo.png", CAPTAIN_EMAIL);
        
        assertNotNull("Team should not be null", team);
        assertEquals("Team name should match", TEAM_NAME, team.getName());
        assertNotNull("Team ID should be generated", team.getTeamId());
        
        // Verify Captain is in roster
        assertTrue("Captain should be a member", team.getMemberEmails().contains(CAPTAIN_EMAIL));

        // 2. INVITE MEMBER
        controller.inviteMember(team.getTeamId(), CAPTAIN_EMAIL, MEMBER_EMAIL);

        // 3. VERIFY INVITATION EXISTS
        List<Invitation> pending = invitationDAO.findPendingByReceiver(MEMBER_EMAIL);
        assertFalse("Should find pending invitations", pending.isEmpty());
        
        Invitation invite = pending.get(0);
        assertEquals("Sender should be captain", CAPTAIN_EMAIL, invite.getSender());
        assertEquals("Status should be PENDING", "PENDING", invite.getStatus().name());

        // 4. ACCEPT INVITATION
        controller.acceptInvitation(invite.getId());

        // 5. VERIFY ROSTER UPDATED
        // Reload team from DB to check members
        Team updatedTeam = teamDAO.getTeamById(team.getTeamId());
        assertTrue("New member should be in the team", updatedTeam.getMemberEmails().contains(MEMBER_EMAIL));
        
        // 6. VERIFY INVITATION STATUS
        Invitation acceptedInvite = invitationDAO.findById(invite.getId());
        assertEquals("Invitation status should be ACCEPTED", "ACCEPTED", acceptedInvite.getStatus().name());
    }
}