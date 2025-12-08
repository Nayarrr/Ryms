package ry.ms.persistLogic.login.dao;

import java.sql.Connection;
import java.sql.SQLException;

import ry.ms.businessLogic.login.models.User;

public abstract class UserDAO {
    
    protected final Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }
    
    public abstract User getUserById(String email) throws SQLException;
}