package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DbConnectionService {
    private static Logger logger = LoggerFactory.getLogger(DbConnectionService.class);
    public static Connection getConnection() throws Exception {
        logger.info("Connecting to database...");
        UserDao userDao = UserDao.getInstance();
        return userDao.connect();
    }
}
