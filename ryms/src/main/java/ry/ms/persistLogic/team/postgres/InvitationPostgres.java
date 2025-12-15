package ry.ms.persistLogic.team.postgres;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.persistLogic.team.dao.InvitationDAO;

public class InvitationPostgres implements InvitationDAO {

    @Override
    public void createInvitation(Invitation invitation) throws SQLException {
        // TODO implement invitation persistence
    }

    @Override
    public void updateInvitationStatus(Long invitationId, String newStatus) throws SQLException {
        // TODO implement status update persistence
    }

    @Override
    public List<Invitation> getPendingInvitationsForUser(String userEmail) throws SQLException {
        // TODO implement invitation retrieval
        return Collections.emptyList();
    }
}