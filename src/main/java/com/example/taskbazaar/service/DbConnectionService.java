package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.utility.TaskBazaarLogger;

import java.sql.Connection;
import java.util.logging.Logger;

public class DbConnectionService {
    private static Logger logger = TaskBazaarLogger.getLogger();
    public static Connection getConnection() throws Exception {
        logger.info("Connecting to database...");
        UserDao userDao = UserDao.getInstance();
        return userDao.connect();
    }
}
