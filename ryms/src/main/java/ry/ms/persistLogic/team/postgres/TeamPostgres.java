package ry.ms.persistLogic.team.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.team.dao.TeamDAO;

public class TeamPostgres implements TeamDAO {

    private static final String URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String USER = "ryms";
    private static final String PASSWORD = "ryms";

    private static final String INSERT_TEAM_SQL =
            "INSERT INTO teams (name, tag, avatar, captain_email) VALUES (?, ?, ?, ?)";
    private static final String INSERT_CAPTAIN_SQL =
            "INSERT INTO team_members (team_id, user_email) VALUES (?, ?)";
    private static final String SELECT_TEAM_BY_NAME_SQL =
            "SELECT team_id, name, tag, avatar, captain_email FROM teams WHERE LOWER(name) = LOWER(?)";

    @Override
    public Team saveTeam(Team team) throws SQLException {
        try (Connection conn = getConnection()) {
            boolean initialAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                Long teamId = insertTeam(conn, team);
                insertCaptain(conn, teamId, team.getCaptainEmail());
                conn.commit();
                team.setTeamId(teamId);
                return team;
            } catch (SQLException ex) {
                conn.rollback();
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
                System.err.println("Error while saving team: " + ex.getMessage());
                throw ex;
            } finally {
                conn.setAutoCommit(initialAutoCommit);
            }
        }
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

    @Override
    public Team getTeamByName(String name) throws SQLException {
        if (name == null) {
            return null;
        }
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_TEAM_BY_NAME_SQL)) {
            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapTeam(rs);
                }
            }
            return null;
        }
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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}