package ry.ms.view.user;

public final class UserSession {
    private static volatile UserSession instance;
    private String userEmail;
    private String role;
    private ry.ms.businessLogic.user.login.models.User user;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public void setUserEmail(String email) { 
        this.userEmail = email; 
    }
    
    public String getUserEmail() { 
        return userEmail; 
    }

    public void clearSession() {
        this.userEmail = null;
    }
}