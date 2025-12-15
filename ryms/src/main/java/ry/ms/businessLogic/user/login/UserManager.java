package ry.ms.businessLogic.user.login;
import java.sql.SQLException;

import ry.ms.businessLogic.user.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.user.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.user.login.models.User;
import ry.ms.persistLogic.user.login.dao.UserDAO;

/**
 * Manages user-related business logic, such as authentication.
 * This class acts as a service layer between the presentation/facade layer and the data access layer.
 */
public class UserManager {

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    /** The Data Access Object for handling user persistence. */
    private UserDAO userDAO;
    
    /**
     * Authenticates a user based on their email and password.
     *
     * @param mail The email of the user trying to log in.
     * @param password The password provided by the user.
     * @return The {@link User} object if authentication is successful.
     * @throws UserDoesntExistException if the user with the specified email does not exist.
     * @throws IncorrectPasswordException if the provided password is not correct.
     * @throws SQLException if a database access error occurs.
     */
    public User login(String mail, String password) throws UserDoesntExistException, SQLException, IncorrectPasswordException{
        try {
            User user = userDAO.getUserById(mail);
            if(user == null ){
                throw new UserDoesntExistException("User does not exist.");
            }
            if(!user.getPassword().equals(password)){
                throw new IncorrectPasswordException("Incorrect Password");
            }
            return user;
        } catch (UserDoesntExistException e) {
            throw new UserDoesntExistException("User does not exist.");
        }
        catch (IncorrectPasswordException pass){
            throw new IncorrectPasswordException("Incorrect Password");
        }
    }
    
}
