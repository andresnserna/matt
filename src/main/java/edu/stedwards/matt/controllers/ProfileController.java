package edu.stedwards.matt.controllers;

import java.io.IOException;
import java.util.Map;

import edu.stedwards.matt.dao.SkillDAO;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.services.DocumentExtractionService;
import edu.stedwards.matt.services.ParserService;

public class ProfileController {
    
    private final ParserService parserService;
    private final UserDAO userDAO;

    public ProfileController() {
        this.parserService = new ParserService();
        this.userDAO = new UserDAO();
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

    // 1. Send raw text to the AI parser
    public Map<String, String> parseResumeText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            System.out.println("Controller Error: No text provided.");
            return null;
        }
        return parserService.parseResume(rawText);
    }

    // 2. Save verified fields to the SQLite database
    public boolean saveUserProfile(Map<String, String> profileData) {
        User user = new User();
        
        String fullName = profileData.getOrDefault("Full Name", "");
        String[] nameParts = fullName.split(" ", 2);
        
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        user.setEmail(profileData.getOrDefault("Email", ""));
        user.setPhoneNumber(""); 
        user.setSummary("");
        
        // 1. Save user and get their new ID
        int newUserId = userDAO.saveAndGetId(user);
        
        if (newUserId != -1) {
            // 2. Parse the comma-separated skills and save them individually
            SkillDAO skillDAO = new SkillDAO();
            String rawSkills = profileData.getOrDefault("Skills", "");
            
            if (!rawSkills.isBlank()) {
                String[] skillArray = rawSkills.split(",");
                for (String skill : skillArray) {
                    skillDAO.insertSkill(newUserId, skill);
                }
            }
            return true;
        }
        
        return false; 
    }
    
}