package ry.ms.persistLogic.login.postgres;

import ry.ms.businessLogic.login.models.User;
import ry.ms.persistLogic.login.dao.UserDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL implementation of the UserDAO.
 * This class handles all database operations related to the {@link User} entity
 * for a PostgreSQL database.
 */
public class UserPostgres extends UserDAO {

    /**
     * Constructs a UserPostgres DAO with the given database connection.
     * @param conn The database connection to be used for queries.
     */
    public UserPostgres(Connection conn) {
        super(conn);
    }

    /**
     * Retrieves a user from the 'users' table by their email address.
     * @param email The email of the user to find.
     * @return A {@link User} object if a matching user is found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    @Override
    public User getUserById(String email) throws SQLException {
        String sql = "SELECT email, username, password, avatar FROM users WHERE email = ?";
        
        // Using try-with-resources to ensure PreparedStatement and ResultSet are closed automatically.
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            
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