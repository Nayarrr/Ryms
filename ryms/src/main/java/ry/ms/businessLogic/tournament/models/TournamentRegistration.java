package ry.ms.businessLogic.tournament.models;

import java.util.Date;

/**
 * Représente une inscription d'une équipe à un tournoi
 */
public class TournamentRegistration {

    private Long registrationId;
    private Long tournamentId;
    private Long teamId;
    private Date registrationDate;

    public TournamentRegistration() {
        this.registrationDate = new Date();
    }

    public TournamentRegistration(Long registrationId, Long tournamentId, Long teamId, Date registrationDate) {
        this.registrationId = registrationId;
        this.tournamentId = tournamentId;
        this.teamId = teamId;
        this.registrationDate = registrationDate;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return "TournamentRegistration{" +
                "registrationId=" + registrationId +
                ", tournamentId=" + tournamentId +
                ", teamId=" + teamId +
                ", registrationDate=" + registrationDate +
                '}';
    }
}
