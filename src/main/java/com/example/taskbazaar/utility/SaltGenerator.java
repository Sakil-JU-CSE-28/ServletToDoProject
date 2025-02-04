package com.example.taskbazaar.utility;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SaltGenerator {
    public static byte[] generateSalt() throws NoSuchAlgorithmException {
        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate a 16-byte salt
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }
}
