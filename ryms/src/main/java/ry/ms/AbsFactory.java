package ry.ms;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.user.login.dao.UserDAO;

/**
 * Abstract Factory for creating Data Access Object DAO instances.
 * This pattern allows for creating families of related objects without specifying their concrete classes.
 */
public abstract class AbsFactory {

    private AbsFactory absFactory;

    public AbsFactory getAbsFactory(){
        if(absFactory != null){
            return new PostgresFactory();
        }
        else{
            return absFactory;
        }
    }
    
    /**
     * Abstract method to create a UserDAO instance.
     * Subclasses will provide the specific implementation for a database.
     * @return An implementation of UserDAO.
     */
    public abstract UserDAO createUserDAO();

    public abstract MatchDAO createMatchDAO();


}
