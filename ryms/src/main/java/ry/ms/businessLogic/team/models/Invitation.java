package ry.ms.businessLogic.team.models;

import java.util.Date;

public class Invitation {

    private Long id;
    private Long teamId;
    private String sender;
    private String receiver;
    private InvitationStatus status;
    private Date sentAt;

    public Invitation() {
    }

    public Invitation(Long id, Long teamId, String sender, String receiver, InvitationStatus status, Date sentAt) {
        this.id = id;
        this.teamId = teamId;
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public InvitationStatus getStatus() {
        return status;
    }

    public void setStatus(InvitationStatus status) {
        this.status = status;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "Invitation{id=" + id + ", teamId=" + teamId + ", sender='" + sender + "', receiver='" + receiver + "'}";
    }
}