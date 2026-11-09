package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Projects;

public class ProjectsDAO {
    public List<Projects> getForUser(int userId) {
        String sql = "SELECT project_id, user_id, title, description, url FROM projects WHERE user_id = ? ORDER BY project_id";
        List<Projects> rows = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Projects row = new Projects();
                    row.setProjectID(rs.getInt("project_id"));
                    row.setUserID(rs.getInt("user_id"));
                    row.setTitle(rs.getString("title"));
                    row.setDescription(rs.getString("description"));
                    row.setUrl(rs.getString("url"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading projects: " + e.getMessage());
        }
        return rows;
    }

    public boolean save(Projects project) {
        // Matches the new SQLite schema: user_id, title, description, url
        String sql = "INSERT INTO projects (user_id, title, description, url) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, project.getUserID()); 
            pstmt.setString(2, project.getTitle());
            pstmt.setString(3, project.getDescription());
            pstmt.setString(4, project.getUrl());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
