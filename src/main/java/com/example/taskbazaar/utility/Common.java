/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Common {

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null) {
            throw new IllegalArgumentException("Hex string cannot be null");
        }
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }

    public static byte[] generateSalt() {
        // Create a secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate a 16-byte salt
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return salt;
    }

    public static String hashPassword(String password, String storedSalt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] salt = hexToBytes(storedSalt);
        // Add the salt to the password
        digest.update(salt);

        // Hash the password with the salt
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Convert the hashed bytes to a hex string
        return bytesToHex(hashedBytes);
    }

    public static boolean isBlank(String string) {
        return string == null || string.trim().isEmpty();
    }
}