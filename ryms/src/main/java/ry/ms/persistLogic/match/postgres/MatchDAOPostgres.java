package ry.ms.persistLogic.match.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public Match getMatchById(Long matchId) throws SQLException {
        String query = "SELECT match_id, match_date, game_id FROM matchs WHERE match_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, matchId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Match(
                    rs.getLong("match_id"),
                    rs.getTimestamp("match_date"),
                    rs.getLong("game_id")
                );
            }
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du match " + matchId);
            e.printStackTrace();
            throw e;
        }
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

    @Override
    public List<User> getTeamMembers(Long teamId) throws SQLException {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.email, u.username, u.password, u.avatar " +
                    "FROM users u " +
                    "INNER JOIN team_members tm ON u.email = tm.user_email " +
                    "WHERE tm.team_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, teamId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar")
                    ));
                }
            }
        }
        return members;
    }

    @Override
    public Long createMatch(Date matchDate, int gameId) throws SQLException {
        String sql = "INSERT INTO matchs (match_date, game_id) VALUES (?, ?) RETURNING match_id";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new java.sql.Timestamp(matchDate.getTime()));
            stmt.setInt(2, gameId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("match_id");
                }
            }
        }
        throw new SQLException("Failed to create match, no ID returned");
    }

    @Override
    public List<Match> getAllMatches() throws SQLException {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT match_id, match_date, game_id FROM matchs ORDER BY match_date ASC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Long matchId = rs.getLong("match_id");
                Date matchDate = rs.getTimestamp("match_date");
                Long gameId = rs.getLong("game_id");
                
                Match match = new Match(matchId, matchDate, gameId);
                
                // Charger les équipes pour ce match
                match.setTeams(getTeamsForMatch(matchId));
                
                // Charger les arbitres pour ce match
                match.setReferees(getRefereesForMatch(matchId));
                
                matches.add(match);
            }
        }
        
        return matches;
    }

    @Override
    public List<Team> getTeamsForMatch(Long matchId) throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.team_id, t.name, t.tag, t.avatar, t.captain_email " +
                     "FROM teams t " +
                     "INNER JOIN match_teams mt ON t.team_id = mt.team_id " +
                     "WHERE mt.match_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, matchId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Team team = new Team();
                    team.setTeamId(rs.getLong("team_id"));
                    team.setName(rs.getString("name"));
                    team.setTag(rs.getString("tag"));
                    team.setAvatar(rs.getString("avatar"));
                    team.setCaptainEmail(rs.getString("captain_email"));
                    teams.add(team);
                }
            }
        }
        
        return teams;
    }

    public List<User> getRefereesForMatch(Long matchId) throws SQLException {
        List<User> referees = new ArrayList<>();
        String sql = "SELECT u.email, u.username, u.password, u.avatar " +
                     "FROM users u " +
                     "INNER JOIN match_referees mr ON u.email = mr.referee_email " +
                     "WHERE mr.match_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, matchId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User referee = new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar")
                    );
                    referees.add(referee);
                }
            }
        }
        
        return referees;
    }

    @Override
    public List<User> searchUsersByEmail(String searchTerm) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, username, password, avatar FROM users " +
                     "WHERE LOWER(email) LIKE LOWER(?) ORDER BY email LIMIT 10";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar")
                    ));
                }
            }
        }
        
        return users;
    }

    @Override
    public boolean delete(Long matchId) throws SQLException {
        String query = "DELETE FROM matchs WHERE match_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, matchId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Match " + matchId + " supprimé avec succès");
                return true;
            } else {
                System.out.println("⚠️  Aucun match trouvé avec l'ID " + matchId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression du match " + matchId);
            e.printStackTrace();
            throw e;
        }
    }

        @Override
    public Match createCompleteMatch(Team team1, Team team2, java.time.LocalDate matchDate, int gameId, User referee) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtMatch = null;
        PreparedStatement stmtTeam1 = null;
        PreparedStatement stmtTeam2 = null;
        PreparedStatement stmtReferee = null;
        ResultSet rs = null;
        
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // ✅ Début de la transaction
            
            java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(matchDate.atStartOfDay());
            
            String sqlMatch = "INSERT INTO matchs (match_date, game_id) VALUES (?, ?) RETURNING match_id";
            stmtMatch = conn.prepareStatement(sqlMatch);
            stmtMatch.setTimestamp(1, sqlTimestamp);
            stmtMatch.setInt(2, gameId);
            
            rs = stmtMatch.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Échec de la création du match, aucun ID retourné.");
            }
            
            long matchId = rs.getLong("match_id");
            System.out.println("✅ Match créé avec ID: " + matchId);
            
            String sqlTeam = "INSERT INTO match_teams (match_id, team_id) VALUES (?, ?)";
            stmtTeam1 = conn.prepareStatement(sqlTeam);
            stmtTeam1.setLong(1, matchId);
            stmtTeam1.setLong(2, team1.getTeamId());
            stmtTeam1.executeUpdate();
            System.out.println("✅ Équipe 1 ajoutée: " + team1.getName());
            
            stmtTeam2 = conn.prepareStatement(sqlTeam);
            stmtTeam2.setLong(1, matchId);
            stmtTeam2.setLong(2, team2.getTeamId());
            stmtTeam2.executeUpdate();
            System.out.println("✅ Équipe 2 ajoutée: " + team2.getName());
            
            String sqlReferee = "INSERT INTO match_referees (match_id, referee_email) VALUES (?, ?)";
            stmtReferee = conn.prepareStatement(sqlReferee);
            stmtReferee.setLong(1, matchId);
            stmtReferee.setString(2, referee.getEmail());
            stmtReferee.executeUpdate();
            System.out.println("✅ Arbitre ajouté: " + referee.getEmail());
            
            conn.commit(); // ✅ Valider la transaction
            System.out.println("✅ Transaction validée avec succès");
            
            Match match = new Match(
                matchId,
                new java.util.Date(sqlTimestamp.getTime()),
                (long) gameId
            );
            
            match.getTeams().add(team1);
            match.getTeams().add(team2);
            
            match.addReferee(referee);
            
            return match;
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); 
                    System.err.println("❌ Transaction annulée suite à une erreur");
                } catch (SQLException ex) {
                    System.err.println("❌ Erreur lors du rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            // Fermer toutes les ressources
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignore */ }
            if (stmtMatch != null) try { stmtMatch.close(); } catch (SQLException e) { /* ignore */ }
            if (stmtTeam1 != null) try { stmtTeam1.close(); } catch (SQLException e) { /* ignore */ }
            if (stmtTeam2 != null) try { stmtTeam2.close(); } catch (SQLException e) { /* ignore */ }
            if (stmtReferee != null) try { stmtReferee.close(); } catch (SQLException e) { /* ignore */ }
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Remettre en mode normal
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("❌ Erreur lors de la fermeture de la connexion: " + e.getMessage());
                }
            }
        }
    }


}