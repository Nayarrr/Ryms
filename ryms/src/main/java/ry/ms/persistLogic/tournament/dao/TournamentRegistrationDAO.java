package ry.ms.persistLogic.tournament.dao;

import ry.ms.businessLogic.tournament.models.TournamentRegistration;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface DAO pour gérer les inscriptions aux tournois
 */
public interface TournamentRegistrationDAO {

    /**
     * Inscrire une équipe à un tournoi
     * 
     * @param registration L'inscription à enregistrer
     * @return L'ID de l'inscription créée
     * @throws SQLException En cas d'erreur SQL
     */
    Long registerTeam(TournamentRegistration registration) throws SQLException;

    /**
     * Récupérer toutes les inscriptions pour un tournoi donné
     * 
     * @param tournamentId L'ID du tournoi
     * @return Liste des inscriptions
     * @throws SQLException En cas d'erreur SQL
     */
    List<TournamentRegistration> getRegistrationsByTournament(Long tournamentId) throws SQLException;

    /**
     * Récupérer toutes les inscriptions d'une équipe
     * 
     * @param teamId L'ID de l'équipe
     * @return Liste des inscriptions
     * @throws SQLException En cas d'erreur SQL
     */
    List<TournamentRegistration> getRegistrationsByTeam(Long teamId) throws SQLException;

    /**
     * Annuler une inscription
     * 
     * @param registrationId L'ID de l'inscription à annuler
     * @return true si l'annulation a réussi
     * @throws SQLException En cas d'erreur SQL
     */
    boolean cancelRegistration(Long registrationId) throws SQLException;

    /**
     * Vérifier si une équipe est déjà inscrite à un tournoi
     * 
     * @param tournamentId L'ID du tournoi
     * @param teamId       L'ID de l'équipe
     * @return true si l'équipe est déjà inscrite
     * @throws SQLException En cas d'erreur SQL
     */
    boolean isTeamRegistered(Long tournamentId, Long teamId) throws SQLException;

    /**
     * Confirmer une inscription
     * 
     * @param registrationId L'ID de l'inscription à confirmer
     * @return true si la confirmation a réussi
     * @throws SQLException En cas d'erreur SQL
     */
    boolean confirmRegistration(Long registrationId) throws SQLException;

    /**
     * Obtenir le nombre d'équipes inscrites à un tournoi (hors annulations)
     * 
     * @param tournamentId L'ID du tournoi
     * @return Le nombre d'équipes inscrites
     * @throws SQLException En cas d'erreur SQL
     */
    int getRegisteredTeamsCount(Long tournamentId) throws SQLException;
}
