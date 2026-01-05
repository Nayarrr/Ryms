package ry.ms.persistLogic.games.postgres;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.games.dao.GameDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GameDAOPostgres implements GameDAO {

    public GameDAOPostgres() {
    }

    @Override
    public ArrayList<Game> loadGameCatalog() throws SQLException {
        String sql = "SELECT * FROM games";
        ArrayList<Game> res = new ArrayList<>();
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.setDate(3, new java.sql.Date(game.getReleaseDate().getTime()));
            stmt.executeUpdate();
        }
    }

    public void deleteGame(Game game) throws SQLException {
        String sql = "DELETE FROM games WHERE name = ? AND editor = ?";
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateGame(Game game) throws SQLException {
        String sql = "UPDATE games SET editor = ?, releaseDate = ?, logo = ?, name = ? WHERE name = ?";
        try (Connection conn = DBConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getEditor());
            stmt.setDate(2, new java.sql.Date(game.getReleaseDate().getTime()));
            stmt.setBytes(3, game.getLogo());
            stmt.setString(4, game.getName());
            stmt.setString(5, game.getName());
            stmt.executeUpdate();
        }
    }
}
