package ry.ms.persistLogic.match.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.user.models.User;

/**
 * Interface DAO pour la gestion des matchs
 * Définit les opérations de persistance liées aux matchs, équipes et arbitres
 */
public interface MatchDAO {

    /**
     * Récupère un match par son identifiant
     * @param matchid L'identifiant du match
     * @return Le match trouvé ou null si inexistant
     * @throws SQLException En cas d'erreur de base de données
     */
    Match getMatchById(Long matchid) throws SQLException;

    /**
     * Récupère une équipe par son identifiant
     * @param teamid L'identifiant de l'équipe
     * @return L'équipe trouvée ou null si inexistante
     * @throws SQLException En cas d'erreur de base de données
     */
    Team getTeamById(Long teamid) throws SQLException;

    /**
     * Ajoute un arbitre à un match
     * @param match Le match concerné
     * @param referee L'arbitre à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean addReferee(Match match, User referee) throws SQLException;

    /**
     * Définit ou met à jour la date d'un match
     * @param match Le match concerné
     * @param date La nouvelle date du match
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean addDate(Match match, Date date) throws SQLException;

    /**
     * Ajoute une équipe à un match
     * @param match Le match concerné
     * @param team L'équipe à ajouter
     * @return true si l'ajout a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean addTeam(Match match, Team team) throws SQLException;

    /**
     * Met à jour le roster d'une équipe en remplaçant un joueur par un autre
     * @param teamId L'identifiant de l'équipe
     * @param currentUser Le joueur actuel à remplacer
     * @param newUser Le nouveau joueur
     * @return true si le remplacement a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean updateRoaster(Long teamId, User currentUser, User newUser) throws SQLException;

    /**
     * Récupère la liste des membres d'une équipe
     * @param teamId L'identifiant de l'équipe
     * @return La liste des utilisateurs membres de l'équipe
     * @throws SQLException En cas d'erreur de base de données
     */
    List<User> getTeamMembers(Long teamId) throws SQLException;

    /**
     * Crée un nouveau match avec une date et un jeu
     * @param matchDate La date du match
     * @param gameId L'identifiant du jeu
     * @return L'identifiant du match créé
     * @throws SQLException En cas d'erreur de base de données
     */
    Long createMatch(Date matchDate, int gameId) throws SQLException;

    /**
     * Récupère la liste de tous les matchs
     * @return La liste complète des matchs avec leurs équipes et arbitres
     * @throws SQLException En cas d'erreur de base de données
     */
    List<Match> getAllMatches() throws SQLException;

    /**
     * Recherche des utilisateurs par leur email
     * @param searchTerm Le terme de recherche (début de l'email)
     * @return La liste des utilisateurs correspondants (maximum 10 résultats)
     * @throws SQLException En cas d'erreur de base de données
     */
    List<User> searchUsersByEmail(String searchTerm) throws SQLException;

    /**
     * Récupère les équipes participant à un match
     * @param matchId L'identifiant du match
     * @return La liste des équipes du match
     * @throws SQLException En cas d'erreur de base de données
     */
    List<Team> getTeamsForMatch(Long matchId) throws SQLException;

    /**
     * Supprime un match de la base de données
     * @param matchId L'identifiant du match à supprimer
     * @return true si la suppression a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean delete(Long matchId) throws SQLException;

    /**
     * Crée un match complet avec toutes ses données (équipes, arbitre, date)
     * Opération transactionnelle regroupant plusieurs insertions
     * @param team1 La première équipe
     * @param team2 La deuxième équipe
     * @param matchDate La date du match
     * @param gameId L'identifiant du jeu
     * @param referee L'arbitre du match
     * @return Le match créé avec toutes ses associations
     * @throws SQLException En cas d'erreur de base de données ou de rollback de transaction
     */
    Match createCompleteMatch(Team team1, Team team2, LocalDate matchDate, int gameId, User referee) throws SQLException;

    /**
     * Met à jour le statut d'un match
     * @param matchId L'identifiant du match
     * @param status Le nouveau statut (SCHEDULED, IN_PROGRESS, FINISHED)
     * @return true si la mise à jour a réussi, false sinon
     * @throws SQLException En cas d'erreur de base de données
     */
    boolean updateMatchStatus(Long matchId, ry.ms.businessLogic.match.models.MatchStatus status) throws SQLException;
    

}