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
        String sql = "INSERT INTO user_profile (username, password_hash, first_name, last_name, email, is_onboarded, phone, address_line_1, address_line_2, city, state, zip, summary, ollama_api_key) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername() != null ? user.getUsername() : "user_" + System.currentTimeMillis()); 
            pstmt.setString(2, user.getPasswordHash() != null ? user.getPasswordHash() : "default_hash");
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getEmail());
            pstmt.setInt(6, user.isOnboarded() ? 1 : 0);
            pstmt.setString(7, user.getPhoneNumber());
            pstmt.setString(8, user.getAddressLine1());
            pstmt.setString(9, user.getAddressLine2());
            pstmt.setString(10, user.getCity());
            pstmt.setString(11, user.getState());
            pstmt.setString(12, user.getZip());
            pstmt.setString(13, user.getSummary());
            pstmt.setString(14, user.getOllamaApiKey());

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
        return findAuthenticatedUser(username, password) != null;
    }

    public User findAuthenticatedUser(String username, String password) {
        String sql = "SELECT user_id, username, password_hash, first_name, last_name, email, is_onboarded, " +
                "phone, address_line_1, address_line_2, city, state, zip, summary " +
                "FROM user_profile WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                String storedHash = rs.getString("password_hash");
                String hashedInput = hashPassword(password);
                if (storedHash == null || !storedHash.equals(hashedInput)) {
                    return null;
                }

                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(storedHash);
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setOnboarded(rs.getInt("is_onboarded") != 0);
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddressLine1(rs.getString("address_line_1"));
                user.setAddressLine2(rs.getString("address_line_2"));
                user.setCity(rs.getString("city"));
                user.setState(rs.getString("state"));
                user.setZip(rs.getString("zip"));
                user.setSummary(rs.getString("summary"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, first_name, last_name, email, is_onboarded, phone, " +
                "address_line_1, address_line_2, city, state, zip, summary " +
                "FROM user_profile WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setOnboarded(rs.getInt("is_onboarded") != 0);
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddressLine1(rs.getString("address_line_1"));
                user.setAddressLine2(rs.getString("address_line_2"));
                user.setCity(rs.getString("city"));
                user.setState(rs.getString("state"));
                user.setZip(rs.getString("zip"));
                user.setSummary(rs.getString("summary"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
            return null;
        }
    }

    public boolean isOnboarded(int userId) {
        String sql = "SELECT is_onboarded FROM user_profile WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt("is_onboarded") != 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking onboarding status: " + e.getMessage());
            return false;
        }
    }

    public boolean markOnboarded(int userId) {
        String sql = "UPDATE user_profile SET is_onboarded = 1 WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error updating onboarding status: " + e.getMessage());
            return false;
        }
    }

    // Temporary utility method until you bring in a real hashing library
    private String hashPassword(String plainTextPassword) {
        // For now, returning the plain text so your app doesn't break, 
        // but this is where you'll apply BCrypt or SHA-256!
        return plainTextPassword; 
    }
}
