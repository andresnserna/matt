package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Skill;

public class SkillDAO {
    
    public boolean save(Skill skill) {
        String sql = "INSERT INTO skill (user_id, skill_name, proficiency_level) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, skill.getUserID());
            pstmt.setString(2, skill.getSkillName() != null ? skill.getSkillName().trim() : "");
            
            // Uses the actual proficiency level from the model, or defaults if empty
            String proficiency = skill.getProficiencyLevel();
            if (proficiency == null || proficiency.trim().isEmpty()) {
                proficiency = "Not Specified";
            }
            pstmt.setString(3, proficiency);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}