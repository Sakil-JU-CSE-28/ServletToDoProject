package com.example.taskbazaar.config;

import com.example.taskbazaar.utility.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConfig {

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

    private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);


    private final String URL = ConfigLoader.getProperty("db.mysql.url");
    private final String USER = ConfigLoader.getProperty("db.mysql.user");
    private final String PASSWORD = ConfigLoader.getProperty("db.mysql.password");
    private static final String DRIVER = ConfigLoader.getProperty("db.mysql.driver");

    private DbConfig() throws Exception {
        Class.forName(DRIVER);
        logger.info("Driver Registered:: {}", DRIVER);
    }

    private static final class SingleObjectHolder {
        private static final DbConfig singleObject;
        static {
            try {
                singleObject = new DbConfig();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static DbConfig getInstance() throws Exception {
        logger.info("create instance of {}",DRIVER);
        return SingleObjectHolder.singleObject;
    }

    public Connection connect() throws Exception {
        logger.info("connected to {}",DRIVER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}