package ry.ms.businessLogic.match;

import java.sql.SQLException;
import java.util.Date;

import ry.ms.businessLogic.match.exceptions.MatchDoesntExistException;
import ry.ms.businessLogic.match.exceptions.TeamDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.models.User;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.postgres.MatchDAOPostgres;

public class MatchFacade {

    private static MatchFacade matchfacade;
    private final MatchManager matchManager;

    private MatchFacade(){
        MatchDAO matchDAO = new MatchDAOPostgres();
        this.matchManager = MatchManager.getMatchManager(matchDAO);
    }

    public static MatchFacade getMatchFacade(){
        if(matchfacade == null){
            matchfacade = new MatchFacade();
        }
        return matchfacade;
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

    public boolean updateRoaster(User currentUser, User newUser){
        return false;
    }
}
