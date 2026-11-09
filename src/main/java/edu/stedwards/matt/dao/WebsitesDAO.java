package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Websites;

public class WebsitesDAO {
    public boolean save(Websites website) {
        String sql = "INSERT INTO websites (user_id, url_type, url) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, website.getUserID());
            pstmt.setString(2, website.getUrlType());
            pstmt.setString(3, website.getUrl());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}