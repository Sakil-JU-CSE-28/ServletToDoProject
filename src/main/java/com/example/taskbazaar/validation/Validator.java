package com.example.taskbazaar.validation;

import com.example.taskbazaar.exception.ValidationException;
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

    public boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }
    public boolean validatePassword(String password) throws ValidationException {
        if(!Regex.isValidPassword(password)) {
            throw  new ValidationException("Invalid password");
        }
        return true;
    }
    public boolean validatePasswordConfirmation(String password,String confirmPassword) throws ValidationException {
          if(!password.equals(confirmPassword)) {
            throw new ValidationException("Password not match");
        }
        return true;
    }
}
