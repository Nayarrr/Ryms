package ry.ms.persistLogic.user.login.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.user.login.dao.UserDAO;

/**
 * PostgreSQL implementation of the UserDAO.
 * This class handles all database operations related to the {@link User} entity
 * for a PostgreSQL database.
 */
public class UserDAOPostgres extends UserDAO {

    public static Connection getConnection() throws SQLException {
        return DBConfig.getConnection();
    }

    /**
     * Constructs a UserPostgres DAO with the given database connection.
     * 
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
     * 
     * @param identifier email or username provided by the user.
     * @return A {@link User} object if a matching user is found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public User getUserById(String identifier) throws SQLException {
        String sql = "SELECT email, username, password, avatar, role FROM users WHERE email = ? OR username = ?";

        // Using try-with-resources to ensure PreparedStatement and ResultSet are closed
        // automatically.
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBytes("avatar"),
                            rs.getString("role"));
                }
            }
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, username, password, avatar, role FROM users ORDER BY username";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getBytes("avatar"),
                        rs.getString("role"));
                users.add(user);
            }
        }

        return users;
    }
}