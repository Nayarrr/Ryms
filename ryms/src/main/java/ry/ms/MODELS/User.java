package ry.ms.MODELS;

public class User {
    private String email;
    private String username;
    private String password;
    private byte[] avatar; // BYTEA in Postgres maps to byte[] in Java

    public User(String email, String username, String password, byte[] avatar) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getAvatar() { return avatar; }
    public void setAvatar(byte[] avatar) { this.avatar = avatar; }
    
    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "'}";
    }
}