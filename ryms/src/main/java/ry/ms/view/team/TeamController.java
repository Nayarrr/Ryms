package ry.ms.view.team;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import ry.ms.businessLogic.team.TeamFacade;
import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.businessLogic.team.models.Team;

/**
 * Controller for Team management operations.
 * Acts as a bridge between the view and the business logic (TeamFacade).
 */
public class TeamController {

    private final TeamFacade teamFacade;

    /**
     * Default constructor.
     * Uses the singleton instance of TeamFacade.
     */
    public TeamController() {
        this(TeamFacade.getInstance());
    }

    /**
     * Constructor with dependency injection.
     * 
     * @param teamFacade The TeamFacade instance to use.
     */
    public TeamController(TeamFacade teamFacade) {
        this.teamFacade = Objects.requireNonNull(teamFacade);
    }

    /**
     * Creates a new team.
     * 
     * @param name      Name of the team.
     * @param tag       Tag of the team.
     * @param avatar    Avatar URL or path.
     * @param userEmail Email of the creator (captain).
     * @return The created Team object.
     */
    public Team createTeam(String name, String tag, String avatar, String userEmail) {
        try {
            return teamFacade.createTeam(name, tag, avatar, userEmail);
        } catch (SQLException ex) {
            return handleSqlException("Failed to create team", ex);
        }
    }

    /**
     * Invites a member to the team.
     * 
     * @param teamId      ID of the team.
     * @param senderEmail Email of the inviter.
     * @param targetEmail Email of the invitee.
     */
    public void inviteMember(Long teamId, String senderEmail, String targetEmail) {
        runWithHandling(() -> teamFacade.inviteMember(teamId, senderEmail, targetEmail),
                "Failed to invite member");
    }

    /**
     * Accepts a team invitation.
     * 
     * @param invitationId ID of the invitation.
     */
    public void acceptInvitation(Long invitationId) {
        runWithHandling(() -> teamFacade.acceptInvitation(invitationId),
                "Failed to accept invitation");
    }

    /**
     * Rejects a team invitation.
     * 
     * @param invitationId ID of the invitation.
     */
    public void rejectInvitation(Long invitationId) {
        runWithHandling(() -> teamFacade.rejectInvitation(invitationId),
                "Failed to reject invitation");
    }

    /**
     * Removes a member from the team.
     * 
     * @param teamId            ID of the team.
     * @param captainEmail      Email of the captain performing the removal.
     * @param targetMemberEmail Email of the member to remove.
     */
    public void removeMember(Long teamId, String captainEmail, String targetMemberEmail) {
        runWithHandling(() -> teamFacade.removeMember(teamId, captainEmail, targetMemberEmail),
                "Failed to remove member");
    }

    /**
     * Leaves a team.
     * 
     * @param teamId    ID of the team.
     * @param userEmail Email of the user leaving.
     */
    public void leaveTeam(Long teamId, String userEmail) {
        runWithHandling(() -> teamFacade.leaveTeam(teamId, userEmail),
                "Failed to leave team");
    }

    /**
     * Transfers captaincy to another member.
     * 
     * @param teamId         ID of the team.
     * @param currentCaptain Email of the current captain.
     * @param newCaptain     Email of the new captain.
     */
    public void transferCaptaincy(Long teamId, String currentCaptain, String newCaptain) {
        runWithHandling(() -> teamFacade.transferCaptaincy(teamId, currentCaptain, newCaptain),
                "Failed to transfer captaincy");
    }

    /**
     * Dissolves a team.
     * 
     * @param teamId       ID of the team.
     * @param captainEmail Email of the captain.
     */
    public void dissolveTeam(Long teamId, String captainEmail) {
        runWithHandling(() -> teamFacade.dissolveTeam(teamId, captainEmail),
                "Failed to dissolve team");
    }

    /**
     * Gets a team by a member's email.
     * 
     * @param userEmail Email of the member.
     * @return The Team object, or null if not found.
     */
    public Team getTeamByMemberEmail(String userEmail) {
        try {
            return teamFacade.getTeamByMemberEmail(userEmail);
        } catch (SQLException ex) {
            return handleSqlException("Failed to load team by member email", ex);
        }
    }

    /**
     * Gets pending invitations for a user.
     * 
     * @param userEmail Email of the user.
     * @return List of Invitation objects.
     */
    public List<Invitation> getMyInvitations(String userEmail) {
        try {
            return teamFacade.getMyInvitations(userEmail);
        } catch (SQLException ex) {
            return handleSqlException("Failed to load invitations", ex);
        }
    }

    private void runWithHandling(SqlRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (SQLException ex) {
            handleSqlException(message, ex);
        }
    }

    private <T> T handleSqlException(String context, SQLException ex) {
        System.err.println(context + ": " + ex.getMessage());
        throw new RuntimeException(context, ex);
    }

    @FunctionalInterface
    private interface SqlRunnable {
        void run() throws SQLException;
    }
}