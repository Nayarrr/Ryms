package ry.ms.persistLogic.match.dao;

import java.sql.SQLException;
import java.util.List;

import ry.ms.models.TeamResult;

/**
 * Interface DAO pour la gestion des résultats de matchs
 * Définit les opérations de persistance liées aux scores et résultats finaux des équipes
 */
public interface MatchResultDAO {
    
    /**
     * Enregistre ou met à jour le résultat d'une équipe pour un match
     * @param matchId L'identifiant du match
     * @param teamId L'identifiant de l'équipe
     * @param score Le score obtenu par l'équipe
     * @param result Le résultat final : "WIN", "LOSS", "DRAW", ou null si non finalisé
     * @return true si l'opération a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean saveMatchResult(Long matchId, Long teamId, int score, String result) throws SQLException;
    
    /**
     * Récupère tous les résultats d'un match
     * @param matchId L'identifiant du match
     * @return La liste des résultats de chaque équipe, vide si aucun résultat
     * @throws SQLException En cas d'erreur de base de données
     */
    List<TeamResult> getMatchResults(Long matchId) throws SQLException;
    
    /**
     * Met à jour uniquement le score d'une équipe
     * @param matchId L'identifiant du match
     * @param teamId L'identifiant de l'équipe
     * @param score Le nouveau score de l'équipe
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean updateScore(Long matchId, Long teamId, int score) throws SQLException;
    
    /**
     * Finalise le match en calculant automatiquement les résultats Win/Loss/Draw
     * @param matchId L'identifiant du match à finaliser
     * @return true si la finalisation a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean finalizeMatchResults(Long matchId) throws SQLException;
}