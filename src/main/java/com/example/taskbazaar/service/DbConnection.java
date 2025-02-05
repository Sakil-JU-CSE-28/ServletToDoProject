package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;

import java.sql.Connection;

public class DbConnection {
    public static Connection getConnection() throws Exception {
        UserDao userDao = UserDao.getInstance();
        return userDao.connect();
    }
}
