package ry.ms.persistLogic.team.dao;

import java.sql.SQLException;
import java.util.List;

import ry.ms.models.team.Invitation;
import ry.ms.models.team.InvitationStatus;

public interface InvitationDAO {

    void save(Invitation invitation) throws SQLException;

    Invitation findById(Long id) throws SQLException;

    void updateStatus(Long id, InvitationStatus status) throws SQLException;

    List<Invitation> findPendingByReceiver(String email) throws SQLException;

    Invitation getPendingInvitation(Long teamId, String receiverEmail) throws SQLException;
}