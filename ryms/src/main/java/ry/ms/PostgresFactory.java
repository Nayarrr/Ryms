package ry.ms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import ry.ms.DAO.UserDAO;
import ry.ms.POSTGRES.UserPostgres;

/**
 * A concrete factory that creates instances of DAOs.
 * This implementation of {@link AbsFactory} is responsible for instantiating
 * the specific DAO implementations for the application.
 */
public class PostgresFactory extends AbsFactory {
    // Database connection details. It's recommended to move these to a configuration file.
    private static final String URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String USER = "ryms";
    private static final String PASSWORD = "ryms";

    /**
     * Creates an instance of {@link UserPostgres}.
     * It handles the potential {@link SQLException} during DAO instantiation
     * by wrapping it in a {@link RuntimeException}.
     * @return A new instance of UserDAO.
     */
    @Override
    public UserDAO createUserDAO() {
        try {
            return new UserPostgres(
                DriverManager.getConnection(URL, USER, PASSWORD);
            );
        } catch (SQLException e) {
            // If the DAO cannot be created (e.g., database connection failure),
            // it's a critical error, so we throw a RuntimeException.
            throw new RuntimeException("Failed to create UserDAO", e);
        }
    }
}
