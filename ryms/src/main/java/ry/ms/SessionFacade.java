package ry.ms;

public class SessionFacade {
    private static SessionFacade sessionFacade;
    private UserManager userManager;

    private SessionFacade() {
    }

    public static SessionFacade getSessionFactory() {
        if(sessionFacade == null){
            sessionFacade = new SessionFacade();
        }
        return sessionFacade;
    }

    public boolean login(String username, String password){
        return userManager.login(username, password);
    }
}
