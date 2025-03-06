/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.model.User;

import java.util.List;

public class UserService {
    private static volatile UserService userService = null;
    private final UserDao userDao = UserDao.getInstance();

    private UserService() {
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


    public UserDTO getByUsername(String username) throws DbException {
        User dbUser = userDao.getByUsername(username);
        UserDTO user = null;
        if (dbUser != null) {
            user = UserDTO.toDTO(dbUser);
        }
        return user;
    }

    public boolean insert(UserDTO user) throws DbException {
        return userDao.insert(user);
    }

    public List<UserDTO> getAll() throws DbException {
        return userDao.getAll();
    }

    public boolean block(String usernameForBlock) throws DbException {
        return userDao.updateBlockedStatusByUsername(usernameForBlock, true);
    }

    public boolean unblock(String usernameForUnBlock) throws DbException {
        boolean result = userDao.updateBlockedStatusByUsername(usernameForUnBlock, false);
        return !result;
    }
}