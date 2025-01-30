package data;

public class User {
    private String username;
    private String password;
    private String role;
    private boolean isDeleted;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        isDeleted = false;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
