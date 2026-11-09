package edu.stedwards.matt.dao;

import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.db.DatabaseManager; // Imported your DB manager!
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TemplateDAO {

    public boolean insertTemplate(Template template) {
        String sql = "INSERT INTO template (folder_id, user_id, template_name, file_loc) VALUES (?, ?, ?, ?)";
        
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
            pstmt.setString(4, template.getFileLoc());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving template to database: " + e.getMessage());
            return false;
        }
    }

    /** Update an existing template owned by the given user. */
    public boolean updateTemplate(Template template) {
        String sql = "UPDATE template SET template_name = ?, file_loc = ? WHERE template_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, template.getTemplateName());
            pstmt.setString(2, template.getFileLoc());
            pstmt.setInt(3, template.getTemplateId());
            pstmt.setInt(4, template.getUserId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating template in database: " + e.getMessage());
            return false;
        }
    }

    /** Fetch template buttons available to a user from the template table. */
    public List<Template> getTemplatesForUser(int userId) {
        String sql = "SELECT template_id, folder_id, user_id, template_name, file_loc FROM template WHERE user_id = ?";
        List<Template> templates = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Template t = new Template();
                    t.setTemplateId(rs.getInt("template_id"));
                    t.setFolderId(rs.getInt("folder_id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setTemplateName(rs.getString("template_name"));
                    t.setFileLoc(rs.getString("file_loc"));
                    templates.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching templates for user: " + e.getMessage());
        }
        return templates;
    }

    /** Load a template only when it belongs to the supplied user. */
    public Template getTemplateById(int templateId, int userId) {
        String sql = "SELECT template_id, folder_id, user_id, template_name, file_loc FROM template WHERE template_id = ? AND user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, templateId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                Template template = new Template();
                template.setTemplateId(rs.getInt("template_id"));
                int folderId = rs.getInt("folder_id");
                template.setFolderId(rs.wasNull() ? null : folderId);
                template.setUserId(rs.getInt("user_id"));
                template.setTemplateName(rs.getString("template_name"));
                template.setFileLoc(rs.getString("file_loc"));
                return template;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching template: " + e.getMessage());
            return null;
        }
    }
}
