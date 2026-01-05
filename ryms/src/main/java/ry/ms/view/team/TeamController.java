package ry.ms.view.team;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import ry.ms.businessLogic.team.TeamFacade;
import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.businessLogic.team.models.Team;

public class TeamController {

    private final TeamFacade teamFacade;

    public TeamController() {
        this(TeamFacade.getInstance());
    }

    public TeamController(TeamFacade teamFacade) {
        this.teamFacade = Objects.requireNonNull(teamFacade);
    }

    public Team createTeam(String name, String tag, String avatar, String userEmail) {
        try {
            return teamFacade.createTeam(name, tag, avatar, userEmail);
        } catch (SQLException ex) {
            return handleSqlException("Failed to create team", ex);
        }
    }

    public void inviteMember(Long teamId, String senderEmail, String targetEmail) {
        runWithHandling(() -> teamFacade.inviteMember(teamId, senderEmail, targetEmail),
                "Failed to invite member");
    }

    public void acceptInvitation(Long invitationId) {
        runWithHandling(() -> teamFacade.acceptInvitation(invitationId),
                "Failed to accept invitation");
    }

    public void rejectInvitation(Long invitationId) {
        runWithHandling(() -> teamFacade.rejectInvitation(invitationId),
                "Failed to reject invitation");
    }

    public void removeMember(Long teamId, String captainEmail, String targetMemberEmail) {
        runWithHandling(() -> teamFacade.removeMember(teamId, captainEmail, targetMemberEmail),
                "Failed to remove member");
    }

    public void leaveTeam(Long teamId, String userEmail) {
        runWithHandling(() -> teamFacade.leaveTeam(teamId, userEmail),
                "Failed to leave team");
    }

    public void transferCaptaincy(Long teamId, String currentCaptain, String newCaptain) {
        runWithHandling(() -> teamFacade.transferCaptaincy(teamId, currentCaptain, newCaptain),
                "Failed to transfer captaincy");
    }

    public void dissolveTeam(Long teamId, String captainEmail) {
        runWithHandling(() -> teamFacade.dissolveTeam(teamId, captainEmail),
                "Failed to dissolve team");
    }

    public Team getTeamByMemberEmail(String userEmail) {
        try {
            return teamFacade.getTeamByMemberEmail(userEmail);
        } catch (SQLException ex) {
            return handleSqlException("Failed to load team by member email", ex);
        }
    }

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