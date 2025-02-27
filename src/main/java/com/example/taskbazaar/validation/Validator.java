/*
 * author : Md. Sakil Ahmed
 * date : 21 feb 2024
 */
package com.example.taskbazaar.validation;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.utility.Constant;

import java.sql.SQLException;

public class Validator {

    private volatile static Validator instance = null;
    private UserDao userDao = UserDao.getInstance();

    private Validator() {
    }


    public static Validator getInstance() {
        if (instance == null) {
            synchronized (Validator.class) {
                if (instance == null) {
                    instance = new Validator();
                }
            }
        }
        return instance;
    }

    public void destroyInstance() {
        userDao = null;
        instance = null;
    }

    public void validateLogin(UserDTO user) throws ValidationException {
        if (user.username() == null || user.username().isEmpty())
            throw new ValidationException(Constant.USERNAME_ERROR);
        if (user.password() == null || user.password().isEmpty())
            throw new ValidationException(Constant.PASSWORD_ERROR);
        try {
            boolean status = userDao.getBlockStatusByUserName(user.username());
            if (status) {
                throw new ValidationException(Constant.ACCOUNT_BLOCKED);
            }
        } catch (SQLException ex) {
            throw new ValidationException(Constant.INTERNAL_ERROR);
        }
    }

    public void validateRegistration(UserDTO user) throws ValidationException {
        if (user.confirmPassword().equals(user.password())) {
            if (!user.confirmPassword().matches(Constant.PASSWORD_REGEX)) {
                throw new ValidationException(Constant.REGEX_ERROR);
            }
        } else {
            throw new ValidationException(Constant.PASSWORD_NOT_MATCH);
        }
    }
}