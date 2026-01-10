package ry.ms.businessLogic.tournament;

import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentRegistration;
import ry.ms.persistLogic.tournament.dao.TournamentDAO;
import ry.ms.persistLogic.tournament.dao.TournamentRegistrationDAO;
import ry.ms.persistLogic.tournament.postgres.TournamentDAOPostgres;
import ry.ms.persistLogic.tournament.postgres.TournamentRegistrationDAOPostgres;

import java.sql.SQLException;
import java.util.List;

public class TournamentManager {

    private final TournamentDAO tournamentDAO;
    private final TournamentRegistrationDAO registrationDAO;

    public TournamentManager() {
        this.tournamentDAO = new TournamentDAOPostgres();
        this.registrationDAO = new TournamentRegistrationDAOPostgres();
    }

    // --- Tournament Operations ---

    public List<Tournament> getAllTournaments() throws SQLException {
        return tournamentDAO.loadTournamentCatalog();
    }

    public void createTournament(Tournament tournament) throws SQLException {
        tournamentDAO.saveTournament(tournament);
    }

    public void updateTournament(Tournament tournament) throws SQLException {
        tournamentDAO.updateTournament(tournament);
    }

    public void deleteTournament(Tournament tournament) throws SQLException {
        tournamentDAO.deleteTournament(tournament);
    }

    // --- Registration Operations ---

    public Long registerTeam(TournamentRegistration registration) throws SQLException {
        return registrationDAO.registerTeam(registration);
    }

    public List<TournamentRegistration> getRegistrationsByTournament(Long tournamentId) throws SQLException {
        return registrationDAO.getRegistrationsByTournament(tournamentId);
    }

    public List<TournamentRegistration> getRegistrationsByTeam(Long teamId) throws SQLException {
        return registrationDAO.getRegistrationsByTeam(teamId);
    }

    public boolean cancelRegistration(Long registrationId) throws SQLException {
        return registrationDAO.cancelRegistration(registrationId);
    }

    public boolean confirmRegistration(Long registrationId) throws SQLException {
        return registrationDAO.confirmRegistration(registrationId);
    }

    public boolean isTeamRegistered(Long tournamentId, Long teamId) throws SQLException {
        return registrationDAO.isTeamRegistered(tournamentId, teamId);
    }

    public int getRegisteredTeamsCount(Long tournamentId) throws SQLException {
        return registrationDAO.getRegisteredTeamsCount(tournamentId);
    }
}
