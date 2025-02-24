/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

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
        String role = UserDao.getUserRoleByUsername(username);
        return role;
    }

    public List<User> getUsers() throws SQLException {
        List<User> users = UserDao.getAllUser();
        return users;
    }

    public boolean blockUser(String usernameForBlock) throws SQLException {
        return UserDao.updateIsBlockedStatusByUsername(usernameForBlock, true);
    }

    public boolean unBlockUser(String usernameForUnBlock) throws SQLException {
        boolean result = UserDao.updateIsBlockedStatusByUsername(usernameForUnBlock, false);
        return !result ? true : false;
    }
}
