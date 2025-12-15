package ry.ms.persistLogic.team.dao;

import java.sql.SQLException;
import java.util.List;

import ry.ms.businessLogic.team.models.Invitation;

public interface InvitationDAO {

    void createInvitation(Invitation invitation) throws SQLException;

    void updateInvitationStatus(Long invitationId, String newStatus) throws SQLException;

    List<Invitation> getPendingInvitationsForUser(String userEmail) throws SQLException;
}