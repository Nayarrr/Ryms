package ry.ms.businessLogic.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ry.ms.businessLogic.user.login.UserManager;
import ry.ms.businessLogic.user.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.user.login.dao.UserDAO;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserManager using a Mock DAO.
 */
public class UserManagerTest {

    private UserManager userManager;
    private MockUserDAO mockDAO;

    @BeforeEach
    public void setUp() {
        mockDAO = new MockUserDAO();
        userManager = new UserManager(mockDAO);
    }

    @Test
    public void testLogin_Success() throws Exception {
        mockDAO.save(new User("TestUser", "test@test.com", "password123", "Employee"));

        User user = userManager.login("test@test.com", "password123");
        Assertions.assertNotNull(user);
        Assertions.assertEquals("TestUser", user.getUsername());
    }

    @Test
    public void testLogin_UserNotFound() {
        Assertions.assertThrows(UserDoesntExistException.class, () -> {
            userManager.login("unknown@test.com", "pass");
        });
    }

    @Test
    public void testLogin_WrongPassword() throws SQLException {
        mockDAO.save(new User("TestUser", "test@test.com", "password123", "Employee"));

        Assertions.assertThrows(IncorrectPasswordException.class, () -> {
            userManager.login("test@test.com", "wrongpass");
        });
    }

    // --- Simple Mock DAO Inner Class ---
    static class MockUserDAO implements UserDAO {
        private java.util.Map<String, User> db = new java.util.HashMap<>();

        void save(User user) {
            db.put(user.getEmail(), user);
        }

        @Override
        public Optional<User> findByEmail(String email) throws SQLException {
            return Optional.ofNullable(db.get(email));
        }

        @Override
        public User createUser(String email, String password, String username) throws SQLException {
            User u = new User(username, email, password, "Employee");
            save(u);
            return u;
        }

        @Override
        public java.util.List<User> getAllUsers() throws SQLException {
            return new java.util.ArrayList<>(db.values());
        }

        @Override
        public void updateUser(User user) throws SQLException {
            db.put(user.getEmail(), user);
        }

        @Override
        public void deleteUser(String email) throws SQLException {
            db.remove(email);
        }

        @Override
        public void updatePassword(String email, String newPassword) throws SQLException {
            User u = db.get(email);
            if (u != null)
                u.setPassword(newPassword);
        }

        @Override
        public void updateStatus(String email, boolean isActive) throws SQLException {
            User u = db.get(email);
            if (u != null)
                u.setActive(isActive);
        }
    }
}
