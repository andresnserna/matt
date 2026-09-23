package edu.stedwards.matt.services;

/**
 * Contains user-focused business logic for the application.
 * This class is called by screens, controllers, or navigators when an action such as login,
 * update, or user lookup needs app-level rules applied before or after DAO access.
 * It is not responsible for raw SQL statements or JavaFX layout construction.
 */

import edu.stedwards.matt.models.User;

public interface ProfileService {
    User getUserProfile(int userId);
    boolean saveUserProfile(User user);
    String buildProfileContextString(int userId);
}
