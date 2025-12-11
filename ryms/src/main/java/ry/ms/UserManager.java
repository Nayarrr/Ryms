package ry.ms;
import java.sql.SQLException;

import ry.ms.DAO.UserDAO;
import ry.ms.Exceptions.IncorrectPasswordException;
import ry.ms.Exceptions.UserDoesntExistException;
import ry.ms.MODELS.User;

public class UserManager {
    private UserDAO userDAO;

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
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
