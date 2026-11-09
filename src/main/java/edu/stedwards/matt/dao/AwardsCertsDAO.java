package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.AwardsCerts;

public class AwardsCertsDAO {
    public boolean save(AwardsCerts award) {
        // Updated to match the new schema: date_issued and description
        String sql = "INSERT INTO awards_certs (user_id, title, issuer, date_issued, description) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, award.getUserID());
            pstmt.setString(2, award.getTitle());
            pstmt.setString(3, award.getIssuer());
            pstmt.setString(4, award.getDateIssued());
            pstmt.setString(5, award.getDescription());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving award: " + e.getMessage());
            return false;
        }
    }
}