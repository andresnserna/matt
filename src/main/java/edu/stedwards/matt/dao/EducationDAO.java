package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Education;

public class EducationDAO {
    public List<Education> getForUser(int userId) {
        String sql = "SELECT education_id, user_id, institution, degree, gpa, start_date, end_date FROM education WHERE user_id = ? ORDER BY education_id";
        List<Education> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Education row = new Education();
                    row.setEducationID(rs.getInt("education_id"));
                    row.setUserID(rs.getInt("user_id"));
                    row.setInstitution(rs.getString("institution"));
                    row.setDegree(rs.getString("degree"));
                    row.setGpa(rs.getString("gpa"));
                    row.setStartDate(rs.getString("start_date"));
                    row.setEndDate(rs.getString("end_date"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading education: " + e.getMessage());
        }
        return rows;
    }

    public boolean save(Education edu) {
        String sql = "INSERT INTO education (user_id, institution, degree, gpa, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, edu.getUserID()); 
            pstmt.setString(2, edu.getInstitution());
            pstmt.setString(3, edu.getDegree());
            pstmt.setString(4, edu.getGpa());
            pstmt.setString(5, edu.getStartDate());
            pstmt.setString(6, edu.getEndDate());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
