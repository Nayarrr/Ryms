package ry.ms.persistLogic.team.dao;

import java.sql.SQLException;
import java.util.List;

import ry.ms.models.team.Team;

/**
 * Data Access Object for team entities.
 */
public interface TeamDAO {

    /**
     * Persists a team, inserting into teams and team_members tables atomically.
     */
    Team saveTeam(Team team) throws SQLException;

    /**
     * Retrieves a team by its unique name.
     */
    Team getTeamByName(String name) throws SQLException;

    Team getTeamById(Long id) throws SQLException;

    Team getTeamByMemberEmail(String userEmail) throws SQLException;

    void addMember(Long teamId, String userEmail) throws SQLException;

    void removeMember(Long teamId, String userEmail) throws SQLException;

    boolean isMember(Long teamId, String userEmail) throws SQLException;

    void updateCaptain(Long teamId, String newCaptainEmail) throws SQLException;

    void deleteTeam(Long teamId) throws SQLException;

    List<Team> getAllTeams() throws SQLException;

    List<Team> searchTeamsByName(String searchTerm) throws SQLException;
}