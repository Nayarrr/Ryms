package ry.ms.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Match {
    private Long matchId;
    public List<User> referees;
    public Date matchDate;
    public List<Team> teams;
    protected List<TeamResult> teamResults;
    public Long gameId;
    private MatchStatus status;

    public Match(){
        this.referees = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamResults = new ArrayList<>();
        this.status = MatchStatus.SCHEDULED;
    }

    public Match(Long matchid, Date matchDate, Long gameId, MatchStatus status){
        this.matchId = matchid;
        this.matchDate = matchDate;
        this.gameId = gameId;
        this.status = status;
        this.referees = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.teamResults = new ArrayList<>();
        this.status = status;
    }

    public Long getMatchId(){
        return this.matchId;
    }

    public void setMatchId(Long matchid){
        this.matchId = matchid;
    }
    
    public List<User> getReferees() {
        return this.referees;
    }
    
    public void setReferees(List<User> referees) {
        this.referees = referees;
    }
    
    public void addReferee(User referee) {
        if (!this.referees.contains(referee)) {
            this.referees.add(referee);
        }
    }
    
    public void removeReferee(User referee) {
        this.referees.remove(referee);
    }
    
    public Date getMatchDate() {
        return this.matchDate;
    }
    
    public void setMatchDate(Date matchDate) {
        this.matchDate = matchDate;
    }
    
    public List<Team> getTeams() {
        return this.teams;
    }
    
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
    
    public List<TeamResult> getTeamResults() {
        return this.teamResults;
    }
    
    public void setTeamResults(List<TeamResult> teamResults) {
        this.teamResults = teamResults;
    }

    public MatchStatus getStatus() {
        return this.status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

}
