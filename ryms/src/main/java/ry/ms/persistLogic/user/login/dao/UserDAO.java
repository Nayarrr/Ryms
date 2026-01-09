package ry.ms.persistLogic.user.login.dao;

import ry.ms.businessLogic.user.models.User;

import java.sql.SQLException;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findByEmail(String email) throws SQLException;

    User createUser(String email, String password, String username) throws SQLException;

    java.util.List<User> getAllUsers() throws SQLException;

    void updateUser(User user) throws SQLException;

    void deleteUser(String email) throws SQLException;

    void updatePassword(String email, String newPassword) throws SQLException;

    void updateStatus(String email, boolean isActive) throws SQLException;
}