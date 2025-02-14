package com.example.taskbazaar.dao;

import com.example.taskbazaar.utility.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class UserDao {

    /*
     * Configuration for Java database connectivity
     * 1. import ---> java.sql.*
     * 2. load and register the driver ---> com.mysql.jdbc.Driver
     * 3. Create connection ---> connection
     * 4. create a statement ----> statement
     * 5. execute the query
     * 6. process the result
     * 7. close
     */

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);


    private final String URL = ConfigLoader.getProperty("db.url");
    private final String USER = ConfigLoader.getProperty("db.user");
    private final String PASSWORD = ConfigLoader.getProperty("db.password");
    private static final String DRIVER = ConfigLoader.getProperty("db.driver");
    private static UserDao singleObject = null;

    private UserDao() throws Exception {
        Class.forName(DRIVER);
        logger.info("Driver Registered:: {}", DRIVER);
    }

    public static UserDao getInstance() throws Exception {
        if (singleObject == null) {
            singleObject = new UserDao();
        }
        logger.info("create instance of {}",DRIVER);
        return singleObject;
    }

    public Connection connect() throws Exception {
        logger.info("connected to {}",DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}