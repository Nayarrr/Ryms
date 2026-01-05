package ry.ms.businessLogic.match;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.user.login.dao.UserDAO;

public class MatchManager {

    private static MatchManager matchmanager;
    private final MatchDAO matchDAO;
    private final UserDAO userDAO;
    private final TeamDAO teamDAO;

    private MatchManager(MatchDAO matchDAO, UserDAO userDAO, TeamDAO teamDAO){
        this.matchDAO = matchDAO;
        this.userDAO = userDAO;
        this.teamDAO = teamDAO;
    }

    public static MatchManager getMatchManager(MatchDAO matchDAO,  UserDAO userDAO, TeamDAO teamDAO){
        if(matchmanager == null){
            matchmanager = new MatchManager(matchDAO, userDAO, teamDAO);
        }
        return matchmanager;
    }

    private User getUserById(String email) throws SQLException, UserDoesntExistException{
        User user = userDAO.getUserById(email);
        if(user == null){
            throw new UserDoesntExistException("User does not exist.");
        }
        return user;
    }

    public Match getMatchById(Long matchid) throws SQLException, MatchDoesntExistException{
        Match match = matchDAO.getMatchById(matchid);
        if(match == null){
            throw new MatchDoesntExistException("Match does not exist.");
        }
        return match;
    }

    private Team getTeamById(Long teamid) throws SQLException, TeamDoesntExistException{
        Team team = matchDAO.getTeamById(teamid);
        if(team == null){
            throw new TeamDoesntExistException("Team does not exist.");
        }
        return team;
    }

    public List<User> getTeamMembers(Long teamId) throws SQLException{
        return matchDAO.getTeamMembers(teamId);
    }

    public List<Match> getAllMatches() throws SQLException {
        return matchDAO.getAllMatches();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public List<Team> getAllTeams() throws SQLException {
        return teamDAO.getAllTeams();
    }

    public List<User> searchUsersByEmail(String searchTerm) throws SQLException {
        return matchDAO.searchUsersByEmail(searchTerm);
    }

    public Long createMatch(Date matchDate, int gameId) throws SQLException {
        if (matchDate == null) {
            throw new IllegalArgumentException("Match date cannot be null");
        }
        
        return matchDAO.createMatch(matchDate, gameId);
    }

    public Match createCompleteMatch(Team team1, Team team2, java.time.LocalDate matchDate, int gameId, User referee) throws SQLException {
        if (team1 == null || team2 == null) {
            throw new IllegalArgumentException("Les deux équipes doivent être spécifiées");
        }

        if (team1.getTeamId().equals(team2.getTeamId())) {
            throw new IllegalArgumentException("Les deux équipes doivent être différentes");
        }

        if (matchDate == null || matchDate.isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La date du match doit être dans le futur ou aujourd'hui");
        }

        if (referee == null) {
            throw new IllegalArgumentException("Un arbitre doit être spécifié");
        }

        return matchDAO.createCompleteMatch(team1, team2, matchDate, gameId, referee);
    }

    public boolean addReferee(Long matchid , String email) throws SQLException, UserDoesntExistException, MatchDoesntExistException{
        User referee = getUserById(email);
        Match match = getMatchById(matchid);

        System.out.println("🔍 Match: " + match.getMatchId());
        System.out.println("🔍 Arbitre: " + referee.getEmail());
        
        boolean result = matchDAO.addReferee(match, referee);
        
        System.out.println("🔍 Résultat addReferee: " + result);
        
        return result;
    }

    public boolean addDate(Long matchid, Date date) throws SQLException, MatchDoesntExistException{
        Match match = getMatchById(matchid);
        if(match == null){
            throw new MatchDoesntExistException("Match does not exist.");
        }

        return matchDAO.addDate(match, date);
    }

    public boolean addTeam(Long matchid, Long teamid) throws SQLException, TeamDoesntExistException, MatchDoesntExistException{
        Match match = getMatchById(matchid);
        Team team = getTeamById(teamid);

        if(team == null){
            throw new TeamDoesntExistException("Team does not exist");
        }

        if(match == null){
            throw new MatchDoesntExistException("Match does not exist.");
        }

        return matchDAO.addTeam(match, team);
    }

    public boolean updateRoaster(Long teamId, String currentUserEmail, String newUserEmail) throws SQLException, UserDoesntExistException {
        User currentUser = getUserById(currentUserEmail);
        User newUser = getUserById(newUserEmail);

        if(currentUser == null){
            throw new UserDoesntExistException("CurrentUser does not exist");
        }

        if(newUser == null){
            throw new UserDoesntExistException("NewUser does not exist");
        }

        return matchDAO.updateRoaster(teamId, currentUser, newUser);
    }

    public List<Team> getTeamsForMatch(Long matchId) throws SQLException {
        return matchDAO.getTeamsForMatch(matchId);
    }

    public boolean deleteMatch(Long matchId) throws SQLException, MatchDoesntExistException {
        Match match = matchDAO.getMatchById(matchId);
        if (match == null) {
            throw new MatchDoesntExistException("Le match avec l'ID " + matchId + " n'existe pas.");
        }
        return matchDAO.delete(matchId);
    }


}
