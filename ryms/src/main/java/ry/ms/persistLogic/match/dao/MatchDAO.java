package ry.ms.persistLogic.match.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import ry.ms.models.Match;
import ry.ms.models.Team;
import ry.ms.models.User;

public abstract class MatchDAO {

    protected final Connection conn;

    public MatchDAO(Connection conn){
        this.conn = conn;
    }

    public abstract User getUserById(String email) throws SQLException;

    public abstract Match getMatchById(Long matchid) throws SQLException;

    public abstract Team getTeamById(Long teamid) throws SQLException;

    public abstract boolean addReferee(Match match, User referee) throws SQLException;

    public abstract boolean addDate(Match match, Date date) throws SQLException;

    public abstract boolean addTeam(Match match, Team team) throws SQLException;

    public abstract boolean updateRoaster(Long teamId, User currentUser, User newUser) throws SQLException;

    public abstract List<User> getTeamMembers(Long teamId) throws SQLException;
}