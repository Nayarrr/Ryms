package ry.ms.view.user;

import ry.ms.businessLogic.user.models.User;

public final class UserSession {
    private static volatile UserSession instance;
    private String userEmail;
    private String role;
    private User user;

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

    public void setUserEmail(String email) { this.userEmail = email; }
    public String getUserEmail() { return userEmail; }

    public void setRole(String role) { this.role = role; }
    public String getRole() { return role; }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userEmail = user.getEmail();
            this.role = user.getRole();
        }
    }

    public User getUser() { return user; }

    public void clearSession() {
        this.userEmail = null;
    }
}