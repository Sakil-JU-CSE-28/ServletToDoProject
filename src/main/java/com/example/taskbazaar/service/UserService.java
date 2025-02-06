package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private static UserService userService = null;
    private UserService(){
    }

    public static UserService getInstance(){
        return userService==null?userService=new UserService():userService;
    }

    public String getUserRole(String username) throws SQLException {

        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.USER_ROLE_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? resultSet.getString("role") : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
