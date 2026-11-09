package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Experience;

public class ExperienceDAO {
    public boolean save(Experience exp) {
        // Updated to include display_order
        String sql = "INSERT INTO experience (user_id, company_name, job_title, description, start_date, end_date, display_order) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, exp.getUserID());
            pstmt.setString(2, exp.getCompanyName());
            pstmt.setString(3, exp.getJobTitle());
            pstmt.setString(4, exp.getDescription());
            pstmt.setString(5, exp.getStartDate());
            pstmt.setString(6, exp.getEndDate());
            pstmt.setInt(7, exp.getDisplayOrder());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}