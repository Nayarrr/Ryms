package ry.ms.businessLogic.team;

import java.sql.SQLException;

import ry.ms.AbsFactory;
import ry.ms.PostgresFactory;
import ry.ms.businessLogic.team.models.Team;

/**
 * Facade exposing team-related use cases.
 */
public class SessionFacade {

    private static SessionFacade instance;
    private final TeamManager teamManager;

    private SessionFacade(AbsFactory factory) {
        this.teamManager = new TeamManager(factory);
    }

    public static SessionFacade getInstance() {
        if (instance == null) {
            instance = new SessionFacade(new PostgresFactory());
        }
        return instance;
    }

    public Team createTeam(String name, String tag, String avatar, String captainEmail) throws SQLException {
        return teamManager.createTeam(name, tag, avatar, captainEmail);
    }

    public void inviteMember(Long teamId, String senderEmail, String receiverEmail) {
        teamManager.inviteMember(teamId, senderEmail, receiverEmail);
    }

    public void removeMember(Long teamId, String userEmail) {
        teamManager.removeMember(teamId, userEmail);
    }

    public void acceptInvitation(Long invitationId) {
        teamManager.acceptInvitation(invitationId);
    }

    public void rejectInvitation(Long invitationId) {
        teamManager.rejectInvitation(invitationId);
    }
}
