package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.User;

public class UserDAO {

    public int saveAndGetId(User user) {
        String sql = "INSERT INTO user_profile (username, password_hash, first_name, last_name, email, phone, address_line_1, address_line_2, city, state, zip, summary, ollama_api_key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername() != null ? user.getUsername() : "user_" + System.currentTimeMillis()); 
            pstmt.setString(2, user.getPasswordHash() != null ? user.getPasswordHash() : "default_hash");
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setString(6, user.getPhoneNumber());
            pstmt.setString(7, user.getAddressLine1());
            pstmt.setString(8, user.getAddressLine2());
            pstmt.setString(9, user.getCity());
            pstmt.setString(10, user.getState());
            pstmt.setString(11, user.getZip());
            pstmt.setString(12, user.getSummary());
            pstmt.setString(13, user.getOllamaApiKey());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
        
        return generatedId;
    }

    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password_hash FROM user_profile WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    
                    // TODO: Replace with your actual hashing utility (e.g., BCrypt.checkpw(password, storedHash))
                    String hashedInput = hashPassword(password); 
                    
                    return storedHash != null && storedHash.equals(hashedInput);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return false;
    }

    // Temporary utility method until you bring in a real hashing library
    private String hashPassword(String plainTextPassword) {
        // For now, returning the plain text so your app doesn't break, 
        // but this is where you'll apply BCrypt or SHA-256!
        return plainTextPassword; 
    }
}