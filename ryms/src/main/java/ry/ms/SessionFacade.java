package ry.ms;

import java.sql.SQLException;

import ry.ms.Exceptions.IncorrectPasswordException;
import ry.ms.Exceptions.UserDoesntExistException;
import ry.ms.MODELS.User;

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

    public User login(String mail, String password) throws UserDoesntExistException, SQLException, IncorrectPasswordException {
        return userManager.login(mail, password);
    }
}
