package com.example.taskbazaar.service;

import com.example.taskbazaar.query.Queries;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserService {
    private static UserService userService = null;
    private Logger logger = TaskBazaarLogger.getLogger();

    private UserService(){
        logger.info("UserService created");
    }

    public static UserService getInstance(){
        return userService==null?userService=new UserService():userService;
    }

    public String getUserRole(String username) throws SQLException {
        String role = null;
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Queries.USER_ROLE_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            role = resultSet.getString("role");
            logger.info("Extracted role : "+role);

        } catch (Exception e) {
           throw new SQLException(e);
        }
        return role;
    }
}
