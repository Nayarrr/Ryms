package ry.ms.models;

public class User {
    private String email;
    private String username;
    private String password;
    private byte[] avatar; // BYTEA in Postgres maps to byte[] in Java
    private String role;

    public User(String email, String username, String password, byte[] avatar, String role) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getAvatar() { return avatar; }
    public void setAvatar(byte[] avatar) { this.avatar = avatar; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "', role='" + role + "'}";
    }
}