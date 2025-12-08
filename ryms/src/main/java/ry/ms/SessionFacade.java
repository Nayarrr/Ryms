package ry.ms;

import java.sql.SQLException;

import ry.ms.Exceptions.IncorrectPasswordException;
import ry.ms.Exceptions.UserDoesntExistException;
import ry.ms.MODELS.User;

/**
 * A Facade for managing user sessions, including login.
 * This class provides a simplified interface to the session management subsystem.
 * It is implemented as a Singleton to ensure only one instance exists.
 */
public class SessionFacade {
    /** The single instance of the SessionFacade. */
    private static SessionFacade sessionFacade;
    /** The manager responsible for user-related business logic. */
    private UserManager userManager;

    /**
     * Private constructor to prevent direct instantiation and enforce the Singleton pattern.
     * Note: The userManager field is not initialized here, which will lead to a NullPointerException.
     * It should be initialized, for example, using a factory.
     */
    private SessionFacade() {
    }

    /**
     * Provides access to the singleton instance of the SessionFacade.
     * Creates the instance if it doesn't exist yet (lazy initialization).
     * @return The singleton {@link SessionFacade} instance.
     */
    public static SessionFacade getSessionFactory() {
        if(sessionFacade == null){
            sessionFacade = new SessionFacade();
        }
        return sessionFacade;
    }

    /**
     * Attempts to log in a user with the given credentials.
     * It delegates the call to the {@link UserManager}.
     * @param mail The user's email address.
     * @param password The user's password.
     * @return The authenticated {@link User} object.
     * @throws UserDoesntExistException if no user is found with the given email.
     * @throws IncorrectPasswordException if the password does not match.
     * @throws SQLException if a database access error occurs.
     */
    public User login(String mail, String password) throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        return userManager.login(mail, password);
    }
}
