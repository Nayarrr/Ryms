package ry.ms.businessLogic.tournament.models;

import ry.ms.businessLogic.games.models.Game;
import ry.ms.businessLogic.match.models.Match;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.businessLogic.tournament.models.TournamentStatus;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class Tournament {
    private int tournamentId;
    private String name;
    private Game game;
    private Date startDate;
    private Date endDate;
    private int maxParticipants;
    private TournamentStatus status;
    private List<Match> matches;
    private List<Team> teams;
    private String location;

    public Tournament(int id, String name, Game game, Date startDate, Date endDate, int maxParticipants, String location) {
        this.tournamentId = id;
        this.name = name;
        this.game = game;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxParticipants = maxParticipants;
        this.status = TournamentStatus.PLANNING;
        this.matches = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public TournamentStatus getStatus() {
        return status;
    }

    public void setStatus(TournamentStatus status) {
        this.status = status;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }
}