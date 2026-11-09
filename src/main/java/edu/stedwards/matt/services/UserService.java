package edu.stedwards.matt.services;

import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.entity.User;

/** Business rules for authentication and user onboarding state. */
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

    public boolean isOnboarded(int userId) {
        return userDAO.isOnboarded(userId);
    }

    public boolean markOnboarded(int userId) {
        return userDAO.markOnboarded(userId);
    }
}
