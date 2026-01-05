package ry.ms;

import java.sql.SQLException;

import ry.ms.persistLogic.match.dao.MatchDAO;
import ry.ms.persistLogic.match.dao.MatchResultDAO;
import ry.ms.persistLogic.match.postgres.MatchDAOPostgres;
import ry.ms.persistLogic.match.postgres.MatchResultDAOPostgres;
import ry.ms.persistLogic.basket.dao.BasketDAO;
import ry.ms.persistLogic.basket.postgres.BasketDAOPostgres;
import ry.ms.persistLogic.product.dao.ProductDAO;
import ry.ms.persistLogic.product.postgres.ProductDAOPostgres;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.team.postgres.InvitationDAOPostgres;
import ry.ms.persistLogic.team.postgres.TeamDAOPostgres;
import ry.ms.persistLogic.user.login.dao.UserDAO;
import ry.ms.persistLogic.user.login.postgres.UserDAOPostgres;
/**
 * A concrete factory that creates instances of DAOs.
 * This implementation of {@link AbsFactory} is responsible for instantiating
 * the specific DAO implementations for the application.
 */
public class PostgresFactory extends AbsFactory {

    /**
     * Creates an instance of {@link UserDAOPostgres}.
     * It handles the potential {@link SQLException} during DAO instantiation
     * by wrapping it in a {@link RuntimeException}.
     * @return A new instance of UserDAO.
     */
    @Override
    public UserDAO createUserDAO() {
        return new UserDAOPostgres();
    }

    @Override
    public MatchDAO createMatchDAO(){
        return new MatchDAOPostgres();
    }

    @Override
    public TeamDAO createTeamDAO() {
        return new TeamDAOPostgres();
    }

    @Override
    public InvitationDAO createInvitationDAO() {
        return new InvitationDAOPostgres();
    }

    @Override
    public ProductDAO createProductDAO() {
        return new ProductDAOPostgres();
    }

    @Override
    public BasketDAO createBasketDAO() {
        return new BasketDAOPostgres();
    }

    @Override
    public MatchResultDAO createMatchResultDAO(){
        return new MatchResultDAOPostgres() {
        };
    }
}
