package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Skill;

public class SkillDAO {
    public List<Skill> getForUser(int userId) {
        String sql = "SELECT skill_id, user_id, skill_name, proficiency_level FROM skill WHERE user_id = ? ORDER BY skill_id";
        List<Skill> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Skill row = new Skill();
                    row.setSkillID(rs.getInt("skill_id"));
                    row.setUserID(rs.getInt("user_id"));
                    row.setSkillName(rs.getString("skill_name"));
                    row.setProficiencyLevel(rs.getString("proficiency_level"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading skills: " + e.getMessage());
        }
        return rows;
    }

    
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
