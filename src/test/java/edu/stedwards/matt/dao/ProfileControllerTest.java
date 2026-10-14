package edu.stedwards.matt.dao;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.stedwards.matt.controllers.ProfileController;
import edu.stedwards.matt.db.DatabaseManager;

public class ProfileControllerTest {

    @Test
    public void testSaveUserProfile() {
        // 1. Ensure the table exists in the test database environment
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS user_profile (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "password_hash TEXT NOT NULL DEFAULT '', " +
                    "first_name TEXT NOT NULL, " +
                    "last_name TEXT NOT NULL, " +
                    "email TEXT, " +
                    "phone TEXT, " +
                    "summary TEXT)";
            stmt.execute(createTableSQL);
            
            String createSkillTableSQL = "CREATE TABLE IF NOT EXISTS skill (" +
                    "skill_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "skill_name TEXT NOT NULL, " +
                    "proficiency_level TEXT)";
            stmt.execute(createSkillTableSQL);
        } catch (Exception e) {
            System.err.println("Test Setup Error: " + e.getMessage());
        }

        // 2. Create simulated AI output
        Map<String, String> dummyData = new HashMap<>();
        dummyData.put("Full Name", "Ada Lovelace");
        dummyData.put("Email", "ada@example.com");
        dummyData.put("Skills", "Java, SQLite, Object-Oriented Programming");

        // 3. Call your controller
        ProfileController controller = new ProfileController();
        boolean isSaved = controller.saveUserProfile(dummyData);

        // 4. Assert and print outcome
        if (!isSaved) {
            System.err.println("SAVE FAILED: Check your UserDAO.saveAndGetId SQL statement!");
        }
        
        assertTrue(isSaved, "The profile should be successfully saved to the database.");
    }
}