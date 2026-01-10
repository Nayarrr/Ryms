package ry.ms.businessLogic.games;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ry.ms.businessLogic.games.models.Game;
import ry.ms.persistLogic.games.dao.GameDAO;
import ry.ms.persistLogic.games.postgres.GameDAOPostgres;

import java.sql.SQLException;
import java.util.List;

//Observable
public class GameCatalogFacade {
    private static GameCatalogFacade gameCatalogFacade;
    private GameCatalogManager gameCatalogManager;
    private final ObservableList<Game> gameList = FXCollections.observableArrayList();

    private GameCatalogFacade() {
        GameDAO gameDAO = new GameDAOPostgres();
        this.gameCatalogManager = new GameCatalogManager(gameDAO);
        this.gameList.addAll(gameCatalogManager.getGameCatalog());
    }

    public static GameCatalogFacade getGameCatalogFactory() {
        if (gameCatalogFacade == null) {
            gameCatalogFacade = new GameCatalogFacade();
        }
        return gameCatalogFacade;
    }

    public ObservableList<Game> loadGameCatalog() {
        return this.gameList;
    }

    public void addGame(Game game) {
        gameCatalogManager.addGame(game);
        gameList.add(game);
    }

    public void deleteGame(Game game) {
        gameCatalogManager.deleteGame(game);
        gameList.remove(game);
    }

    public void updateGame(Game game) {
        gameCatalogManager.updateGame(game);
        for (int i = 0; i < gameList.size(); i++) {
            if (gameList.get(i).getName().equals(game.getName())) {
                gameList.set(i, game);
            }
        }
    }

    public Game getGameByName(String name) {
        return this.gameCatalogManager.getGameByName(name);
    }
}
