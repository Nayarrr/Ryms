package ry.ms.persistLogic.match.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.match.models.MatchStatus;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.match.dao.MatchDAO;

public class MatchDAOPostgres implements MatchDAO {

    public MatchDAOPostgres() {
    }

    @Override
    public Match getMatchById(Long matchId) throws SQLException {
        String query = "SELECT match_id, match_date, game_id, status FROM matchs WHERE match_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, matchId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("match_id");
                Date date = rs.getTimestamp("match_date");
                Long gameId = rs.getLong("game_id");
                String statusStr = rs.getString("status");

                Match match = new Match(id, date, gameId,
                        ry.ms.businessLogic.match.models.MatchStatus.fromString(statusStr));

                // Charger les équipes
                match.setTeams(getTeamsForMatch(id));

                // Charger les arbitres
                match.setReferees(getRefereesForMatch(id));

                return match;
            }
        }
        return null;
    }

    @Override
    public Team getTeamById(Long teamid) throws SQLException {
        String sql = "SELECT team_id, name, tag, avatar, captain_email, created_at FROM teams WHERE team_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, teamid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
    public boolean addReferee(Match match, User referee) throws SQLException {
        if (match == null || match.getMatchId() == null || referee == null || referee.getEmail() == null) {
            throw new IllegalArgumentException("Match ID and referee email must be provided");
        }

        String checkSql = "SELECT COUNT(*) FROM match_referees WHERE match_id = ? AND referee_email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setLong(1, match.getMatchId());
            checkStmt.setString(2, referee.getEmail());

            try (var rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println(
                            "⚠️ L'arbitre " + referee.getEmail() + " est déjà assigné au match " + match.getMatchId());
                    return false;
                }
            }
        }

        String insertSql = "INSERT INTO match_referees (match_id, referee_email) VALUES (?, ?)";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setLong(1, match.getMatchId());
            stmt.setString(2, referee.getEmail());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                System.out.println("✅ Arbitre " + referee.getEmail() + " ajouté au match " + match.getMatchId());
            }

            return success;
        }
    }

    @Override
    public boolean addDate(Match match, Date date) throws SQLException {
        if (match == null || match.getMatchId() == null || date == null) {
            throw new IllegalArgumentException("Match ID and date must be provided");
        }

        String sql = "UPDATE matchs SET match_date = ? WHERE match_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, new Timestamp(date.getTime())); // Convertir java.util.Date en java.sql.Timestamp pour
                                                                 // PostgreSQL
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

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String checkSql = "SELECT 1 FROM team_members WHERE team_id = ? AND user_email = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setLong(1, teamId);
                    checkStmt.setString(2, currentUserEmail);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            return false; // L'utilisateur actuel n'est pas dans l'équipe
                        }
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
                        conn.rollback();
                        return false;
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    @Override
    public List<User> getTeamMembers(Long teamId) throws SQLException {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.email, u.username, u.password, u.avatar " +
                "FROM users u " +
                "INNER JOIN team_members tm ON u.email = tm.user_email " +
                "WHERE tm.team_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new User(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBytes("avatar"),
                            rs.getString("role")));
                }
            }
        }
        return members;
    }

    @Override
    public Long createMatch(Date matchDate, int tournamentId) throws SQLException {
        String sql = "INSERT INTO matchs (match_date, tournament_id) VALUES (?, ?) RETURNING match_id";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, new Timestamp(matchDate.getTime()));
            stmt.setInt(2, tournamentId);

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
        String sql = "SELECT match_id, match_date, game_id, status FROM matchs ORDER BY match_date ASC";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Long id = rs.getLong("match_id");
                Date date = rs.getTimestamp("match_date");
                Long gameId = rs.getLong("game_id");
                String statusStr = rs.getString("status");

                Match match = new Match(id, date, gameId,
                        ry.ms.businessLogic.match.models.MatchStatus.fromString(statusStr));

                // Charger les équipes et arbitres
                match.setTeams(getTeamsForMatch(id));
                match.setReferees(getRefereesForMatch(id));

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

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
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

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, matchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User referee = new User(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBytes("avatar"),
                            rs.getString("role"));
                    referees.add(referee);
                }
            }
        }

        return referees;
    }

    @Override
    public List<User> searchUsersByEmail(String searchTerm) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, username, password, avatar, role FROM users " +
                "WHERE LOWER(email) LIKE LOWER(?) ORDER BY email LIMIT 10";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBytes("avatar"),
                            rs.getString("role")));
                }
            }
        }

        return users;
    }

    @Override
    public boolean delete(Long matchId) throws SQLException {
        String query = "DELETE FROM matchs WHERE match_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, matchId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Match " + matchId + " supprimé avec succès");
                return true;
            } else {
                System.out.println("⚠️  Aucun match trouvé avec l'ID " + matchId);
                return false;
            }
        }
    }

    @Override

    public Match createCompleteMatch(Team team1, Team team2, LocalDate matchDate, int gameId, int tournamentId,
            User referee)
            throws SQLException {
        try (Connection conn = DBConfig.getConnection()) {
            conn.setAutoCommit(false);

            Timestamp sqlTimestamp = Timestamp.valueOf(matchDate.atStartOfDay());

            String sqlMatch = "INSERT INTO matchs (match_date, game_id, tournament_id) VALUES (?, ?, ?) RETURNING match_id";
            long matchId;
            try (PreparedStatement stmtMatch = conn.prepareStatement(sqlMatch)) {
                stmtMatch.setTimestamp(1, sqlTimestamp);
                stmtMatch.setInt(2, gameId);
                stmtMatch.setInt(3, tournamentId);

                try (ResultSet rs = stmtMatch.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Échec de la création du match, aucun ID retourné.");
                    }
                    matchId = rs.getLong("match_id");
                }
            }

            System.out.println("✅ Match créé avec ID: " + matchId);

            // Ajouter équipes
            String sqlTeam = "INSERT INTO match_teams (match_id, team_id) VALUES (?, ?)";
            try (PreparedStatement stmtTeam1 = conn.prepareStatement(sqlTeam)) {
                stmtTeam1.setLong(1, matchId);
                stmtTeam1.setLong(2, team1.getTeamId());
                stmtTeam1.executeUpdate();
            }

            try (PreparedStatement stmtTeam2 = conn.prepareStatement(sqlTeam)) {
                stmtTeam2.setLong(1, matchId);
                stmtTeam2.setLong(2, team2.getTeamId());
                stmtTeam2.executeUpdate();
            }

            // Ajouter arbitre
            String sqlReferee = "INSERT INTO match_referees (match_id, referee_email) VALUES (?, ?)";
            try (PreparedStatement stmtReferee = conn.prepareStatement(sqlReferee)) {
                stmtReferee.setLong(1, matchId);
                stmtReferee.setString(2, referee.getEmail());
                stmtReferee.executeUpdate();
            }

            conn.commit();

            Match match = new Match(
                    matchId,
                    new Date(sqlTimestamp.getTime()),
                    (long) gameId,
                    MatchStatus.SCHEDULED);

            match.getTeams().add(team1);
            match.getTeams().add(team2);
            match.addReferee(referee);

            return match;

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public boolean updateMatchStatus(Long matchId, ry.ms.businessLogic.match.models.MatchStatus status)
            throws SQLException {
        String sql = "UPDATE matchs SET status = ? WHERE match_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setLong(2, matchId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Statut du match #" + matchId + " mis à jour : " + status.name());
                return true;
            }

            return false;
        }
    }

    @Override
    public List<Match> getMatchesByTournament(int tournamentId) throws SQLException {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT m.match_id, m.match_date, m.status, m.game_id " +
                "FROM matchs m " +
                "WHERE m.tournament_id = ? " +
                "ORDER BY m.match_date";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tournamentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Long matchId = rs.getLong("match_id");
                    Date matchDate = rs.getDate("match_date");
                    String statusStr = rs.getString("status");
                    Long gameId = rs.getLong("game_id");

                    MatchStatus status = MatchStatus.valueOf(statusStr);

                    // Créer le match avec le constructeur existant
                    Match match = new Match(matchId, matchDate, gameId, status);

                    // Récupérer et ajouter les équipes du match
                    List<Team> teams = getTeamsForMatch(matchId);
                    match.setTeams(teams);

                    // Récupérer et ajouter les arbitres (utiliser une requête simple)
                    List<User> referees = getRefereesForMatchSimple(matchId);
                    match.setReferees(referees);

                    matches.add(match);
                }
            }
        }

        return matches;
    }

    private List<User> getRefereesForMatchSimple(Long matchId) throws SQLException {
        List<User> referees = new ArrayList<>();
        String sql = "SELECT u.email, u.username, u.password, u.avatar, u.role " +
                "FROM users u " +
                "JOIN match_referees mr ON u.email = mr.referee_email " +
                "WHERE mr.match_id = ?";

        try (Connection conn = DBConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, matchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User referee = new User(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBytes("avatar"),
                            rs.getString("role"));
                    referees.add(referee);
                }
            }
        }

        return referees;
    }
}