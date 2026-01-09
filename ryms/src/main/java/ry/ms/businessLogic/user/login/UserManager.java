package ry.ms.businessLogic.user.login;

import java.sql.SQLException;

import ry.ms.businessLogic.user.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserAlreadyExistsException;
import ry.ms.businessLogic.user.login.exceptions.UserCreationException;
import ry.ms.businessLogic.user.models.User;
import ry.ms.persistLogic.user.login.dao.UserDAO;

/**
 * Manages user-related business logic, such as authentication.
 * This class acts as a service layer between the presentation/facade layer and
 * the data access layer.
 */
public class UserManager {

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /** The Data Access Object for handling user persistence. */
    private final UserDAO userDAO;

    /**
     * Authenticates a user based on their email and password.
     *
     * @param mail     The email of the user trying to log in.
     * @param password The password provided by the user.
     * @return The {@link User} object if authentication is successful.
     * @throws UserDoesntExistException   if the user with the specified email does
     *                                    not exist.
     * @throws IncorrectPasswordException if the provided password is not correct.
     * @throws SQLException               if a database access error occurs.
     */
    public User login(String mail, String password)
            throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        try {
            // Updated to use findByEmail (Optional) instead of hypothetical getUserById
            User user = userDAO.findByEmail(mail)
                    .orElseThrow(() -> new UserDoesntExistException("User does not exist."));

            if (!user.getPassword().equals(password)) {
                throw new IncorrectPasswordException("Incorrect Password");
            }
            return user;
        } catch (UserDoesntExistException e) {
            throw e;
        } catch (IncorrectPasswordException pass) {
            throw pass;
        }
    }

    public User register(String email, String password, String username, String surname)
            throws UserAlreadyExistsException, UserCreationException {
        try {
            // Check if user already exists
            if (userDAO.findByEmail(email).isPresent()) {
                throw new UserAlreadyExistsException("A user with the email " + email + " already exists.");
            }

            // Create the new user
            return userDAO.createUser(email, password, username, surname);

        } catch (SQLException e) {
            // Wrap the persistence error in a business-level exception
            throw new UserCreationException("Failed to create user due to a database error.", e);
        }
    }

    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    public void deleteUser(String email) throws SQLException {
        userDAO.deleteUser(email);
    }
}
