/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.model;

public class User {
    private String username;
    private String password;
    private String role;
    private String salt;
    private boolean isBlocked;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String userName, String role, boolean isDeleted) {
        this.username = userName;
        this.role = role;
        this.isBlocked = isDeleted;
    }

    public User(String userName, String password, String role, String salt, Boolean isBlocked) {
        this.username = userName;
        this.password = password;
        this.role = role;
        this.salt = salt;
        this.isBlocked = isBlocked;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + ", role=" + role + '}';
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
