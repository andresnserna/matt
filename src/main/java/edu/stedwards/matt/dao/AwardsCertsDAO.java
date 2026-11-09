package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.AwardsCerts;

public class AwardsCertsDAO {
    public List<AwardsCerts> getForUser(int userId) {
        String sql = "SELECT award_id, user_id, title, issuer, date_issued, description FROM awards_certs WHERE user_id = ? ORDER BY award_id";
        List<AwardsCerts> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AwardsCerts row = new AwardsCerts();
                    row.setAwardID(rs.getInt("award_id"));
                    row.setUserID(rs.getInt("user_id"));
                    row.setTitle(rs.getString("title"));
                    row.setIssuer(rs.getString("issuer"));
                    row.setDateIssued(rs.getString("date_issued"));
                    row.setDescription(rs.getString("description"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading awards and certifications: " + e.getMessage());
        }
        return rows;
    }

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
