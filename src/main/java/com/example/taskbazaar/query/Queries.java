package com.example.taskbazaar.query;

public class Queries {
    public static final String SELECT_PASSWORD_BY_USERNAME = "SELECT password, salt FROM users WHERE username = ?";
    public static final String INSERT_USER = "INSERT INTO users (username, password, role, salt) VALUES (?, ?, ?, ?)";
}
