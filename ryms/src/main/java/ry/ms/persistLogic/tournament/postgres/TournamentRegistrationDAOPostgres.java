package ry.ms.persistLogic.tournament.postgres;

import ry.ms.businessLogic.tournament.models.TournamentRegistration;
import ry.ms.persistLogic.DBConfig;
import ry.ms.persistLogic.tournament.dao.TournamentRegistrationDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation PostgreSQL du DAO pour les inscriptions aux tournois
 */
public class TournamentRegistrationDAOPostgres implements TournamentRegistrationDAO {

    private Connection conn;

    public TournamentRegistrationDAOPostgres() {
        try {
            this.conn = DBConfig.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Can't connect to database", e);
        }
    }

    @Override
    public Long registerTeam(TournamentRegistration registration) throws SQLException {
        String sql = "INSERT INTO tournament_registrations (tournament_id, team_id, registration_date) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, registration.getTournamentId());
            stmt.setLong(2, registration.getTeamId());
            stmt.setTimestamp(3, new Timestamp(registration.getRegistrationDate().getTime()));

            stmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    registration.setRegistrationId(id);
                    return id;
                }
            }
        }
        return null;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByTournament(Long tournamentId) throws SQLException {
        String sql = "SELECT * FROM tournament_registrations WHERE tournament_id = ? ORDER BY registration_date DESC";
        List<TournamentRegistration> registrations = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }

    @Override
    public List<TournamentRegistration> getRegistrationsByTeam(Long teamId) throws SQLException {
        String sql = "SELECT * FROM tournament_registrations WHERE team_id = ? ORDER BY registration_date DESC";
        List<TournamentRegistration> registrations = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }

    @Override
    public boolean cancelRegistration(Long registrationId) throws SQLException {
        // Status logic removed, returning false as operation is no longer supported via
        // status update
        return false;
    }

    @Override
    public boolean isTeamRegistered(Long tournamentId, Long teamId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tournament_registrations " +
                "WHERE tournament_id = ? AND team_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);
            stmt.setLong(2, teamId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean confirmRegistration(Long registrationId) throws SQLException {
        // Status logic removed
        return true;
    }

    @Override
    public int getRegisteredTeamsCount(Long tournamentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tournament_registrations " +
                "WHERE tournament_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, tournamentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Mapper un ResultSet vers un objet TournamentRegistration
     */
    private TournamentRegistration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        TournamentRegistration registration = new TournamentRegistration();
        registration.setRegistrationId(rs.getLong("registration_id"));
        registration.setTournamentId(rs.getLong("tournament_id"));
        registration.setTeamId(rs.getLong("team_id"));
        registration.setRegistrationDate(rs.getTimestamp("registration_date"));

        return registration;
    }
}
