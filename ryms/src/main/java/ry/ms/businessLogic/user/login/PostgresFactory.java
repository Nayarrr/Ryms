package ry.ms.businessLogic.user.login;

import java.sql.SQLException;

import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.user.login.postgres.UserPostgres;
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
            return new UserPostgres();
    }
}
