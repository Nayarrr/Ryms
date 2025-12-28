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
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/ryms_database";
    private static final String DEFAULT_USER = "ryms";
    private static final String DEFAULT_PASSWORD = "ryms";

    private DBConfig() {
        // Utility class
    }

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
