package edu.stedwards.matt.controllers;

import java.io.IOException;
import java.util.Map;

import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.services.DocumentExtractionService;
import edu.stedwards.matt.services.ParserService;
import edu.stedwards.matt.services.ProfileService;

public class ProfileController {

    private final ParserService parserService;
    private final ProfileService profileService;

    public ProfileController() {
        this.parserService = new ParserService();
        this.profileService = new ProfileService();
    }

    public Map<String, String> uploadResumeFile(String filePath) {
        DocumentExtractionService docService = new DocumentExtractionService();
        try {
            String rawText = docService.extractText(filePath);
            return parseResumeText(rawText); // Passes the extracted text to your existing AI parser
        } catch (IOException e) {
            System.err.println("Upload failed: " + e.getMessage());
            return null;
        }
    }

    // 1. Send raw text to the AI parser and populate OnboardingSession's User profile
    public Map<String, String> parseResumeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            System.out.println("Controller Error: No text provided.");
            return null;
        }
        Map<String, String> parsedData = parserService.parseResume(rawText);
        if (parsedData != null && !parsedData.isEmpty()) {
            syncParsedDataToOnboardingSession(parsedData);
        }
        return parsedData;
    }

    /**
     * Day 5 Task: Syncs AI-parsed resume fields directly into OnboardingSession's User entity
     * so personal info is immediately available during onboarding and on MyInfoLayout.
     */
    private void syncParsedDataToOnboardingSession(Map<String, String> parsedData) {
        User sessionUser = OnboardingSession.getInstance().getUserProfile();
        if (sessionUser == null) {
            sessionUser = new User();
            OnboardingSession.getInstance().setUserProfile(sessionUser);
        }

        String fullName = parsedData.getOrDefault("Full Name", "").trim();
        if (!fullName.isEmpty()) {
            String[] parts = fullName.split("\\s+", 2);
            sessionUser.setFirstName(parts[0]);
            if (parts.length > 1) {
                sessionUser.setLastName(parts[1]);
            }
        }
        if (!parsedData.getOrDefault("Email", "").isBlank()) {
            sessionUser.setEmail(parsedData.get("Email").trim());
        }
        if (!parsedData.getOrDefault("Phone", "").isBlank()) {
            sessionUser.setPhoneNumber(parsedData.get("Phone").trim());
        }
    }

    // 2. Day 5 Task: Connect saveUserProfile(Map) to OnboardingSession and ProfileService
    public boolean saveUserProfile(Map<String, String> profileData) {
        return profileService.saveUserProfileFromMap(profileData);
    }

    // 3. Day 5 Task: Save or update a User entity directly (used by MyInfoLayout and Onboarding)
    public boolean saveUserProfile(User user) {
        return profileService.saveUserProfile(user);
    }

    // 4. Load the currently active User profile (from OnboardingSession / SQLite)
    public User getCurrentUserProfile() {
        return profileService.getCurrentUserProfile();
    }
}
