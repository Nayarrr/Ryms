package ry.ms;
import ry.ms.DAO.UserDAO;

public abstract class AbsFactory {
    
    public abstract UserDAO createUserDAO();
}
