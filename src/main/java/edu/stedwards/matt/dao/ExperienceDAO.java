package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Experience;

public class ExperienceDAO {
    public List<Experience> getForUser(int userId) {
        String sql = "SELECT experience_id, user_id, job_title, company_name, start_date, end_date, description, display_order FROM experience WHERE user_id = ? ORDER BY display_order, experience_id";
        List<Experience> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Experience row = new Experience();
                    row.setExperienceID(rs.getInt("experience_id"));
                    row.setUserID(rs.getInt("user_id"));
                    row.setJobTitle(rs.getString("job_title"));
                    row.setCompanyName(rs.getString("company_name"));
                    row.setStartDate(rs.getString("start_date"));
                    row.setEndDate(rs.getString("end_date"));
                    row.setDescription(rs.getString("description"));
                    row.setDisplayOrder(rs.getInt("display_order"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading experience: " + e.getMessage());
        }
        return rows;
    }

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
