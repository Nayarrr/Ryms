package ry.ms.persistLogic.team.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.persistLogic.team.dao.InvitationDAO;

public class InvitationDAOPostgres implements InvitationDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String USER = "ryms";
    private static final String PASSWORD = "ryms";

    private static final String INSERT_INVITATION_SQL =
            "INSERT INTO invitations (team_id, sender_email, receiver_email, status) VALUES (?, ?, ?, ?)";

    private static final String SELECT_INVITATION_SQL =
            "SELECT invitation_id, team_id, sender_email, receiver_email, status, sent_at FROM invitations WHERE invitation_id = ?";

    private static final String UPDATE_STATUS_SQL =
            "UPDATE invitations SET status = ? WHERE invitation_id = ?";

    private static final String SELECT_PENDING_BY_RECEIVER_SQL =
            "SELECT invitation_id, team_id, sender_email, receiver_email, status, sent_at " +
                    "FROM invitations WHERE receiver_email = ? AND status = 'PENDING'";

    @Override
    public void save(Invitation invitation) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_INVITATION_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, invitation.getTeamId());
            stmt.setString(2, invitation.getSender());
            stmt.setString(3, invitation.getReceiver());
            stmt.setString(4, invitation.getStatus() == null ? "PENDING" : invitation.getStatus());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    invitation.setId(keys.getLong(1));
                }
            }
        }
    }

    @Override
    public Invitation findById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_INVITATION_SQL)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapInvitation(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void updateStatus(Long invitationId, String newStatus) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS_SQL)) {

            stmt.setString(1, newStatus);
            stmt.setLong(2, invitationId);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<Invitation> findPendingByReceiver(String userEmail) throws SQLException {
        List<Invitation> invitations = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PENDING_BY_RECEIVER_SQL)) {

            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    invitations.add(mapInvitation(rs));
                }
            }
        }
        return invitations;
    }

    private Invitation mapInvitation(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("sent_at");
        Date sentAt = timestamp == null ? null : new Date(timestamp.getTime());
        return new Invitation(
                rs.getLong("invitation_id"),
                rs.getLong("team_id"),
                rs.getString("sender_email"),
                rs.getString("receiver_email"),
                rs.getString("status"),
                sentAt
        );
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}