package ry.ms.businessLogic.team.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team {

    private Long teamId;
    private String name;
    private String tag;
    private String avatar;
    private String captainEmail;
    private List<String> memberEmails;

    public Team() {
        this.memberEmails = new ArrayList<>();
    }

    public Team(Long teamId, String name, String tag, String avatar, String captainEmail) {
        this.teamId = teamId;
        this.name = name;
        this.tag = tag;
        this.avatar = avatar;
        this.captainEmail = captainEmail;
        this.memberEmails = new ArrayList<>();
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCaptainEmail() {
        return captainEmail;
    }

    public void setCaptainEmail(String captainEmail) {
        this.captainEmail = captainEmail;
    }

    public List<String> getMemberEmails() {
        return Collections.unmodifiableList(memberEmails);
    }

    public void setMemberEmails(List<String> memberEmails) {
        if (memberEmails == null) {
            this.memberEmails = new ArrayList<>();
        } else {
            this.memberEmails = new ArrayList<>(memberEmails);
        }
    }

    public void addMemberEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        if (this.memberEmails == null) {
            this.memberEmails = new ArrayList<>();
        }
        this.memberEmails.add(email);
    }

    @Override
    public String toString() {
        return "Team{id=" + teamId + ", name='" + name + "', tag='" + tag + "', captain='" + captainEmail + "'}";
    }
}