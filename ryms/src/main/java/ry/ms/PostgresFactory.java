package ry.ms;

import java.sql.SQLException;

import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.postgres.MatchDAOPostgres;
import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.user.login.postgres.UserDAOPostgres;
/**
 * A concrete factory that creates instances of DAOs.
 * This implementation of {@link AbsFactory} is responsible for instantiating
 * the specific DAO implementations for the application.
 */
public class PostgresFactory extends AbsFactory {
    /**
     * Creates an instance of {@link UserDAOPostgres}.
     * It handles the potential {@link SQLException} during DAO instantiation
     * by wrapping it in a {@link RuntimeException}.
     * @return A new instance of UserDAO.
     */
    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public MatchDAO createMatchDAO(){
        return new MatchDAOPostgres();
    }
}
