package ry.ms;

import java.sql.Connection;
import java.sql.SQLException;

import ry.ms.DAO.UserDAO;
import ry.ms.Exceptions.IncorrectPasswordException;
import ry.ms.Exceptions.UserDoesntExistException;
import ry.ms.MODELS.User;
import ry.ms.POSTGRES.UserPostgres;

public class SessionFacade {
    private static SessionFacade sessionFacade;
    private UserManager userManager;

    private SessionFacade() {
        try {
            Connection conn = DatabaseManager.getConnection();
            UserDAO userDAO = new UserPostgres(conn);
            this.userManager = new UserManager(userDAO);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't connect to database", e);
        }
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
