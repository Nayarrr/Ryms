package ry.ms.persistLogic.match.dao;

import java.sql.SQLException;
import java.util.List;

import ry.ms.models.TeamResult;

public interface MatchResultDAO {
    
    /**
     * Enregistre ou met à jour le résultat d'une équipe pour un match
     */
    boolean saveMatchResult(Long matchId, Long teamId, int score, String result) throws SQLException;
    
    /**
     * Récupère tous les résultats d'un match
     */
    List<TeamResult> getMatchResults(Long matchId) throws SQLException;
    
    /**
     * Met à jour uniquement le score
     */
    boolean updateScore(Long matchId, Long teamId, int score) throws SQLException;
    
    /**
     * Finalise le match en calculant automatiquement les résultats Win/LOSS/DRAW
     */
    boolean finalizeMatchResults(Long matchId) throws SQLException;
}