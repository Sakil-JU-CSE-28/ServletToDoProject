package com.example.taskbazaar.validation;

import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.model.User;
import com.example.taskbazaar.utility.Regex;

public class Validator {

    private Validator() {
    }

    private static class Holder {
        private static final Validator INSTANCE = new Validator();
    }

    public static Validator getInstance() {
        return Holder.INSTANCE;
    }

    public boolean validateLogin(User user) {
        return user.getUsername() != null && user.getPassword() != null;
    }
    public boolean validateRegistration(User user,String confirmPassword) throws ValidationException {
        if(confirmPassword.equals(user.getPassword())) {
            if(!Regex.isValidPassword(user.getPassword())) {
                throw new ValidationException("Password contains invalid characters");
            }
            return true;
        }
        else{
            throw new ValidationException("Password not match");
        }
    }
}
