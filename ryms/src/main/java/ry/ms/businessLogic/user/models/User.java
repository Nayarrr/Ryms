package ry.ms.businessLogic.user.models;

public class User {
    private String email;
    private String username;
    private String password;
    private String name;
    private byte[] avatar;
    private String role;
    private boolean isActive;

    public User(String username, String email, String password, String role) {
        this(username, email, password, role, null);
    }

    public User(String username, String email, String password, String role, byte[] avatar) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.isActive = true; // Default to active
    }

    public User(String username, String email, String password, String role, boolean isActive) {
        this(username, email, password, role, null, isActive);
    }

    public User(String username, String email, String password, String role, byte[] avatar, boolean isActive) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = avatar;
        this.isActive = isActive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * @return String
     */
    @Override
    public String toString() {
        return "User{email='" + email + "', username='" + username + "', role='" + role
                + "', isActive=" + isActive + "}";
    }
}