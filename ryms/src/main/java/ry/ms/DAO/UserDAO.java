package ry.ms.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import ry.ms.MODELS.User;

public abstract class UserDAO {
    
    protected final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }
    
    public abstract User getUserById(String email) throws SQLException;
}