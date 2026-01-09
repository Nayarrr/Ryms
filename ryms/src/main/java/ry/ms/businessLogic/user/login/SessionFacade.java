package ry.ms.businessLogic.user.login;

import java.sql.SQLException;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.user.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.user.login.exceptions.UserAlreadyExistsException;
import ry.ms.businessLogic.user.login.exceptions.UserCreationException;
import ry.ms.businessLogic.user.models.User;

/**
 * A Facade for managing user sessions, including login.
 * This class provides a simplified interface to the session management
 * subsystem.
 * It is implemented as a Singleton to ensure only one instance exists.
 */
public class SessionFacade {
    /** The single instance of the SessionFacade. */
    private static SessionFacade sessionFacade;
    /** The manager responsible for user-related business logic. */
    private final UserManager userManager;

    /**
     * Private constructor to prevent direct instantiation and enforce the Singleton
     * pattern.
     * Note: The userManager field is not initialized here, which will lead to a
     * NullPointerException.
     * It should be initialized, for example, using a factory.
     */
    private SessionFacade() {
        // Adhering to Dependency Inversion Principle by using an Abstract Factory
        this.userManager = new UserManager(AbsFactory.getInstance().createUserDAO());
    }

    /**
     * Provides access to the singleton instance of the SessionFacade.
     * Creates the instance if it doesn't exist yet (lazy initialization).
     * 
     * @return The singleton {@link SessionFacade} instance.
     */
    public static SessionFacade getSessionFactory() {
        if (sessionFacade == null) {
            sessionFacade = new SessionFacade();
        }
        return sessionFacade;
    }

    /**
     * Attempts to log in a user with the given credentials.
     * It delegates the call to the {@link UserManager}.
     * 
     * @param mail     The user's email address.
     * @param password The user's password.
     * @return The authenticated {@link User} object.
     * @throws UserDoesntExistException   if no user is found with the given email.
     * @throws IncorrectPasswordException if the password does not match.
     * @throws SQLException               if a database access error occurs.
     */
    private User currentUser;

    /**
     * Attempts to log in a user with the given credentials.
     * It delegates the call to the {@link UserManager}.
     * 
     * @param mail     The user's email address.
     * @param password The user's password.
     * @return The authenticated {@link User} object.
     * @throws UserDoesntExistException   if no user is found with the given email.
     * @throws IncorrectPasswordException if the password does not match.
     * @throws SQLException               if a database access error occurs.
     */
    public User loginUser(String mail, String password)
            throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        User user = userManager.login(mail, password);
        this.currentUser = user;
        return user;
    }

    /**
     * Registers a new user.
     * 
     * @param email    The user's email.
     * @param password The user's password.
     * @param name     The user's first name.
     * @param surname  The user's last name.
     * @return The newly created User object.
     * @throws UserAlreadyExistsException if a user with the given email already
     *                                    exists.
     * @throws UserCreationException      if there is a database error during
     *                                    creation.
     */
    public User registerUser(String email, String password, String username, String surname)
            throws UserAlreadyExistsException, SQLException, UserCreationException { // Added SQLException to signature
        User user = userManager.register(email, password, username, surname);
        this.currentUser = user;
        return user;
    }

    public boolean login(String mail, String password)
            throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        User user = userManager.login(mail, password);
        if (user != null) {
            this.currentUser = user;
            return true;
        }
        return false;
    }

    public void updateUser(User user) throws SQLException {
        userManager.updateUser(user);
        this.currentUser = user;
    }

    public void deleteUser() throws SQLException {
        if (currentUser != null) {
            userManager.deleteUser(currentUser.getEmail());
            logout();
        }
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

}
