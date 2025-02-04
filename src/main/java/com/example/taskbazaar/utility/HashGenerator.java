package com.example.taskbazaar.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.taskbazaar.utility.HexByteConverter.bytesToHex;

public class HashGenerator {
    public static String hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Add the salt to the password
        digest.update(salt);

        // Hash the password with the salt
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Convert the hashed bytes to a hex string
        return bytesToHex(hashedBytes);
    }
}
