package ry.ms.persistLogic.team.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;

import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.DBConfig;

public class TeamDAOPostgres implements TeamDAO {

    private static final String INSERT_TEAM_SQL =
            "INSERT INTO teams (name, tag, avatar, captain_email) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CAPTAIN_SQL =
            "INSERT INTO team_members (team_id, user_email) VALUES (?, ?)";
    private static final String SELECT_TEAM_BY_NAME_SQL =
            "SELECT team_id, name, tag, avatar, captain_email FROM teams WHERE LOWER(name) = LOWER(?)";
    private static final String SELECT_TEAM_BY_ID_SQL =
            "SELECT team_id, name, tag, avatar, captain_email FROM teams WHERE team_id = ?";
    private static final String SELECT_TEAM_MEMBERS_SQL =
            "SELECT user_email FROM team_members WHERE team_id = ? ORDER BY joined_at";
    private static final String INSERT_MEMBER_SQL =
            "INSERT INTO team_members (team_id, user_email) VALUES (?, ?)";
    private static final String DELETE_MEMBER_SQL =
            "DELETE FROM team_members WHERE team_id = ? AND user_email = ?";
    private static final String CHECK_MEMBER_SQL =
            "SELECT 1 FROM team_members WHERE team_id = ? AND user_email = ? LIMIT 1";
    private static final String UPDATE_CAPTAIN_SQL =
            "UPDATE teams SET captain_email = ? WHERE team_id = ?";
    private static final String DELETE_TEAM_SQL =
            "DELETE FROM teams WHERE team_id = ?";
    private static final String SELECT_TEAM_BY_MEMBER_SQL =
    "SELECT t.* FROM teams t JOIN team_members tm ON t.team_id = tm.team_id WHERE tm.user_email = ?";

    @Override
    public Team saveTeam(Team team) throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            boolean initialAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                Long teamId = insertTeam(conn, team);
                insertCaptain(conn, teamId, team.getCaptainEmail());
                conn.commit();
                team.setTeamId(teamId);
                team.setMemberEmails(getMembers(conn, teamId));
                return team;
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("Error while saving team: " + ex.getMessage());
                throw ex;
            } finally {
                conn.setAutoCommit(initialAutoCommit);
            }
        }
    }

    @Override
    public Team getTeamByName(String name) throws SQLException {
        if (name == null) {
            return null;
        }
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TEAM_BY_NAME_SQL)) {

            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Team team = mapTeam(rs);
                    team.setMemberEmails(getMembers(conn, team.getTeamId()));
                    return team;
                }
            }
        }
        return null;
    }

    @Override
    public Team getTeamById(Long id) throws SQLException {
        if (id == null) {
            return null;
        }
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TEAM_BY_ID_SQL)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Team team = mapTeam(rs);
                    team.setMemberEmails(getMembers(conn, team.getTeamId()));
                    return team;
                }
            }
        }
        return null;
    }

    @Override
    public void addMember(Long teamId, String userEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_MEMBER_SQL)) {

            stmt.setLong(1, teamId);
            stmt.setString(2, userEmail);
            stmt.executeUpdate();
        }
    }

    @Override
    public void removeMember(Long teamId, String userEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_MEMBER_SQL)) {

            stmt.setLong(1, teamId);
            stmt.setString(2, userEmail);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean isMember(Long teamId, String userEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_MEMBER_SQL)) {

            stmt.setLong(1, teamId);
            stmt.setString(2, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void updateCaptain(Long teamId, String newCaptainEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_CAPTAIN_SQL)) {

            stmt.setString(1, newCaptainEmail);
            stmt.setLong(2, teamId);
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteTeam(Long teamId) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_TEAM_SQL)) {

            stmt.setLong(1, teamId);
            stmt.executeUpdate();
        }
    }

    @Override
    public Team getTeamByMemberEmail(String userEmail) throws SQLException {
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SELECT_TEAM_BY_MEMBER_SQL)) {
            stmt.setString(1, userEmail);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Team team = mapTeam(rs); // Utilise ta méthode mapTeam existante
                    // Charge les membres (utilise ta méthode getMembers existante)
                    team.setMemberEmails(getMembers(conn, team.getTeamId())); 
                    return team;
                }
            }
        }
        return null;
    }

    private Long insertTeam(Connection conn, Team team) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_TEAM_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, team.getName());
            stmt.setString(2, team.getTag());
            stmt.setString(3, team.getAvatar());
            stmt.setString(4, team.getCaptainEmail());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Unable to retrieve generated team identifier.");
    }

    private void insertCaptain(Connection conn, Long teamId, String captainEmail) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(INSERT_CAPTAIN_SQL)) {
            stmt.setLong(1, teamId);
            stmt.setString(2, captainEmail);
            stmt.executeUpdate();
        }
    }

    private List<String> getMembers(Connection conn, Long teamId) throws SQLException {
        List<String> members = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_TEAM_MEMBERS_SQL)) {
            stmt.setLong(1, teamId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(rs.getString("user_email"));
                }
            }
        }
        return members;
    }

    private Team mapTeam(ResultSet rs) throws SQLException {
        return new Team(
                rs.getLong("team_id"),
                rs.getString("name"),
                rs.getString("tag"),
                rs.getString("avatar"),
                rs.getString("captain_email")
        );
    }
}