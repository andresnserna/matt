package edu.stedwards.matt.services;

import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.entity.User;
import java.util.ArrayList;
import java.util.List;

/** Business rules for authentication, registration validation, and user onboarding state. */
public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this(new UserDAO());
    }

    UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return null;
        }
        return userDAO.findAuthenticatedUser(username.trim(), password);
    }

    /**
     * Validates sign-up fields against email format and password strength rules.
     * Returns an empty list if all inputs are valid.
     */
    public List<String> validateRegistration(String username, String email, String password) {
        List<String> errors = new ArrayList<>();
        if (username == null || username.isBlank()) {
            errors.add("Username is required.");
        }
        if (!ValidationService.isValidEmail(email)) {
            errors.add("Email must follow the format [username]@[domain].[tld] (e.g., alex@example.com).");
        }
        errors.addAll(ValidationService.validatePassword(password));
        return errors;
    }

    /**
     * Validates and registers a new user account in SQLite.
     * Throws IllegalArgumentException with all validation errors if email/password rules fail.
     */
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        List<String> errors = validateRegistration(username, email, password);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errors));
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPasswordHash(password); // UserDAO.saveAndGetId hashes this with SHA-256
        user.setFirstName(firstName == null ? "" : firstName.trim());
        user.setLastName(lastName == null ? "" : lastName.trim());
        user.setOnboarded(false);

        int id = userDAO.saveAndGetId(user);
        return id > 0 ? user : null;
    }

    public boolean isValidEmail(String email) {
        return ValidationService.isValidEmail(email);
    }

    public List<String> validatePassword(String password) {
        return ValidationService.validatePassword(password);
    }

    public boolean isOnboarded(int userId) {
        return userDAO.isOnboarded(userId);
    }

    public boolean markOnboarded(int userId) {
        return userDAO.markOnboarded(userId);
    }
}
