package ry.ms.persistLogic.team.dao;

import java.sql.SQLException;

import ry.ms.businessLogic.team.models.Team;

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
}