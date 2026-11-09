package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Education;

public class EducationDAO {
    public boolean save(Education edu) {
        // Updated to include display_order
        String sql = "INSERT INTO education (user_id, institution, degree, gpa, start_date, end_date, display_order) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, edu.getUserID()); 
            pstmt.setString(2, edu.getInstitution());
            pstmt.setString(3, edu.getDegree());
            pstmt.setString(4, edu.getGpa());
            pstmt.setString(5, edu.getStartDate());
            pstmt.setString(6, edu.getEndDate());
            pstmt.setInt(7, edu.getDisplayOrder());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}