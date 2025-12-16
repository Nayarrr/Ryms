package ry.ms.businessLogic.team;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import ry.ms.AbsFactory;
import ry.ms.businessLogic.team.models.Invitation;
import ry.ms.businessLogic.team.models.InvitationStatus;
import ry.ms.businessLogic.team.models.Team;
import ry.ms.persistLogic.team.dao.InvitationDAO;
import ry.ms.persistLogic.team.dao.TeamDAO;
import ry.ms.persistLogic.user.login.dao.UserDAO;

public class TeamManager {

    private final TeamDAO teamDAO;
    private final InvitationDAO invitationDAO;
    private final UserDAO userDAO;

    public TeamManager(AbsFactory factory) {
        Objects.requireNonNull(factory, "Factory must not be null.");
        this.teamDAO = factory.createTeamDAO();
        this.invitationDAO = factory.createInvitationDAO();
        this.userDAO = factory.createUserDAO();
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

    public void inviteMember(Long teamId, String senderEmail, String targetEmail) throws SQLException {
        Team team = loadTeamOrThrow(teamId);
        ensureCaptain(team, senderEmail);

        if (userDAO.getUserById(targetEmail) == null) {
            throw new IllegalArgumentException("User does not exist.");
        }

        if (teamDAO.isMember(teamId, targetEmail)) {
            throw new IllegalStateException("Target user is already a member of the team.");
        }

        Invitation invitation = new Invitation(null, teamId, senderEmail, targetEmail, InvitationStatus.PENDING, new Date());
        invitationDAO.save(invitation);
    }

    public void acceptInvitation(Long invitationId) throws SQLException {
        Invitation invitation = requireInvitation(invitationId);
        ensurePending(invitation);

        teamDAO.addMember(invitation.getTeamId(), invitation.getReceiver());
        invitationDAO.updateStatus(invitationId, InvitationStatus.ACCEPTED);
    }

    public void rejectInvitation(Long invitationId) throws SQLException {
        Invitation invitation = requireInvitation(invitationId);
        ensurePending(invitation);
        invitationDAO.updateStatus(invitationId, InvitationStatus.REJECTED);
    }

    public void removeMember(Long teamId, String captainEmail, String targetMemberEmail) throws SQLException {
        Team team = loadTeamOrThrow(teamId);
        ensureCaptain(team, captainEmail);

        if (equalsIgnoreCase(team.getCaptainEmail(), targetMemberEmail)) {
            throw new IllegalStateException("Captain cannot be removed through kick operation.");
        }

        if (!teamDAO.isMember(teamId, targetMemberEmail)) {
            throw new IllegalArgumentException("Target user is not a member of this team.");
        }

        teamDAO.removeMember(teamId, targetMemberEmail);
    }

    public void leaveTeam(Long teamId, String userEmail) throws SQLException {
        Team team = loadTeamOrThrow(teamId);

        if (equalsIgnoreCase(team.getCaptainEmail(), userEmail)) {
            throw new IllegalStateException("Captain cannot leave. Transfer captaincy or dissolve first.");
        }

        if (!teamDAO.isMember(teamId, userEmail)) {
            throw new IllegalArgumentException("User is not a member of this team.");
        }

        teamDAO.removeMember(teamId, userEmail);
    }

    public void transferCaptaincy(Long teamId, String currentCaptain, String newCaptain) throws SQLException {
        Team team = loadTeamOrThrow(teamId);
        ensureCaptain(team, currentCaptain);

        if (!teamDAO.isMember(teamId, newCaptain)) {
            throw new IllegalArgumentException("New captain must already be a team member.");
        }

        teamDAO.updateCaptain(teamId, newCaptain);
    }

    public void dissolveTeam(Long teamId, String captainEmail) throws SQLException {
        Team team = loadTeamOrThrow(teamId);
        ensureCaptain(team, captainEmail);
        teamDAO.deleteTeam(teamId);
    }

    private Team loadTeamOrThrow(Long teamId) throws SQLException {
        Team team = teamDAO.getTeamById(teamId);
        if (team == null) {
            throw new IllegalArgumentException("Team not found.");
        }
        return team;
    }

    private Invitation requireInvitation(Long invitationId) throws SQLException {
        Invitation invitation = invitationDAO.findById(invitationId);
        if (invitation == null) {
            throw new IllegalArgumentException("Invitation not found.");
        }
        return invitation;
    }

    private void ensureCaptain(Team team, String email) {
        if (!equalsIgnoreCase(team.getCaptainEmail(), email)) {
            throw new IllegalStateException("Operation allowed to captain only.");
        }
    }

    private void ensurePending(Invitation invitation) {
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation is no longer pending.");
        }
    }

    private boolean equalsIgnoreCase(String left, String right) {
        return left != null && right != null && left.equalsIgnoreCase(right);
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
}