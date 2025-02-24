/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.config.DbConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DatabaseService {
    private static Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    public static Connection getConnection() throws Exception {
        logger.info("Connecting to database...");
        DbConfig userDao = DbConfig.getInstance();
        return userDao.connect();
    }
}
