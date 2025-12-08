package ry.ms.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import ry.ms.MODELS.User;

/**
 * Abstract Data Access Object (DAO) for User entities.
 * It defines the standard operations to be performed on a User model,
 * and manages the database connection for its subclasses.
 */
public abstract class UserDAO {
    
    /** The database connection instance, shared by all methods in a DAO implementation. */
    protected final Connection conn;

    /**
     * Constructs a UserDAO with a database connection.
     * @param conn The active database connection.
     */
    public UserDAO(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Retrieves a user from the data source by their email address.
     * @param email The email of the user to retrieve.
     * @return A {@link User} object if found, otherwise null.
     * @throws SQLException if a database access error occurs.
     */
    public abstract User getUserById(String email) throws SQLException;
}