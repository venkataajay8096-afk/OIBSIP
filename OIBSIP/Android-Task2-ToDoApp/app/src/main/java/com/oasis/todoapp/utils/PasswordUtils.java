package com.oasis.todoapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing passwords securely using SHA-256.
 * Plain-text passwords are never stored directly in the database.
 */
public class PasswordUtils {

    /**
     * Hashes the given plain-text password using SHA-256.
     *
     * @param password Plain text password
     * @return Hexadecimal string representing the hashed password
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Convert byte array into Signum representation and then Hex format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Fallback for safety (should not happen for SHA-256)
            return String.valueOf(password.hashCode());
        }
    }

    /**
     * Verifies if a plain text password matches a hashed password.
     *
     * @param plainPassword  The input plain text password
     * @param hashedPassword The stored hash from the database
     * @return True if they match, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        String inputHash = hashPassword(plainPassword);
        return hashedPassword.equalsIgnoreCase(inputHash);
    }
}
