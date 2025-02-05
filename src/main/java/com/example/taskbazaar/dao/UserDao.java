package com.example.taskbazaar.dao;

import java.sql.*;
import com.example.taskbazaar.utility.ConfigLoader;


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


    private String url = ConfigLoader.getProperty("db.url");
    private String user = ConfigLoader.getProperty("db.user");
    private String password = ConfigLoader.getProperty("db.password");
    private String driver = ConfigLoader.getProperty("db.driver");
    private static UserDao singleObject = null;
    private UserDao() throws Exception {
        Class.forName(driver);
    }

    public static UserDao getInstance() throws Exception {
        if (singleObject == null) {
            singleObject = new UserDao();
        }
        return singleObject;
    }

    public Connection connect() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

}