package ry.ms.businessLogic.team;

import java.sql.SQLException;

import ry.ms.AbsFactory;
import ry.ms.PostgresFactory;
import ry.ms.businessLogic.team.models.Team;

public final class SessionFacade {

    private static SessionFacade instance;

    private final TeamManager teamManager;

    private SessionFacade() {
        AbsFactory factory = new PostgresFactory();
        this.teamManager = new TeamManager(factory);
    }

    public static synchronized SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade();
        }
        return instance;
    }

    public Team createTeam(String name, String tag, String avatar, String captainEmail) throws SQLException {
        return teamManager.createTeam(name, tag, avatar, captainEmail);
    }

    public void inviteMember(Long teamId, String senderEmail, String targetEmail) throws SQLException {
        teamManager.inviteMember(teamId, senderEmail, targetEmail);
    }

    public void acceptInvitation(Long invitationId) throws SQLException {
        teamManager.acceptInvitation(invitationId);
    }

    public void rejectInvitation(Long invitationId) throws SQLException {
        teamManager.rejectInvitation(invitationId);
    }

    public void removeMember(Long teamId, String captainEmail, String targetMemberEmail) throws SQLException {
        teamManager.removeMember(teamId, captainEmail, targetMemberEmail);
    }

    public void leaveTeam(Long teamId, String userEmail) throws SQLException {
        teamManager.leaveTeam(teamId, userEmail);
    }

    public void transferCaptaincy(Long teamId, String currentCaptain, String newCaptain) throws SQLException {
        teamManager.transferCaptaincy(teamId, currentCaptain, newCaptain);
    }

    public void dissolveTeam(Long teamId, String captainEmail) throws SQLException {
        teamManager.dissolveTeam(teamId, captainEmail);
    }

    public Team getTeamByMemberEmail(String userEmail) throws SQLException {
        return teamManager.getTeamByMemberEmail(userEmail);
    }
}
