package ry.ms.persistLogic.games.dao;

import ry.ms.businessLogic.games.models.Game;

import java.sql.SQLException;
import java.util.ArrayList;

public interface GameDAO {

    public abstract ArrayList<Game> loadGameCatalog() throws SQLException;
    public abstract void saveGame(Game game) throws SQLException;
    public abstract void deleteGame(Game game) throws SQLException;
    public abstract void updateGame(Game game) throws SQLException;
}
