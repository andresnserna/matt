package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Projects;

public class ProjectsDAO {
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