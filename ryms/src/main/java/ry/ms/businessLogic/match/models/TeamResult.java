package ry.ms.businessLogic.match.models;

import ry.ms.businessLogic.team.models.Team;

public class TeamResult {
    int score;
    MatchResult status;
    final Team team;

    public TeamResult(Team team){
        this.team = team;
        this.score = 0;
        this.status = null;
    }

    public MatchResult getResult(){
        if(this.status == null){
            System.out.println("Le match n'est pas terminé !");
        }
        return this.status;
    }

    public void setResult(MatchResult result){
        this.status = result;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        if (score < 0) {
            throw new IllegalArgumentException("Le score ne peut pas être négatif");
        }
        this.score = score;
    }

    public Team getTeam(){
        return this.team;
    }

}
