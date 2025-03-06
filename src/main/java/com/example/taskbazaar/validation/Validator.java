/*
 * author : Md. Sakil Ahmed
 * date : 21 feb 2024
 */
package com.example.taskbazaar.validation;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.DbException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.utility.Common;
import com.example.taskbazaar.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Validator {

    private volatile static Validator instance = null;
    private final UserDao userDao = UserDao.getInstance();
    private final Logger logger = LoggerFactory.getLogger(Validator.class);

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


    public void validateLogin(UserDTO userDTO) throws ValidationException, DbException {
        if (Common.isBlank(userDTO.username())) {
            logger.warn("Invalid username: {}", userDTO.username());
            throw new ValidationException(Constants.Error.USERNAME_ERROR);
        }

        if (Common.isBlank(userDTO.password())) {
            logger.warn("Invalid password: {}", userDTO.password());
            throw new ValidationException(Constants.Error.PASSWORD_ERROR);
        }

        boolean isBlocked;
        try {
            isBlocked = userDao.checkIsBlocked(userDTO.username());
            if (isBlocked) {
                logger.warn("Account is blocked for username: {}", userDTO.username());
                throw new ValidationException(Constants.Constant.ACCOUNT_BLOCKED);
            }
        } catch (SQLException e) {
            throw new DbException(Constants.Error.INTERNAL_ERROR);
        }

    }

    public void validateRegistration(UserDTO userDTO) throws ValidationException, DbException, AuthenticationException {
        if (Common.isBlank(userDTO.password()) || Common.isBlank(userDTO.confirmPassword())) {
            logger.warn("Password is empty for user: {}", userDTO.username());
            throw new ValidationException(Constants.Error.PASSWORD_ERROR);
        }

        if (!userDTO.password().equals(userDTO.confirmPassword())) {
            logger.warn("Password and confirm password do not match for user: {}", userDTO.username());
            throw new ValidationException(Constants.Constant.PASSWORD_NOT_MATCH);
        }

        if (!userDTO.confirmPassword().matches(Constants.Constant.PASSWORD_REGEX)) {
            logger.warn("Password confirmation does not meet the regex requirement for user: {}", userDTO.username());
            throw new ValidationException(Constants.Error.REGEX_ERROR);
        }
        User user = userDao.getByUsername(userDTO.username());
        if (user != null) {
            throw new AuthenticationException(Constants.Constant.USER_EXISTS);
        }
    }
}