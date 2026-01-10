package ry.ms.persistLogic.games.postgres;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.games.dao.GameDAO;

import java.sql.*;
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
        String sql = "INSERT INTO games (name, editor, releaseDate, logo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.setDate(3, game.getReleaseDate() != null ?
                    new java.sql.Date(game.getReleaseDate().getTime()) : null);
            stmt.setBytes(4, game.getLogo());
            stmt.executeUpdate();

            // Récupérer l'ID auto-généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    game.setGameId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void deleteGame(Game game) throws SQLException {
        String sql = "DELETE FROM games WHERE game_id = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateGame(Game game) throws SQLException {
        String sql = "UPDATE games SET name = ?, editor = ?, releaseDate = ?, logo = ? WHERE game_id = ?";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, game.getName());
            stmt.setString(2, game.getEditor());
            stmt.setDate(3, game.getReleaseDate() != null ?
                    new java.sql.Date(game.getReleaseDate().getTime()) : null);
            stmt.setBytes(4, game.getLogo());
            stmt.setInt(5, game.getGameId());
            stmt.executeUpdate();
        }
    }

    @Override
    public Game getGameByName(String name) throws SQLException {
        String sql = "SELECT * FROM games WHERE name = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Game(
                            rs.getInt("game_id"),
                            rs.getString("name"),
                            rs.getString("editor"),
                            rs.getDate("releaseDate"),
                            rs.getBytes("logo")
                    );
                }
            }
        }
        return null;
    }
}