package ry.ms.persistLogic.match.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ry.ms.models.MatchResult;
import ry.ms.models.Team;
import ry.ms.models.TeamResult;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.match.dao.MatchResultDAO;

public class MatchResultDAOPostgres implements MatchResultDAO {

    @Override
    public boolean saveMatchResult(Long matchId, Long teamId, int score, String result) throws SQLException {
        String sql = """
            INSERT INTO match_results (match_id, team_id, score, result)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (match_id, team_id) 
            DO UPDATE SET score = EXCLUDED.score, result = EXCLUDED.result
        """;
        
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, matchId);
            stmt.setLong(2, teamId);
            stmt.setInt(3, score);
            stmt.setString(4, result);
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<TeamResult> getMatchResults(Long matchId) throws SQLException {
        String sql = """
            SELECT mr.team_id, mr.score, mr.result, 
                   t.name, t.tag, t.avatar, t.captain_email
            FROM match_results mr
            JOIN teams t ON mr.team_id = t.team_id
            WHERE mr.match_id = ?
        """;
        
        List<TeamResult> results = new ArrayList<>();
        
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, matchId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getLong("team_id"));
                team.setName(rs.getString("name"));
                team.setTag(rs.getString("tag"));
                team.setAvatar(rs.getString("avatar"));
                team.setCaptainEmail(rs.getString("captain_email"));
                
                TeamResult teamResult = new TeamResult(team);
                teamResult.setScore(rs.getInt("score"));
                
                String resultStr = rs.getString("result");
                if (resultStr != null) {
                    teamResult.setResult(MatchResult.valueOf(resultStr));
                }
                
                results.add(teamResult);
            }
        }
        
        return results;
    }

    @Override
    public boolean updateScore(Long matchId, Long teamId, int score) throws SQLException {
        String sql = """
            INSERT INTO match_results (match_id, team_id, score)
            VALUES (?, ?, ?)
            ON CONFLICT (match_id, team_id) 
            DO UPDATE SET score = EXCLUDED.score
        """;
        
        try (Connection conn = DBConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, matchId);
            stmt.setLong(2, teamId);
            stmt.setInt(3, score);
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean finalizeMatchResults(Long matchId) throws SQLException {
        String sql = """
            WITH scores AS (
                SELECT 
                    team_id, 
                    score,
                    MAX(score) OVER () as max_score
                FROM match_results
                WHERE match_id = ?
            ),
            unique_scores AS (
                SELECT COUNT(*) as distinct_count
                FROM (SELECT DISTINCT score FROM match_results WHERE match_id = ?) sub
            )
            UPDATE match_results mr
            SET result = CASE
                WHEN us.distinct_count = 1 THEN 'DRAW'
                WHEN s.score = s.max_score THEN 'WIN'
                ELSE 'LOSS'
            END
            FROM scores s, unique_scores us
            WHERE mr.match_id = ? AND mr.team_id = s.team_id
        """;
        
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, matchId);
            stmt.setLong(2, matchId);
            stmt.setLong(3, matchId);
            
            int updated = stmt.executeUpdate();
            
            if (updated > 0) {
                System.out.println("✅ Match " + matchId + " finalisé avec succès (" + updated + " résultats mis à jour)");
                return true;
            } else {
                System.err.println("⚠️ Aucun résultat trouvé pour le match " + matchId);
                return false;
            }
        }
    }
}