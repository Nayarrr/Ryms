package ry.ms.persistLogic.games.postgres;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.user.login.models.User;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.games.dao.GameDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GameDAOPostgres extends GameDAO {

    public GameDAOPostgres() {
        super(initConnection());
    }

    private static Connection initConnection() {
        try {
            return DBConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database", e);
        }
    }

    @Override
    public ArrayList<Game> loadGameCatalog() throws SQLException {
        System.out.println("Connexion à : " + this.conn.getMetaData().getURL());
        System.out.println("Utilisateur : " + this.conn.getMetaData().getUserName());
        String sql = "SELECT * FROM games";
        ArrayList<Game> res = new ArrayList<>();
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                // Remplacez 'if' par 'while' pour parcourir toute la table
                while (rs.next()) {
                    res.add(new Game(rs.getString("name"), rs.getString("editor"), rs.getDate("releaseDate"), rs.getBytes("logo")));
                }
            }
        }
        return res;
    }

    @Override
    public void saveGame(Game game) throws SQLException {
        String sql = "INSERT INTO games (name, editor, releaseDate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.setDate(3, new java.sql.Date(game.getReleaseDate().getTime()));
            stmt.executeUpdate();
        }
    }

    public void deleteGame(Game game) throws SQLException {
        String sql = "DELETE FROM games WHERE name = ? AND editor = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateGame(Game game) throws SQLException {
        String sql = "UPDATE games SET editor = ?, releaseDate = ?, logo = ?, name = ? WHERE name = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, game.getEditor());
            stmt.setDate(2, new java.sql.Date(game.getReleaseDate().getTime()));
            stmt.setBytes(3, game.getLogo());
            stmt.setString(4, game.getName());
            stmt.setString(5, game.getName());
            stmt.executeUpdate();
        }
    }
}
