package model;

public class User {
    public enum Role { ADMIN, TEACHER, STUDENT }

    private final String username;
    private String password;
    private final Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public void setPassword(String newPassword) { this.password = newPassword; }
}
