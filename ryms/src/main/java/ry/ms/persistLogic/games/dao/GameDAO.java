package ry.ms.persistLogic.games.dao;

import ry.ms.businessLogic.games.models.Game;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class GameDAO {
    /** The database connection instance, shared by all methods in a DAO implementation. */
    protected final Connection conn;

    public GameDAO(Connection conn) {
        this.conn = conn;
    }


    public abstract ArrayList<Game> loadGameCatalog() throws SQLException;
    public abstract void saveGame(Game game) throws SQLException;
    public abstract void deleteGame(Game game) throws SQLException;
    public abstract void updateGame(Game game) throws SQLException;
    public abstract Game getGameByName(String name) throws SQLException;
}
