package ry.ms.persistLogic.tournament.postgres;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.tournament.models.Tournament;
import ry.ms.businessLogic.tournament.models.TournamentStatus;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.tournament.dao.TournamentDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TournamentDAOPostgres extends TournamentDAO {
    public TournamentDAOPostgres() {
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
    public List<Tournament> loadTournamentCatalog() throws SQLException {
        String sql = "SELECT t.*, g.name as game_name, g.editor, g.releaseDate, g.logo " +
                "FROM tournaments t " +
                "LEFT JOIN games g ON t.game_id = g.game_id";
        ArrayList<Tournament> res = new ArrayList<>();

        try (PreparedStatement stmt = this.conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Créer l'objet Game si game_id n'est pas null
                Game game = null;
                if (rs.getInt("game_id") != 0) {
                    game = new Game(
                            rs.getInt("game_id"),
                            rs.getString("game_name"),
                            rs.getString("editor"),
                            rs.getDate("releaseDate"),
                            rs.getBytes("logo"));
                }

                // Créer le Tournament
                Tournament tournament = new Tournament(
                        rs.getInt("tournament_id"),
                        rs.getString("name"),
                        game,
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getInt("max_participants"),
                        rs.getString("location"));

                // Set le status
                String statusStr = rs.getString("status");
                if (statusStr != null) {
                    tournament.setStatus(TournamentStatus.valueOf(statusStr));
                }

                res.add(tournament);
            }
        }
        return res;
    }

    @Override
    public void saveTournament(Tournament tournament) throws SQLException {
        String sql = "INSERT INTO tournaments (name, game_id, start_date, end_date, max_participants, location, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tournament.getName());

            // Gérer le game_id (peut être null)
            if (tournament.getGame() != null) {
                stmt.setInt(2, tournament.getGame().getGameId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setTimestamp(3,
                    tournament.getStartDate() != null ? new java.sql.Timestamp(tournament.getStartDate().getTime())
                            : null);
            stmt.setTimestamp(4,
                    tournament.getEndDate() != null ? new java.sql.Timestamp(tournament.getEndDate().getTime()) : null);
            stmt.setInt(5, tournament.getMaxParticipants());
            stmt.setString(6, tournament.getLocation());
            stmt.setString(7, tournament.getStatus().toString());

            stmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    tournament.setTournamentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void deleteTournament(Tournament tournament) throws SQLException {
        String sql = "DELETE FROM tournaments WHERE tournament_id = ?";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setInt(1, tournament.getTournamentId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void updateTournament(Tournament tournament) throws SQLException {
        String sql = "UPDATE tournaments SET name = ?, game_id = ?, start_date = ?, end_date = ?, " +
                "max_participants = ?, location = ?, status = ? WHERE tournament_id = ?";

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            stmt.setString(1, tournament.getName());

            // Gérer le game_id (peut être null)
            if (tournament.getGame() != null) {
                stmt.setInt(2, tournament.getGame().getGameId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setTimestamp(3,
                    tournament.getStartDate() != null ? new java.sql.Timestamp(tournament.getStartDate().getTime())
                            : null);
            stmt.setTimestamp(4,
                    tournament.getEndDate() != null ? new java.sql.Timestamp(tournament.getEndDate().getTime()) : null);
            stmt.setInt(5, tournament.getMaxParticipants());
            stmt.setString(6, tournament.getLocation());
            stmt.setString(7, tournament.getStatus().toString());
            stmt.setInt(8, tournament.getTournamentId());

            stmt.executeUpdate();
        }
    }
}