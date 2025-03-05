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


    public UserDTO getUser(String username) throws DbException {
        User retrievedUser = userDao.getByUsername(username);
        UserDTO user = null;
        if (retrievedUser != null) {
            user = new UserDTO(retrievedUser.getUsername(),
                    retrievedUser.getPassword(), null,
                    retrievedUser.getRole(), retrievedUser.getSalt(),
                    retrievedUser.isBlocked());
        }
        return user;
    }

    public boolean insert(UserDTO user) throws DbException {
        return userDao.insert(user);
    }

    public List<UserDTO> getUsers() throws DbException {
        return userDao.getAll();
    }

    public boolean blockUser(String usernameForBlock) throws DbException {
        return userDao.updateBlockedStatusByUsername(usernameForBlock, true);
    }

    public boolean unBlockUser(String usernameForUnBlock) throws DbException {
        boolean result = userDao.updateBlockedStatusByUsername(usernameForUnBlock, false);
        return !result;
    }
}