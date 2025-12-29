package ry.ms.businessLogic.match;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.postgres.MatchDAOPostgres;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;
import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.user.login.postgres.UserDAOPostgres;

public class MatchFacade {

    private static MatchFacade matchfacade;
    private final MatchManager matchManager;

    private MatchFacade(){
        MatchDAO matchDAO = new MatchDAOPostgres();
        TeamDAO teamDAO = new TeamDAOPostgres();
        UserDAO userDAO = new UserDAOPostgres();
        this.matchManager = MatchManager.getMatchManager(matchDAO, userDAO, teamDAO);
    }

    public static MatchFacade getMatchFacade(){
        if(matchfacade == null){
            matchfacade = new MatchFacade();
        }
        return matchfacade;
    }

    public Long createMatch(Date matchDate, int gameId) throws SQLException {
        return matchManager.createMatch(matchDate, gameId);
    }

    public Match createCompleteMatch(Team team1, Team team2, LocalDate matchDate, int gameId, User referee) throws SQLException {
        return matchManager.createCompleteMatch(team1, team2, matchDate, gameId, referee);
    }

    public List<User> getTeamMembers(Long teamId) throws SQLException, TeamDoesntExistException {
        return matchManager.getTeamMembers(teamId);
    }

    public List<Match> getAllMatches() throws SQLException {
        return matchManager.getAllMatches();
    }

    public boolean addReferee(Long matchid, String email) throws SQLException, MatchDoesntExistException, UserDoesntExistException{
        return matchManager.addReferee(matchid, email);
    }

    public boolean addDate(Long matchid, Date date) throws SQLException, MatchDoesntExistException{
        return matchManager.addDate(matchid, date);
    }

    public boolean addTeam(Long matchid, Long teamid) throws SQLException, TeamDoesntExistException, MatchDoesntExistException{
        return matchManager.addTeam(matchid, teamid);
    }

    public boolean updateRoaster(Long teamId, String currentUserEmail, String newUserEmail) throws SQLException, UserDoesntExistException, TeamDoesntExistException{
        return matchManager.updateRoaster(teamId, currentUserEmail, newUserEmail);
    }

    public Match getMatchById(Long matchId) throws SQLException, MatchDoesntExistException {
        return matchManager.getMatchById(matchId);
    }

    public List<User> searchUsersByEmail(String searchTerm) throws SQLException {
        return matchManager.searchUsersByEmail(searchTerm);
    }

    public List<Team> getTeamsForMatch(Long matchId) throws SQLException {
        return matchManager.getTeamsForMatch(matchId);
    }

    public boolean deleteMatch(Long matchId) throws SQLException, MatchDoesntExistException {
        return matchManager.deleteMatch(matchId);
    }

    public List<User> getAllUsers() throws SQLException {
        return matchManager.getAllUsers();
    }







    
}
