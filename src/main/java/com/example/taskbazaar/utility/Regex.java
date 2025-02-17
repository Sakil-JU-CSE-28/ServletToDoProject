package com.example.taskbazaar.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {

    public static boolean isValid(String input, String regexPattern) {
        if (input == null || regexPattern == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return isValid(password, passwordRegex);
    }
}
