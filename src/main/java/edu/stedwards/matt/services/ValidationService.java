package edu.stedwards.matt.services;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ValidationService {

    // Enforces [username]@[Second-level domain].[Top-Level Domain]
    private static final Pattern STRICT_EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,}$");

    private static final int MIN_PASSWORD_LENGTH = 8;

    public static boolean isValidEmail(String email) {
        return email != null && STRICT_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Returns an empty list if the password meets all requirements,
     * or a list of human-readable error messages to display in the UI.
     */
    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            errors.add("Must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        if (password == null || !password.matches(".*[A-Z].*")) {
            errors.add("Must include at least one uppercase letter.");
        }
        if (password == null || !password.matches(".*[a-z].*")) {
            errors.add("Must include at least one lowercase letter.");
        }
        if (password == null || !password.matches(".*\\d.*")) {
            errors.add("Must include at least one number.");
        }
        if (password == null || !password.matches(".*[^A-Za-z0-9\\s].*")) {
            errors.add("Must include at least one special character (e.g., !@#$%^&*).");
        }
        if (password != null && password.matches(".*\\s.*")) {
            errors.add("Must not contain spaces.");
        }
        return errors;
    }

    public static boolean isValidPassword(String password) {
        return validatePassword(password).isEmpty();
    }
}