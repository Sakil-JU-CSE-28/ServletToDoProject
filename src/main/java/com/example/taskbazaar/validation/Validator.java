/*
 * author : Md. Sakil Ahmed
 * date : 21 feb 2024
 */
package com.example.taskbazaar.validation;

import com.example.taskbazaar.dao.UserDao;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.utility.Constant;

import java.sql.SQLException;

public class Validator {

    private Validator() {
    }

    private static class Holder {
        private static final Validator INSTANCE = new Validator();
    }

    public static Validator getInstance() {
        return Holder.INSTANCE;
    }

    public void validateLogin(User user) throws ValidationException {
        if(user.getUsername() == null) throw new ValidationException("username is null");
        if(user.getPassword() == null) throw new ValidationException("password is null");
        try {
            boolean status = UserDao.getBlockStatusByUserName(user.getUsername());
            if (status) {
                throw new ValidationException("Account blocked!");
            }
        } catch (SQLException ex) {
            throw new ValidationException(Constant.INTERNAL_ERROR);
        }
    }

    public void validateRegistration(User user, String confirmPassword) throws ValidationException {
        if (confirmPassword.equals(user.getPassword())) {
            if (!confirmPassword.matches(Constant.PASSWORD_REGEX)) {
                throw new ValidationException("Password not follow regular password pattern");
            }
        } else {
            throw new ValidationException("Password and confirm password not match");
        }
    }
}