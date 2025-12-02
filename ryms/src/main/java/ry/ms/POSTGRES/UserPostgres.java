package ry.ms.POSTGRES;

import ry.ms.DAO.UserDAO;
import ry.ms.MODELS.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserPostgres extends UserDAO {

    public UserPostgres(Connection conn) {
        super(conn);
    }

    @Override
    public User getUserById(String email) throws SQLException {
        String sql = "SELECT email, username, password, avatar FROM users WHERE email = ?";
        
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