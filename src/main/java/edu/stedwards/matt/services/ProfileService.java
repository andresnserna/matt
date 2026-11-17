/**
 * Architecture layer: service
 * Business logic for user-related operations such as validation and workflows.
 */
package edu.stedwards.matt.services;

import edu.stedwards.matt.dao.SkillDAO;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.User;
import java.util.Map;

/**
 * Contains user-focused business logic for the application.
 * This class is called by screens, controllers, or navigators when an action such as login,
 * update, or user lookup needs app-level rules applied before or after DAO access.
 * It is not responsible for raw SQL statements or JavaFX layout construction.
 */
public class ProfileService {

    private final UserDAO userDAO = new UserDAO();
    private final SkillDAO skillDAO = new SkillDAO();

    /** Retrieves a user profile by userId from SQLite. */
    public User getUserProfile(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userDAO.getUserById(userId);
    }

    /** Retrieves the active user from OnboardingSession, refreshing from SQLite if an ID exists. */
    public User getCurrentUserProfile() {
        User sessionUser = OnboardingSession.getInstance().getUserProfile();
        if (sessionUser != null && sessionUser.getUserID() > 0) {
            User dbUser = userDAO.getUserById(sessionUser.getUserID());
            if (dbUser != null) {
                OnboardingSession.getInstance().setUserProfile(dbUser);
                return dbUser;
            }
        }
        if (sessionUser == null) {
            sessionUser = new User();
            OnboardingSession.getInstance().setUserProfile(sessionUser);
        }
        return sessionUser;
    }

    /**
     * Day 5 Service Task: Supports saving a new user or updating all editable user fields
     * for an existing user, and keeps OnboardingSession in sync.
     */
    public boolean saveUserProfile(User user) {
        if (user == null) {
            return false;
        }

        boolean success;
        if (user.getUserID() > 0 && userDAO.getUserById(user.getUserID()) != null) {
            success = userDAO.updateUserProfile(user);
        } else {
            int generatedId = userDAO.saveAndGetId(user);
            success = generatedId > 0;
        }

        if (success) {
            OnboardingSession.getInstance().setUserProfile(user);
        }
        return success;
    }

    /**
     * Connects a Map of onboarding/parsed fields to the active User profile in OnboardingSession
     * and persists all user fields and skills to SQLite.
     */
    public boolean saveUserProfileFromMap(Map<String, String> profileData) {
        if (profileData == null || profileData.isEmpty()) {
            return false;
        }

        User user = getCurrentUserProfile();

        if (profileData.containsKey("First Name") || profileData.containsKey("Last Name")) {
            user.setFirstName(profileData.getOrDefault("First Name", user.getFirstName()));
            user.setLastName(profileData.getOrDefault("Last Name", user.getLastName()));
        } else if (profileData.containsKey("Full Name")) {
            String fullName = profileData.getOrDefault("Full Name", "").trim();
            String[] nameParts = fullName.split("\\s+", 2);
            user.setFirstName(nameParts.length > 0 ? nameParts[0] : "");
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        }

        if (profileData.containsKey("Email")) user.setEmail(profileData.get("Email"));
        if (profileData.containsKey("Phone")) user.setPhoneNumber(profileData.get("Phone"));
        if (profileData.containsKey("Address Line 1")) user.setAddressLine1(profileData.get("Address Line 1"));
        if (profileData.containsKey("Address Line 2")) user.setAddressLine2(profileData.get("Address Line 2"));
        if (profileData.containsKey("City")) user.setCity(profileData.get("City"));
        if (profileData.containsKey("State")) user.setState(profileData.get("State"));
        if (profileData.containsKey("Zip")) user.setZip(profileData.get("Zip"));
        if (profileData.containsKey("Summary")) user.setSummary(profileData.get("Summary"));
        if (profileData.containsKey("Ollama API Key")) user.setOllamaApiKey(profileData.get("Ollama API Key"));

        user.setOnboarded(true);
        boolean saved = saveUserProfile(user);

        if (saved && user.getUserID() > 0) {
            String rawSkills = profileData.getOrDefault("Skills", "");
            if (!rawSkills.isBlank()) {
                for (String skillName : rawSkills.split(",")) {
                    String trimmed = skillName.trim();
                    if (!trimmed.isEmpty()) {
                        Skill skill = new Skill();
                        skill.setUserID(user.getUserID());
                        skill.setSkillName(trimmed);
                        skillDAO.save(skill);
                    }
                }
            }
        }
        return saved;
    }

    /** Builds the formatted profile context string for AI prompts using TemplateService. */
    public String buildProfileContextString(int userId) {
        User user = getUserProfile(userId);
        if (user == null) {
            return "";
        }
        return new TemplateService().buildUserContext(user);
    }
}