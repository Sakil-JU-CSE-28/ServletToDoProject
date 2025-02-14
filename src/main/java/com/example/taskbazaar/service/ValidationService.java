package com.example.taskbazaar.service;

public class ValidationService {

    private ValidationService() {
    }
    // Static inner class for lazy initialization
    private static class Holder {
        private static final ValidationService INSTANCE = new ValidationService();
    }
    // Public method to get the instance
    public static ValidationService getInstance() {
        return Holder.INSTANCE;
    }

    public boolean validateUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }
    public boolean validatePassword(String password,String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
