package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;

public class SkillDAO {
    
    public boolean insertSkill(int userId, String skillName) {
        String sql = "INSERT INTO skill (user_id, skill_name, proficiency_level) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setString(2, skillName.trim());
            pstmt.setString(3, "Not Specified"); // Default proficiency

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
