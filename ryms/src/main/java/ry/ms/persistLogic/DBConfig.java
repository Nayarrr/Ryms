package ry.ms.persistLogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralizes database configuration and connection creation.
 */
public final class DBConfig {

    private static final String URL_ENV = "RYMS_DB_URL";
    private static final String USER_ENV = "RYMS_DB_USER";
    private static final String PASSWORD_ENV = "RYMS_DB_PASSWORD";

    // Fallback values for development/testing only
    private static final String DEFAULT_URL = "jdbc:postgresql://db.qbjdhxggrueklrjdgsfo.supabase.co:5432/postgres";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASSWORD = "Nm7TurbI8961Ki";

    private DBConfig() {
        // Utility class
    }

    /**
     * Establishes and returns a connection to the database.
     * Uses environment variables for configuration, falling back to default values
     * if not set.
     * 
     * @return A {@link Connection} to the configured database.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        String url = getEnvOrDefault(URL_ENV, DEFAULT_URL);
        String user = getEnvOrDefault(USER_ENV, DEFAULT_USER);
        String password = getEnvOrDefault(PASSWORD_ENV, DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }

    private static String getEnvOrDefault(String envVarName, String defaultValue) {
        String value = System.getenv(envVarName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
}
