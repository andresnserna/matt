/**
 * Architecture layer: model
 * Data-access component that persists and retrieves app settings.
 */
package edu.stedwards.matt.dao;

import edu.stedwards.matt.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles raw database access for app settings and preferences stored on user_profile.
 * This class is called by the service/UI layer whenever settings need to be loaded, saved,
 * or updated.
 */
public class SettingsDAO {

    /** Query and return the saved Ollama model name for this user. */
    public String getOllamaModelName(int userId) {
        return querySingleColumn(
                "SELECT ollama_model FROM user_profile WHERE user_id = ?",
                userId,
                "ollama_model"
        );
    }

    /** Persist the selected Ollama model name for this user. */
    public void setOllamaModelName(int userId, String modelName) {
        updateSingleColumn(
                "UPDATE user_profile SET ollama_model = ? WHERE user_id = ?",
                userId,
                modelName
        );
    }

    /** Query and return the saved downloads directory for this user. */
    public String getOllamaDownloadsLocation(int userId) {
        return querySingleColumn(
                "SELECT download_path FROM user_profile WHERE user_id = ?",
                userId,
                "download_path"
        );
    }

    /** Persist the downloads directory for this user. */
    public void setOllamaDownloadsLocation(int userId, String path) {
        updateSingleColumn(
                "UPDATE user_profile SET download_path = ? WHERE user_id = ?",
                userId,
                path
        );
    }

    /** Query and return the last saved Ollama status for this user. */
    public String getOllamaStatus(int userId) {
        return querySingleColumn(
                "SELECT ollama_status FROM user_profile WHERE user_id = ?",
                userId,
                "ollama_status"
        );
    }

    /** Persist the latest locally detected Ollama status for this user. */
    public void updateOllamaStatus(int userId, String status) {
        updateSingleColumn(
                "UPDATE user_profile SET ollama_status = ? WHERE user_id = ?",
                userId,
                status
        );
    }

    private String querySingleColumn(String sql, int userId, String columnName) {
        if (userId <= 0) {
            return null;
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(columnName);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading setting (" + columnName + ") for user " + userId + ": " + e.getMessage());
        }
        return null;
    }

    private void updateSingleColumn(String sql, int userId, String value) {
        if (userId <= 0) {
            return;
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (value == null || value.isBlank()) {
                pstmt.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(1, value.trim());
            }
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving setting for user " + userId + ": " + e.getMessage());
        }
    }
}