/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.service;

import com.example.taskbazaar.config.DbConfig;


import java.sql.Connection;

public class DatabaseService {

    public static Connection getConnection() throws Exception {
        DbConfig userDao = DbConfig.getInstance();
        return userDao.connect();
    }
}
