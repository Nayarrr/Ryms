package ry.ms.businessLogic.games;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.persistLogic.games.dao.GameDAO;
import ry.ms.persistLogic.games.postgres.GameDAOPostgres;

import java.sql.SQLException;
import java.util.List;

/**
 * Facade class for managing the game catalog.
 * Implements the Singleton pattern and provides an Observable list of games.
 */
// Observable
public class GameCatalogFacade {
    private static GameCatalogFacade gameCatalogFacade;
    private GameCatalogManager gameCatalogManager;
    private final ObservableList<Game> gameList = FXCollections.observableArrayList();

    /**
     * Private constructor for Singleton.
     * Initializes the manager and loads the game list.
     */
    private GameCatalogFacade() {
        GameDAO gameDAO = new GameDAOPostgres();
        this.gameCatalogManager = new GameCatalogManager(gameDAO);
        this.gameList.addAll(gameCatalogManager.getGameCatalog());
    }

    /**
     * Gets the singleton instance of the GameCatalogFacade.
     * 
     * @return The singleton instance.
     */
    public static GameCatalogFacade getGameCatalogFactory() {
        if (gameCatalogFacade == null) {
            gameCatalogFacade = new GameCatalogFacade();
        }
        return gameCatalogFacade;
    }

    /**
     * Returns the observable list of games.
     * 
     * @return ObservableList of {@link Game}.
     */
    public ObservableList<Game> loadGameCatalog() {
        return this.gameList;
    }

    /**
     * Adds a new game to the catalog and updates the observable list.
     * 
     * @param game The {@link Game} to add.
     */
    public void addGame(Game game) {
        gameCatalogManager.addGame(game);
        gameList.add(game);
    }

    /**
     * Removes a game from the catalog and updates the observable list.
     * 
     * @param game The {@link Game} to remove.
     */
    public void deleteGame(Game game) {
        gameCatalogManager.deleteGame(game);
        gameList.remove(game);
    }

    /**
     * Updates an existing game in the catalog and the observable list.
     * 
     * @param game The {@link Game} to update.
     */
    public void updateGame(Game game) {
        gameCatalogManager.updateGame(game);
        for (int i = 0; i < gameList.size(); i++) {
            if (gameList.get(i).getName().equals(game.getName())) {
                gameList.set(i, game);
            }
        }
    }

    /**
     * Retrieves a game by its name.
     * 
     * @param name The name of the game.
     * @return The {@link Game} object if found, null otherwise.
     */
    public Game getGameByName(String name) {
        return this.gameCatalogManager.getGameByName(name);
    }
}
