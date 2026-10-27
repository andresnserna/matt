package edu.stedwards.matt.dao;

import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.db.DatabaseManager; // Imported your DB manager!
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TemplateDAO {

    public boolean insertTemplate(Template template) {
        String sql = "INSERT INTO template (folder_id, user_id, template_name, template_type, file_loc) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Handle the nullable folder_id safely
            if (template.getFolderId() != null) {
                pstmt.setInt(1, template.getFolderId());
            } else {
                pstmt.setNull(1, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(2, template.getUserId());
            pstmt.setString(3, template.getTemplateName());
            pstmt.setString(4, template.getTemplateType());
            pstmt.setString(5, template.getFileLoc());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving template to database: " + e.getMessage());
            return false;
        }
    }
}