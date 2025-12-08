package ry.ms;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A utility class for managing database connections.
 * It provides a centralized way to get a connection to the PostgreSQL database.
 * This class cannot be instantiated.
 */
public class DatabaseManager {

    // Database connection details. It's recommended to move these to a configuration file.
    private static final String URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String USER = "ryms";
    private static final String PASSWORD = "ryms";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseManager() {}

    /**
     * Establishes and returns a connection to the database.
     * @return A {@link Connection} object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}