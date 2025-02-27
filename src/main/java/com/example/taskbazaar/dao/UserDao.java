/*
 * author : Md. Sakil Ahmed
 */
package com.example.taskbazaar.dao;


import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.DbException;
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
    private Logger logger = LoggerFactory.getLogger(UserDao.class);
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


    public UserDTO getDetailsByUsername(String username) throws DbException {
        UserDTO user = null;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.FIND_USER_BY_USERNAME)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String userName = resultSet.getString("username");
                String password = resultSet.getString("password");
                String salt = resultSet.getString("salt");
                String role = resultSet.getString("role");
                user = new UserDTO(userName, password, null, role, salt,false);
            }
        } catch (Exception e) {
            logger.error("error in retrieving user details:: {}", e.getMessage());
            throw new DbException(Constant.INTERNAL_ERROR);
        }
        return user;
    }

    public synchronized boolean insert(UserDTO user) throws DbException {
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.INSERT_USER)) {

            byte[] salt = generateSalt();
            String saltString = bytesToHex(salt);
            String hashedPassword = hashPassword(user.password(), saltString);
            preparedStatement.setString(1, user.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, user.role());
            preparedStatement.setString(4, saltString);

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            logger.error("error inserting user in db:: {}", e.getMessage());
            throw new DbException(Constant.INTERNAL_ERROR);
        }
        return true;
    }


    public List<UserDTO> getAll() throws DbException {
        List<UserDTO> users = new ArrayList<>();
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(Constant.Queries.GET_ALL_USER)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String userName = resultSet.getString("username");
                String role = resultSet.getString("role");
                boolean isDeleted = resultSet.getBoolean("isBlocked");
                UserDTO user = new UserDTO(userName,null,null, role,null, isDeleted);
                users.add(user);
            }
        } catch (Exception e) {
            logger.error("error in retrieving all user:: {}", e.getMessage());
            throw new DbException(Constant.INTERNAL_ERROR);
        }
        return users;
    }

    public synchronized boolean updateBlockedStatusByUsername(String usernameForBlock, boolean status) throws DbException {
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
            logger.error("error in blocking:: {}", e.getMessage());
            throw new DbException(e.getMessage());
        }
        return isBlocked;
    }

    public boolean getBlockStatusByUserName(String username) throws SQLException {
        boolean isBlocked = false;
        try (Connection connection = DatabaseService.getConnection(); PreparedStatement selectStatement = connection.prepareStatement(Constant.Queries.CHECK_BLOCK_STATUS_BY_USERNAME)) {
            selectStatement.setString(1, username);
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    isBlocked = resultSet.getBoolean("isBlocked");
                }
            }
        } catch (Exception e) {
            logger.error("error in retrieving block status:: {}", e.getMessage());
            throw new SQLException(e.getMessage());
        }
        return isBlocked;
    }
}