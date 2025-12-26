package ry.ms.persistLogic.match.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import ry.ms.persistLogic.match.dao.MatchDAO;

public class MatchDAOPostgres extends MatchDAO{
    private static final String URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String USER = "ryms";
    private static final String PASSWORD = "ryms";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public MatchDAOPostgres(){
        super(initConnection());
    }

    private static Connection initConnection(){
        try {
            return getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database", e);
        }
    }

    @Override
    public User getUserById(String email) throws SQLException {
        String sql = "SELECT email, username, password, avatar FROM users WHERE email = ?";
        
        // Using try-with-resources to ensure PreparedStatement and ResultSet are closed automatically.
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Match getMatchById(Long matchid) throws SQLException{
        String sql = "SELECT match_id, match_date, game_id FROM matchs WHERE match_id = ?";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)){
            stmt.setLong(1, matchid);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return new Match(
                        rs.getLong("match_id"),
                        rs.getTimestamp("match_date"),
                        rs.getLong("game_id")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public Team getTeamById(Long teamid) throws SQLException{
        String sql = "SELECT team_id, name, tag, avatar, captain_email, created_at FROM teams WHERE team_id = ?";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)){
            stmt.setLong(1, teamid);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    Team team = new Team();
                    team.setTeamId(rs.getLong("team_id"));
                    team.setName(rs.getString("name"));
                    team.setTag(rs.getString("tag"));
                    team.setAvatar(rs.getString("avatar"));
                    team.setCaptainEmail(rs.getString("captain_email"));
                    return team;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addReferee(Match match , User referee) throws SQLException{
        if (match == null || match.getMatchId() == null || referee == null || referee.getEmail() == null) {
            throw new IllegalArgumentException("Match ID and referee email must be provided");
        }

        String sql = "INSERT INTO match_referees (match_id, referee_email) VALUES (?, ?) " +
                     "ON CONFLICT (match_id, referee_email) DO NOTHING";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setLong(1, match.getMatchId());
            stmt.setString(2, referee.getEmail());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean addDate(Match match, Date date) throws SQLException {
        if (match == null || match.getMatchId() == null || date == null) {
            throw new IllegalArgumentException("Match ID and date must be provided");
        }

        String sql = "UPDATE matchs SET match_date = ? WHERE match_id = ?";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, new java.sql.Timestamp(date.getTime())); // Convertir java.util.Date en java.sql.Timestamp pour PostgreSQL
            stmt.setLong(2, match.getMatchId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                match.setMatchDate(date);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean addTeam(Match match, Team team) throws SQLException {
        if (match == null || match.getMatchId() == null || team == null || team.getTeamId() == null) {
            throw new IllegalArgumentException("Match ID and Team ID must be provided");
        }

        String sql = "INSERT INTO match_teams (match_id, team_id) VALUES (?, ?) " +
                     "ON CONFLICT (match_id, team_id) DO NOTHING";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setLong(1, match.getMatchId());
            stmt.setLong(2, team.getTeamId());
            
            int affectedRows = stmt.executeUpdate();
            
            // Si l'insertion a réussi, ajouter l'équipe à l'objet Match en mémoire
            if (affectedRows > 0) {
                match.getTeams().add(team);
                return true;
            }
            return false; // L'équipe était déjà associée au match
        }
    }

    @Override
    public boolean updateRoaster(Long teamId, User currentUser, User newUser) throws SQLException {
        if (teamId == null || currentUser == null || currentUser.getEmail() == null || 
            newUser == null || newUser.getEmail() == null) {
            throw new IllegalArgumentException("Team ID, current user and new user must be provided with valid emails");
        }

        String currentUserEmail = currentUser.getEmail();
        String newUserEmail = newUser.getEmail();

        // Utiliser une transaction pour garantir l'atomicité
        conn.setAutoCommit(false);
        
        try {
            String checkSql = "SELECT 1 FROM team_members WHERE team_id = ? AND user_email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setLong(1, teamId);
                checkStmt.setString(2, currentUserEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false; // L'utilisateur actuel n'est pas dans l'équipe
                }
            }

            String deleteSql = "DELETE FROM team_members WHERE team_id = ? AND user_email = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setLong(1, teamId);
                deleteStmt.setString(2, currentUserEmail);
                deleteStmt.executeUpdate();
            }

            String insertSql = "INSERT INTO team_members (team_id, user_email) VALUES (?, ?) " +
                             "ON CONFLICT (team_id, user_email) DO NOTHING";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setLong(1, teamId);
                insertStmt.setString(2, newUserEmail);
                int inserted = insertStmt.executeUpdate();
                
                if (inserted == 0) {
                    // Le nouveau membre était déjà dans l'équipe
                    conn.rollback();
                    conn.setAutoCommit(true);
                    return false;
                }
            }
            
            conn.commit();
            conn.setAutoCommit(true);
            return true;
            
        } catch (SQLException e) {
            conn.rollback();
            conn.setAutoCommit(true);
            throw e;
        }
    }

}