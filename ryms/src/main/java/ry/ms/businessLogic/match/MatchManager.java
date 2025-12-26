package ry.ms.businessLogic.match;

import java.sql.SQLException;
import java.util.Date;

import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;
import ry.ms.persistLogic.match.dao.MatchDAO;

public class MatchManager {

    private static MatchManager matchmanager;
    private final MatchDAO matchDAO;

    private MatchManager(MatchDAO matchDAO){
        this.matchDAO = matchDAO;
    }

    public static MatchManager getMatchManager(MatchDAO matchDAO){
        if(matchmanager == null){
            matchmanager = new MatchManager(matchDAO);
        }
        return matchmanager;
    }

    private User getUserById(String email) throws SQLException, UserDoesntExistException{
        try {
            User user = matchDAO.getUserById(email);
            if(user == null){
                throw new UserDoesntExistException("User does not exist.");
            }
            return user;
        } catch (UserDoesntExistException e) {
            throw new UserDoesntExistException("User does not exist.");
        }
    }

    private Match getMatchById(Long matchid) throws SQLException, MatchDoesntExistException{
        try {
            Match match = matchDAO.getMatchById(matchid);
            if(match == null){
                throw new MatchDoesntExistException("Match does not exist.");
            }
            return match;
        } catch (MatchDoesntExistException e) {
            throw new MatchDoesntExistException("Match does not exist.");
        }
    }

    private Team getTeamById(Long teamid) throws SQLException, TeamDoesntExistException{
        try{
            Team team = matchDAO.getTeamById(teamid);
            if(team == null){
                throw new TeamDoesntExistException("Team does not exist.");
            }
            return team;
        } catch (TeamDoesntExistException t){
            throw new TeamDoesntExistException("Team does not exist.");
        }
    }


    public boolean addReferee(Long matchid , String email) throws SQLException, UserDoesntExistException, MatchDoesntExistException{
        try{
            User referee = getUserById(email);
            Match match = getMatchById(matchid);

            if(referee == null){
                throw new UserDoesntExistException("User does not exist.");
            }
            if(match == null){
                throw new MatchDoesntExistException("Match does not exist.");
            }
            
            return matchDAO.addReferee(match, referee);
        }
        catch (UserDoesntExistException u){
            throw new UserDoesntExistException("User does not exist.");
        } 
        catch(MatchDoesntExistException m){
            throw new MatchDoesntExistException("Match does not exist.");
        }
    }

    public boolean addDate(Long matchid, Date date) throws SQLException, MatchDoesntExistException{
        try {
            Match match = getMatchById(matchid);
            if(match == null){
                throw new MatchDoesntExistException("Match does not exist.");
            }

            return matchDAO.addDate(match, date);
        } catch (MatchDoesntExistException m) {
            throw new MatchDoesntExistException("Match does not exist.");
        }
    }

    public boolean addTeam(Long matchid, Long teamid) throws SQLException, TeamDoesntExistException, MatchDoesntExistException{
        try {
            Match match = getMatchById(matchid);
            Team team = getTeamById(teamid);

            if(team == null){
                throw new TeamDoesntExistException("Team does not exist");
            }

            if(match == null){
                throw new MatchDoesntExistException("Match does not exist.");
            }

            return matchDAO.addTeam(match, team);
        } catch (TeamDoesntExistException t) {
            throw new TeamDoesntExistException("Team does not exist");
        } catch (MatchDoesntExistException m){
            throw new MatchDoesntExistException("Match does not exist.");
        }
    }

    public boolean updateRoaster(Long teamId, String currentUserEmail, String newUserEmail) throws SQLException, UserDoesntExistException, TeamDoesntExistException {
        try{
            User currentUser = getUserById(currentUserEmail);
            User newUser = getUserById(newUserEmail);

            if(currentUser == null){
                throw new UserDoesntExistException("CurrentUser does not exist");
            }

            if(newUser == null){
                throw new UserDoesntExistException("NewUser does not exist");
            }

            return matchDAO.updateRoaster(teamId, currentUser, newUser);
        } catch(UserDoesntExistException u){
            throw new UserDoesntExistException("User does not exist");
        }
    }
}
