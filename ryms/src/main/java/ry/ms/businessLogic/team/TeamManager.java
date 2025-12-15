package ry.ms.businessLogic.team;

import java.sql.SQLException;
import java.util.Objects;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;

public class TeamManager {

    private final TeamDAO teamDAO;
    @SuppressWarnings("unused")
    private final InvitationDAO invitationDAO;

    public TeamManager(AbsFactory factory) {
        Objects.requireNonNull(factory, "Factory must not be null.");
        this.teamDAO = factory.createTeamDAO();
        this.invitationDAO = factory.createInvitationDAO();
    }

    public Team createTeam(String name, String tag, String avatar, String captainEmail) throws SQLException {
        validateName(name);
        validateTag(tag);
        validateCaptain(captainEmail);

        Team existingTeam = teamDAO.getTeamByName(name);
        if (existingTeam != null) {
            throw new IllegalArgumentException("Team name is already taken.");
        }

        Team team = new Team(null, name.trim(), tag.trim(), avatar, captainEmail.trim());
        return teamDAO.saveTeam(team);
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Team name is required.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 3 || trimmed.length() > 100) {
            throw new IllegalArgumentException("Team name must contain between 3 and 100 characters.");
        }
    }

    private void validateTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Team tag is required.");
        }
        String trimmed = tag.trim();
        if (trimmed.isEmpty() || trimmed.length() > 10) {
            throw new IllegalArgumentException("Team tag must contain between 1 and 10 characters.");
        }
    }

    private void validateCaptain(String captainEmail) {
        if (captainEmail == null || captainEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Captain email is required.");
        }
    }

    public void inviteMember(Long teamId, String senderEmail, String receiverEmail) {
        // TODO implement invitation workflow
    }

    public void removeMember(Long teamId, String userEmail) {
        // TODO implement member removal workflow
    }

    public void acceptInvitation(Long invitationId) {
        // TODO implement invitation acceptance workflow
    }

    public void rejectInvitation(Long invitationId) {
        // TODO implement invitation rejection workflow
    }
}