package ry.ms;
import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.product.dao.ProductDAO;
import ry.ms.persistLogic.user.login.dao.UserDAO;

/**
 * Abstract Factory for creating Data Access Object DAO instances.
 * This pattern allows for creating families of related objects without specifying their concrete classes.
 */
public abstract class AbsFactory {

    private static AbsFactory absFactory;

    public static AbsFactory getInstance(){
        if(absFactory == null){
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


    /**
     * Abstract method to create a TeamDAO instance.
     * Subclasses will provide the specific implementation for a database.
     * @return An implementation of TeamDAO.
     */
    public abstract TeamDAO createTeamDAO();

    /**
     * Abstract method to create an InvitationDAO instance.
     * Subclasses will provide the specific implementation for a database.
     * @return An implementation of InvitationDAO.
     */
    public abstract InvitationDAO createInvitationDAO();

    /**
     * Abstract method to create a ProductDAO instance.
     * @return An implementation of ProductDAO.
     */
    public abstract ProductDAO createProductDAO();
}
