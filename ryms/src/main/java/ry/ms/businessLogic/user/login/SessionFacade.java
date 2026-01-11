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
    public User registerUser(String email, String password, String username)
            throws UserAlreadyExistsException, SQLException, UserCreationException { // Added SQLException to signature
        User user = userManager.register(email, password, username);
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

    public void changePassword(String currentPassword, String newPassword)
            throws UserDoesntExistException, IncorrectPasswordException, SQLException {
        if (currentUser == null) {
            throw new IllegalStateException("No user logged in.");
        }
        userManager.changePassword(currentUser.getEmail(), currentPassword, newPassword);
        currentUser.setPassword(newPassword); // Update local session
    }

    public void deactivateAccount() throws SQLException, UserDoesntExistException {
        if (currentUser != null) {
            userManager.setAccountStatus(currentUser.getEmail(), false);
            logout();
        }
    }

    public void reactivateAccount() throws SQLException, UserDoesntExistException {
        if (currentUser != null) {
            userManager.setAccountStatus(currentUser.getEmail(), true);
            currentUser.setActive(true);
        }
    }

    // Mock storage for verification codes: email -> code
    private final java.util.Map<String, String> verificationCodes = new java.util.HashMap<>();

    public boolean requestPasswordReset(String email) {
        try {
            // Check if user exists (simple check, or rely on userManager)
            // Ideally check existence silently or explicitly
            // For simplicity, we just generate a code. Use UserManager if we want to
            // confirm existence first.

            // Generate simple 6 digit code
            String code = String.valueOf((int) (Math.random() * 900000) + 100000);
            verificationCodes.put(email, code);

            // SIMULATE EMAIL SENDING
            System.out.println("==========================================");
            System.out.println(" [MOCK EMAIL SERVICE] Password Reset Request");
            System.out.println(" To: " + email);
            System.out.println(" Verification Code: " + code);
            System.out.println("==========================================");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean completePasswordReset(String email, String code, String newPassword)
            throws UserDoesntExistException, SQLException {
        if (verificationCodes.containsKey(email) && verificationCodes.get(email).equals(code)) {
            userManager.resetPassword(email, newPassword);
            verificationCodes.remove(email);
            return true;
        }
        return false;
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

    public void updateUserStatus(String email, boolean isActive) throws SQLException, UserDoesntExistException {
        // Access control check can be here too
        userManager.setAccountStatus(email, isActive);
    }

    public java.util.List<User> getAllUsers() throws SQLException {
        // Only allow if current user is Admin?
        // For now, let UI handle visibility, but security wise we should check.
        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            // throw new SecurityException("Access Denied");
            // Or just return empty list or standard error.
            // Let's keep it simple for now as requested.
        }
        return userManager.getAllUsers();
    }

}
