/*
 * author : Md. Sakil Ahmed
 */
package com.example.taskbazaar.dao;

import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.DatabaseService;
import com.example.taskbazaar.utility.Constants;
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
    private final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private static volatile UserDao instance = null;

    private UserDao() {}

    public static UserDao getInstance() {
        if (instance == null) {
            synchronized (UserDao.class) {
                if (instance == null) {
                    instance = new UserDao();
                }
            }
        }
        return instance;
    }


    public User getByUsername(String username) throws DbException {
        User user = null;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.User.FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String userName = resultSet.getString("username");
                String password = resultSet.getString("password");
                String salt = resultSet.getString("salt");
                String role = resultSet.getString("role");
                Boolean isBlocked = resultSet.getBoolean("isBlocked");
                user = new User(userName, password,role, salt,isBlocked);
            }
        } catch (Exception e) {
            logger.error("error in retrieving user details:: {}", e.getMessage(),e);
            throw new DbException(Constants.Error.INTERNAL_ERROR);
        }
        return user;
    }

    public boolean insert(UserDTO user) throws DbException {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.User.INSERT_USER)) {

            byte[] salt = generateSalt();
            String saltString = bytesToHex(salt);
            String hashedPassword = hashPassword(user.password(), saltString);
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.role());
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            logger.error("error inserting user in db:: {}", e.getMessage(),e);
            throw new DbException(Constants.Error.INTERNAL_ERROR);
        }
        return true;
    }


    public List<UserDTO> getAll() throws DbException {
        List<UserDTO> users = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constants.Queries.User.GET_ALL_USER)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                String role = resultSet.getString("role");
                boolean isDeleted = resultSet.getBoolean("isBlocked");
                UserDTO user = new UserDTO(userName,null,null, role,null, isDeleted);
                users.add(user);
            }
        } catch (Exception e) {
            logger.error("error in retrieving all user:: {}", e.getMessage(),e);
            throw new DbException(Constants.Error.INTERNAL_ERROR);
        }
        return users;
    }

    public boolean updateBlockedStatusByUsername(String username, boolean status) throws DbException {
        boolean isBlocked = false;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement updateStatement = connection.prepareStatement(Constants.Queries.User.BLOCK_USER); PreparedStatement selectStatement = connection.prepareStatement(Constants.Queries.User.CHECK_BLOCK_STATUS_BY_USERNAME)) {
            updateStatement.setBoolean(1, status);
            updateStatement.setString(2, username);
            updateStatement.executeUpdate();
            selectStatement.setString(1, username);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    isBlocked = resultSet.getBoolean("isBlocked");
                }
            }
        } catch (Exception e) {
            logger.error("error in blocking:: {} {}", username,e.getMessage(),e);
            throw new DbException(e.getMessage());
        }
        return isBlocked;
    }

    public boolean checkIsBlocked(String username) throws SQLException {
        boolean isBlocked = false;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement selectStatement = connection.prepareStatement(Constants.Queries.User.CHECK_BLOCK_STATUS_BY_USERNAME)) {
            selectStatement.setString(1, username);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    isBlocked = resultSet.getBoolean("isBlocked");
                }
            }
        } catch (Exception e) {
            logger.error("error in retrieving block status:: {} {}", username,e.getMessage(), e);
            throw new SQLException(e.getMessage());
        }
        return isBlocked;
    }
}