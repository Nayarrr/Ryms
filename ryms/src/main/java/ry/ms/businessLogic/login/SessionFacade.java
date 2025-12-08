package ry.ms.businessLogic.login;

import java.sql.SQLException;

import ry.ms.businessLogic.login.exceptions.IncorrectPasswordException;
import ry.ms.businessLogic.login.exceptions.UserDoesntExistException;
import ry.ms.businessLogic.login.models.User;

public class SessionFacade {
    private static SessionFacade sessionFacade;
    private UserManager userManager;

    private SessionFacade() {
    }

    public static SessionFacade getSessionFactory() {
        if(sessionFacade == null){
            sessionFacade = new SessionFacade();
        }
        return sessionFacade;
    }

    public boolean login(String mail, String password) throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        User user =  userManager.login(mail, password);
        if (user != null){
            return true;
        }
        else{
            return false;
        }
    }
}
