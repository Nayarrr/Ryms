package ry.ms.view.team;

import java.sql.SQLException;

import ry.ms.businessLogic.team.SessionFacade;
import ry.ms.businessLogic.team.models.Team;

public class TeamController {

    private final SessionFacade sessionFacade;

    public TeamController() {
        this(SessionFacade.getInstance());
    }

    public TeamController(SessionFacade sessionFacade) {
        this.sessionFacade = sessionFacade;
    }

    public Team createTeam(String name, String tag, String avatar, String userEmail) throws SQLException {
        return sessionFacade.createTeam(name, tag, avatar, userEmail);
    }
}