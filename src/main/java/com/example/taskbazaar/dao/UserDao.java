package com.example.taskbazaar.dao;

import java.sql.*;
import java.util.logging.Logger;

import com.example.taskbazaar.utility.ConfigLoader;
import com.example.taskbazaar.utility.TaskBazaarLogger;


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

    private static Logger logger = TaskBazaarLogger.getLogger();


    private String url = ConfigLoader.getProperty("db.url");
    private String user = ConfigLoader.getProperty("db.user");
    private String password = ConfigLoader.getProperty("db.password");
    private String driver = ConfigLoader.getProperty("db.driver");
    private static UserDao singleObject = null;

    private UserDao() throws Exception {
        Class.forName(driver);
        logger.info("Driver Registered: " + driver);
    }

    public static UserDao getInstance() throws Exception {
        if (singleObject == null) {
            singleObject = new UserDao();
        }
        logger.info("Connected to database driver");
        return singleObject;
    }

    public Connection connect() throws Exception {
        logger.info("connecting successfully");
        return DriverManager.getConnection(url, user, password);
    }

}