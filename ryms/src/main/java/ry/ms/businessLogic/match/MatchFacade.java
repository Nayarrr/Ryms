package ry.ms.businessLogic.match;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.User;
import ry.ms.models.match.Match;
import ry.ms.models.match.TeamResult;
import ry.ms.models.team.Team;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.dao.MatchResultDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.user.login.dao.UserDAO;

public class MatchFacade {

    private static MatchFacade matchfacade;
    private final MatchManager matchManager;
    private final MatchDAO matchDAO;
    private final TeamDAO teamDAO;
    private final UserDAO userDAO;
    private final MatchResultDAO matchResultDAO;

    private MatchFacade() {
        AbsFactory factory = AbsFactory.getInstance();
        this.matchDAO = factory.createMatchDAO();
        this.teamDAO = factory.createTeamDAO();
        this.userDAO = factory.createUserDAO();
        this.matchResultDAO = factory.createMatchResultDAO();
        this.matchManager = MatchManager.getMatchManager(matchDAO, userDAO, teamDAO);
    }

    public static MatchFacade getMatchFacade() {
        if (matchfacade == null) {
            matchfacade = new MatchFacade();
        }
        return matchfacade;
    }

    public Long createMatch(Date matchDate, int gameId) throws SQLException {
        return matchManager.createMatch(matchDate, gameId);
    }

    public Long createMatch(Long team1Id, Long team2Id, Date matchDate, int gameId) 
            throws SQLException, TeamDoesntExistException, MatchDoesntExistException {
        
        Long matchId = matchManager.createMatch(matchDate, gameId);
        
        if (matchId == null) {
            throw new SQLException("Échec de création du match");
        }

        matchManager.addTeam(matchId, team1Id);
        matchManager.addTeam(matchId, team2Id);

        return matchId;
    }

    public boolean addReferee(Long matchId, String refereeEmail) 
            throws UserDoesntExistException, MatchDoesntExistException, SQLException {
        return matchManager.addReferee(matchId, refereeEmail);
    }

    public boolean addDate(Long matchId, Date date) 
            throws MatchDoesntExistException, SQLException {
        return matchManager.addDate(matchId, date);
    }

    public boolean addTeam(Long matchId, Long teamId) 
            throws TeamDoesntExistException, MatchDoesntExistException, SQLException {
        return matchManager.addTeam(matchId, teamId);
    }

    public boolean updateRoaster(Long teamId, String currentEmail, String newEmail)
            throws UserDoesntExistException,SQLException {
        return matchManager.updateRoaster(teamId, currentEmail, newEmail);
    }

    public List<User> getTeamMembers(Long teamId) throws SQLException {
        return matchManager.getTeamMembers(teamId);
    }

    public List<Match> getAllMatches() throws SQLException {
        return matchManager.getAllMatches();
    }

    public List<User> getAllUsers() throws SQLException {
        return matchManager.getAllUsers();
    }

    public Match getMatchById(Long matchId) throws SQLException, MatchDoesntExistException {
        return matchManager.getMatchById(matchId);
    }

    public List<User> searchUsersByEmail(String searchTerm) throws SQLException {
        return matchManager.searchUsersByEmail(searchTerm);
    }

    public boolean deleteMatch(Long matchId) throws SQLException, MatchDoesntExistException {
        return matchManager.deleteMatch(matchId);
    }

    public Match createCompleteMatch(Team team1, Team team2, LocalDate matchDate, 
                                     int gameId, User referee) throws SQLException {
        return matchManager.createCompleteMatch(team1, team2, matchDate, gameId, referee);
    }

    public List<Team> getTeamsForMatch(Long matchId) throws SQLException {
        return matchManager.getTeamsForMatch(matchId);
    }

    public boolean updateScore(Long matchId, Long teamId, int score) throws SQLException{
        return matchResultDAO.updateScore(matchId, teamId, score);
    }

    public List<TeamResult> getMatchResults(Long matchId) throws SQLException {
        return matchResultDAO.getMatchResults(matchId);
    }

    public boolean finalizeMatch(Long matchId) throws SQLException {
        return matchResultDAO.finalizeMatchResults(matchId);
    }

    public boolean startMatch(Long matchId) throws SQLException, MatchDoesntExistException {
        return matchManager.startMatch(matchId);
    }
}