package com.example.taskbazaar.service;

import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {
    private static volatile UserService userService = null;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserService() {
        logger.info("UserService created");
    }

    public static UserService getInstance() {
        if (userService == null) {
            synchronized (UserService.class) {
                if (userService == null) {
                    userService = new UserService();
                }
            }
        }
        return userService;
    }

    public String getUserRole(String username) throws SQLException {
        String role;
        try (Connection connection = DbConnectionService.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Constants.USER_ROLE_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            role = resultSet.getString("role");
            logger.info("{} is {}", username, role);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SQLException(e);
        }
        return role;
    }
}
