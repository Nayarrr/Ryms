package ry.ms.businessLogic.login;
import ry.ms.persistLogic.login.dao.UserDAO;

public abstract class AbsFactory {
    
    public abstract UserDAO createUserDAO();
}
