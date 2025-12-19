package ry.ms.persistLogic.user.login.postgres;

import ry.ms.businessLogic.user.login.models.User;
import ry.ms.persistLogic.user.login.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ry.ms.persistLogic.DBConfig;

/**
 * PostgreSQL implementation of the UserDAO.
 * This class handles all database operations related to the {@link User} entity
 * for a PostgreSQL database.
 */
public class UserDAOPostgres extends UserDAO {

    /**
     * Constructs a UserPostgres DAO with the given database connection.
     * @param conn The database connection to be used for queries.
     */
    public UserDAOPostgres() {
        super(initConnection());
    }

    private static Connection initConnection() {
        try {
            return DBConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database", e);
        }
    }

    /**
     * Retrieves a user from the 'users' table by email or username (identifier).
     * @param identifier email or username provided by the user.
     * @return A {@link User} object if a matching user is found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public User getUserById(String identifier) throws SQLException {
        String sql = "SELECT email, username, password, avatar FROM users WHERE email = ? OR username = ?";
        
        // Using try-with-resources to ensure PreparedStatement and ResultSet are closed automatically.
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar")
                    );
                }
            }
        }
        return null;
    }
}