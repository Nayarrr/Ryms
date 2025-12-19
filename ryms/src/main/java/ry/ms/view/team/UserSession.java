package ry.ms.view.team;

public final class UserSession {
    private static UserSession instance;
    private String userEmail;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void setUserEmail(String email) { this.userEmail = email; }
    public String getUserEmail() { return userEmail; }
}