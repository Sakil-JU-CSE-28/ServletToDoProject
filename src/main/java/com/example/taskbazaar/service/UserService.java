/*
 * author : Md. Sakil Ahmed
 * Date : 21 feb 2024
 */
package com.example.taskbazaar.service;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.DbException;
import java.util.List;

public class UserService {
    private static volatile UserService userService = null;
    private final UserDao userDao = UserDao.getInstance();

    private UserService() {}

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

    public String getRole(String username) throws DbException {
        UserDTO user = userDao.getDetailsByUsername(username);
        String role = user.role();
        return role;
    }

    public List<UserDTO> getUsers() throws DbException {
        List<UserDTO> users = userDao.getAll();
        return users;
    }

    public boolean blockUser(String usernameForBlock) throws DbException {
        return userDao.updateBlockedStatusByUsername(usernameForBlock, true);
    }

    public boolean unBlockUser(String usernameForUnBlock) throws DbException {
        boolean result = userDao.updateBlockedStatusByUsername(usernameForUnBlock, false);
        return !result ? true : false;
    }
}
