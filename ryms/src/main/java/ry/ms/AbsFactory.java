package ry.ms;

import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;

/**
 * Abstract Factory for creating Data Access Object DAO instances.
 * This pattern allows for creating families of related objects without specifying their concrete classes.
 */
public abstract class AbsFactory {

    /**
     * Abstract method to create a UserDAO instance.
     * Subclasses will provide the specific implementation for a database.
     * @return An implementation of UserDAO.
     */
    public abstract UserDAO createUserDAO();

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
}
