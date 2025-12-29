package ry.ms.persistLogic.match.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
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

    public abstract Long createMatch(Date matchDate, int gameId) throws SQLException;

    public abstract List<Match> getAllMatches() throws SQLException;

    public abstract List<User> searchUsersByEmail(String searchTerm) throws SQLException;

    public abstract List<Team> getTeamsForMatch(Long matchId) throws SQLException;

    public abstract boolean delete(Long matchId) throws SQLException;

    public abstract Match createCompleteMatch(Team team1, Team team2, LocalDate matchDate, int gameId, User referee) throws SQLException;
    

}