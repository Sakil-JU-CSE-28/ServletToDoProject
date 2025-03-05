/*
 * author : Md. Sakil Ahmed
 * date : 21 feb 2024
 */
package com.example.taskbazaar.validation;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.utility.Constant;
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


    public void validateLogin(UserDTO user) throws ValidationException {
        validateCredentials(user,1);
    }

    public void validateRegistration(UserDTO user) throws ValidationException {
        validateCredentials(user,2);
    }

    private void validateCredentials(UserDTO user,int validationType) throws ValidationException {
        if (user == null) {
            throw new ValidationException("User cannot be null");
        }

        try {
            boolean isBlocked = userDao.checkIsBlocked(user.username());
            if (isBlocked) {
                logger.warn("Account is blocked for username: {}", user.username());
                throw new ValidationException(Constant.ACCOUNT_BLOCKED);
            }

            validateUsername(user.username());
            validatePassword(user,validationType);

        } catch (SQLException ex) {
            logger.error("Database error while validating user: {}", user.username(), ex);
            throw new ValidationException(Constant.Error.INTERNAL_ERROR);
        }
    }

    private void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            logger.warn("Invalid username: {}", username);
            throw new ValidationException(Constant.Error.USERNAME_ERROR);
        }
    }

    private void validatePassword(UserDTO user,int validationType) throws ValidationException {
        String password = user.password();
        String confirmPassword = user.confirmPassword();

        if (password == null || password.isEmpty()) {
            logger.warn("Password is empty for user: {}", user.username());
            throw new ValidationException(Constant.Error.PASSWORD_ERROR);
        }

        if(validationType==1){
            return;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            logger.warn("Confirm password is empty for user: {}", user.username());
            throw new ValidationException(Constant.Error.PASSWORD_ERROR);
        }

        if (!password.equals(confirmPassword)) {
            logger.warn("Password and confirm password do not match for user: {}", user.username());
            throw new ValidationException(Constant.PASSWORD_NOT_MATCH);
        }

        if (!confirmPassword.matches(Constant.PASSWORD_REGEX)) {
            logger.warn("Password confirmation does not meet the regex requirement for user: {}", user.username());
            throw new ValidationException(Constant.Error.REGEX_ERROR);
        }
    }
}