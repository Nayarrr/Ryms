package ry.ms.persistLogic.user.login.postgres;

import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.user.login.dao.UserDAO;

import java.sql.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class UserDAOPostgres implements UserDAO {

    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT email, password, username, role, is_active, avatar FROM users WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Constructor: username, email, password, role, avatar, isActive
                    // Adjust based on User.java check
                    User user = new User(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getBytes("avatar"), // Assuming avatar is byte[]
                            rs.getBoolean("is_active"));
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public User createUser(String email, String password, String username) throws SQLException {
        String sql = "INSERT INTO users (email, password, username, is_active) VALUES (?, ?, ?, ?) RETURNING role";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password); // Should be hashed
            pstmt.setString(3, username);
            pstmt.setBoolean(4, true); // Default active

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    // Constructor for new user (no avatar yet)
                    // username, email, password, role, avatar, isActive
                    return new User(username, email, password, role, null, true);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT email, password, username, role, is_active, avatar FROM users ORDER BY username";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBytes("avatar"),
                        rs.getBoolean("is_active")));
            }
        }
        return users;
    }

    @Override
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, avatar = ? WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());
            pstmt.setBytes(4, user.getAvatar());
            pstmt.setString(5, user.getEmail());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteUser(String email) throws SQLException {
        String sql = "DELETE FROM users WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updatePassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    @Override
    public void updateStatus(String email, boolean isActive) throws SQLException {
        String sql = "UPDATE users SET is_active = ? WHERE email = ?";
        try (Connection conn = DBConfig.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isActive);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }
}