package ry.ms.businessLogic.login;
import java.sql.SQLException;

import ry.ms.businessLogic.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.login.models.User;
import ry.ms.persistLogic.login.dao.UserDAO;

public class UserManager {
    private UserDAO userDAO;
    
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
