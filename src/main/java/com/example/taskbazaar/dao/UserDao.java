/*
 * author : Md. Sakil Ahmed
 */
package com.example.taskbazaar.dao;


import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.taskbazaar.utility.Common.hashPassword;
import static com.example.taskbazaar.utility.Common.bytesToHex;
import static com.example.taskbazaar.utility.Common.generateSalt;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public static int getUserCountByUsername(String username) throws AuthenticationException {
        int count;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            count = resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (Exception e) {
            throw new AuthenticationException(e.getMessage());
        }
        return count;
    }

    public static synchronized boolean insertUser(User user) throws AuthenticationException {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.INSERT_USER)) {

            byte[] salt = generateSalt();
            String saltString = bytesToHex(salt);
            String hashedPassword = hashPassword(user.getPassword(), saltString);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.getRole());
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new AuthenticationException(e.getMessage());
        }
        return true;
    }

    public static String getPasswordByUserName(String userName) throws AuthenticationException {
        String storedHashedPassword = null;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.SELECT_PASSWORD_BY_USERNAME)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    storedHashedPassword = resultSet.getString("password");
                }
            }
        } catch (SQLException e) {
            logger.error("SQL exception:: {}", e.getMessage());
            throw new AuthenticationException("Database error occurred");
        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new AuthenticationException("Unexpected error occurred");
        }
        return storedHashedPassword;
    }

    public static String getSaltByUsername(String userName) throws AuthenticationException {
        String storedSalt = null;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.SELECT_PASSWORD_BY_USERNAME)) {
            preparedStatement.setString(1, userName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    storedSalt = resultSet.getString("salt");
                }
            }
        } catch (SQLException e) {
            logger.error("Database error occurred:: " + e.getMessage());
            throw new AuthenticationException("");
        } catch (Exception e) {
            logger.error("Unexpected error occurred:: " + e.getMessage());
            throw new AuthenticationException("Unexpected error occurred:: " + e.getMessage());
        }
        if (storedSalt == null) {
            logger.error("Salt is null");
            throw new AuthenticationException("user not exist");
        }
        return storedSalt;
    }

    public static String getUserRoleByUsername(String username) throws SQLException {
        String role;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.USER_ROLE_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            role = resultSet.getString("role");

        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new SQLException(e.getMessage());
        }
        return role;
    }

    public static List<User> getAllUser() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.GET_ALL_USER)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                String role = resultSet.getString("role");
                boolean isDeleted = resultSet.getBoolean("isBlocked");
                User user = new User(userName, role, isDeleted);
                users.add(user);
            }
        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new SQLException(e.getMessage());
        }
        return users;
    }

    public static synchronized boolean updateIsBlockedStatusByUsername(String usernameForBlock, boolean status) throws SQLException {
        boolean isBlocked = false;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement updateStatement = connection.prepareStatement(Constant.Queries.BLOCK_USER); PreparedStatement selectStatement = connection.prepareStatement(Constant.Queries.CHECK_BLOCK_STATUS_BY_USERNAME)) {
            updateStatement.setBoolean(1, status);
            updateStatement.setString(2, usernameForBlock);
            updateStatement.executeUpdate();
            selectStatement.setString(1, usernameForBlock);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    isBlocked = resultSet.getBoolean("isBlocked");
                }
            }
        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new SQLException(e.getMessage());
        }
        return isBlocked;
    }

    public static boolean getBlockStatusByUserName(String username) throws SQLException {
        boolean isBlocked = false;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement selectStatement = connection.prepareStatement(Constant.Queries.CHECK_BLOCK_STATUS_BY_USERNAME)) {
            selectStatement.setString(1, username);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    isBlocked = resultSet.getBoolean("isBlocked");
                }
            }
        } catch (Exception e) {
            logger.error("error:: {}", e.getMessage());
            throw new SQLException(e.getMessage());
        }
        return isBlocked;
    }
}