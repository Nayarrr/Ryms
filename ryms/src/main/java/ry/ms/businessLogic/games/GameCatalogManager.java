package ry.ms.businessLogic.games;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.persistLogic.games.dao.GameDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameCatalogManager {

    private final GameDAO gameDAO;
    public GameCatalogManager(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public List<Game> getGameCatalog() {
        try {
            return this.gameDAO.loadGameCatalog();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addGame(Game game) {
        try {
            this.gameDAO.saveGame(game);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du jeu", e);
        }
    }

    public void deleteGame(Game game) {
        try {
            this.gameDAO.deleteGame(game);
        }
        catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du jeu", e);
        }
    }

    public void updateGame(Game game) {
        try {
            this.gameDAO.updateGame(game);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'edition du jeu", e);
        }
    }

}
