package ry.ms;

import java.sql.SQLException;

import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.user.login.postgres.UserDAOPostgres;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.team.postgres.InvitationDAOPostgres;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;
/**
 * A concrete factory that creates instances of DAOs.
 * This implementation of {@link AbsFactory} is responsible for instantiating
 * the specific DAO implementations for the application.
 */
public class PostgresFactory extends AbsFactory {

    /**
     * Creates an instance of {@link UserPostgres}.
     * It handles the potential {@link SQLException} during DAO instantiation
     * by wrapping it in a {@link RuntimeException}.
     * @return A new instance of UserDAO.
     */
    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public TeamDAO createTeamDAO() {
        return new TeamDAOPostgres();
    }

    @Override
    public InvitationDAO createInvitationDAO() {
        return new InvitationDAOPostgres();
    }
}
