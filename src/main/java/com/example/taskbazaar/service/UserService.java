package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
       String role = UserDao.getUserRole(username);
       return role;
    }
}
