package ry.ms.businessLogic.tournament;

import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentRegistration;

import java.sql.SQLException;
import java.util.List;

public class TournamentFacade {

    private static TournamentFacade instance;
    private final TournamentManager tournamentManager;

    private TournamentFacade() {
        this.tournamentManager = new TournamentManager();
    }

    public static synchronized TournamentFacade getInstance() {
        if (instance == null) {
            instance = new TournamentFacade();
        }
        return instance;
    }

    // --- Tournament Operations ---

    public List<Tournament> getAllTournaments() throws SQLException {
        return tournamentManager.getAllTournaments();
    }

    public void createTournament(Tournament tournament) throws SQLException {
        tournamentManager.createTournament(tournament);
    }

    public void updateTournament(Tournament tournament) throws SQLException {
        tournamentManager.updateTournament(tournament);
    }

    public void deleteTournament(Tournament tournament) throws SQLException {
        tournamentManager.deleteTournament(tournament);
    }

    // --- Registration Operations ---

    public Long registerTeam(TournamentRegistration registration) throws SQLException {
        return tournamentManager.registerTeam(registration);
    }

    public List<TournamentRegistration> getRegistrationsByTournament(Long tournamentId) throws SQLException {
        return tournamentManager.getRegistrationsByTournament(tournamentId);
    }

    public List<TournamentRegistration> getRegistrationsByTeam(Long teamId) throws SQLException {
        return tournamentManager.getRegistrationsByTeam(teamId);
    }

    public boolean cancelRegistration(Long registrationId) throws SQLException {
        return tournamentManager.cancelRegistration(registrationId);
    }

    public boolean confirmRegistration(Long registrationId) throws SQLException {
        return tournamentManager.confirmRegistration(registrationId);
    }

    public boolean isTeamRegistered(Long tournamentId, Long teamId) throws SQLException {
        return tournamentManager.isTeamRegistered(tournamentId, teamId);
    }

    public int getRegisteredTeamsCount(Long tournamentId) throws SQLException {
        return tournamentManager.getRegisteredTeamsCount(tournamentId);
    }
}
