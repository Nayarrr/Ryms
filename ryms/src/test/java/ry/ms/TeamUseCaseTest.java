package ry.ms;

import junit.framework.TestCase;
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

    private static final String CAPTAIN_EMAIL = "junit_captain@test.com";
    private static final String MEMBER_EMAIL = "junit_member@test.com";
    private static final String TEAM_NAME = "JUnit_Team_Alpha";

    /**
     * Test the full flow: Create -> Invite -> Accept -> Verify Roster
     */
    public void testTeamScenario() throws Exception {
        cleanData();
        createUsers();

        TeamController controller = new TeamController();
        InvitationDAO invitationDAO = new InvitationDAOPostgres();
        TeamDAO teamDAO = new TeamDAOPostgres();

        try {
            Team team = controller.createTeam(TEAM_NAME, "JUNIT", "logo.png", CAPTAIN_EMAIL);
            assertNotNull("Team should not be null", team);
            assertEquals("Team name should match", TEAM_NAME, team.getName());
            assertNotNull("Team ID should be generated", team.getTeamId());
            assertTrue("Captain should be a member", team.getMemberEmails().contains(CAPTAIN_EMAIL));

            controller.inviteMember(team.getTeamId(), CAPTAIN_EMAIL, MEMBER_EMAIL);

            List<Invitation> pending = invitationDAO.findPendingByReceiver(MEMBER_EMAIL);
            assertFalse("Should find pending invitations", pending.isEmpty());

            Invitation invite = pending.get(0);
            assertEquals("Sender should be captain", CAPTAIN_EMAIL, invite.getSender());
            assertEquals("Status should be PENDING", "PENDING", invite.getStatus().name());

            controller.acceptInvitation(invite.getId());

            Team updatedTeam = teamDAO.getTeamById(team.getTeamId());
            assertTrue("New member should be in the team", updatedTeam.getMemberEmails().contains(MEMBER_EMAIL));

            Invitation acceptedInvite = invitationDAO.findById(invite.getId());
            assertEquals("Invitation status should be ACCEPTED", "ACCEPTED", acceptedInvite.getStatus().name());
        } finally {
            cleanData();
        }
    }

    private void createUsers() throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (email, username, password) VALUES (?, ?, ?), (?, ?, ?)")) {
                stmt.setString(1, CAPTAIN_EMAIL);
                stmt.setString(2, "CaptainJUnit");
                stmt.setString(3, "pass");
                stmt.setString(4, MEMBER_EMAIL);
                stmt.setString(5, "MemberJUnit");
                stmt.setString(6, "pass");
                stmt.executeUpdate();
            }
        }
    }

    private void cleanData() throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM teams WHERE name = ?")) {
                stmt.setString(1, TEAM_NAME);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE email IN (?, ?)")) {
                stmt.setString(1, CAPTAIN_EMAIL);
                stmt.setString(2, MEMBER_EMAIL);
                stmt.executeUpdate();
            }
        }
    }
}