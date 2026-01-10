package ry.ms.persistLogic.tournament.dao;

import ry.ms.businessLogic.tournament.models.Tournament;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class TournamentDAO {

    protected final Connection conn;

    public TournamentDAO(Connection conn) {
        this.conn = conn;
    }

    public abstract List<Tournament> loadTournamentCatalog() throws SQLException;
    public abstract void saveTournament(Tournament tournament) throws SQLException;
    public abstract void deleteTournament(Tournament tournament) throws SQLException;
    public abstract void updateTournament(Tournament tournament) throws SQLException;
}
