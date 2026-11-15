package edu.stedwards.matt.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
            
            String rawOrHashedPassword = user.getPasswordHash() != null ? user.getPasswordHash() : "default_hash";
            String finalPasswordHash = isSha256Hash(rawOrHashedPassword) ? rawOrHashedPassword : hashPassword(rawOrHashedPassword);

            pstmt.setString(1, user.getUsername() != null ? user.getUsername() : "user_" + System.currentTimeMillis()); 
            pstmt.setString(2, finalPasswordHash);
            pstmt.setString(3, user.getFirstName() != null ? user.getFirstName() : "");
            pstmt.setString(4, user.getLastName() != null ? user.getLastName() : "");
            pstmt.setString(5, user.getEmail() != null ? user.getEmail() : "user_" + System.currentTimeMillis() + "@example.com");
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
                        user.setUserID(generatedId);
                        user.setPasswordHash(finalPasswordHash);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
        
        return generatedId;
    }

    /**
     * Day 5 Task: Updates all profile fields for an existing user in user_profile.
     */
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE user_profile SET first_name = ?, last_name = ?, email = ?, is_onboarded = ?, " +
                "phone = ?, address_line_1 = ?, address_line_2 = ?, city = ?, state = ?, zip = ?, " +
                "summary = ?, ollama_api_key = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.isOnboarded() ? 1 : 0);
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getAddressLine1());
            pstmt.setString(7, user.getAddressLine2());
            pstmt.setString(8, user.getCity());
            pstmt.setString(9, user.getState());
            pstmt.setString(10, user.getZip());
            pstmt.setString(11, user.getSummary());
            pstmt.setString(12, user.getOllamaApiKey());
            pstmt.setInt(13, user.getUserID());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    /**
     * Days 1-2 Task: Updates only the ollama_api_key for a user.
     */
    public boolean updateOllamaApiKey(int userId, String apiKey) {
        String sql = "UPDATE user_profile SET ollama_api_key = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, apiKey);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Error updating Ollama API key: " + e.getMessage());
            return false;
        }
    }

    public boolean authenticateUser(String username, String password) {
        return findAuthenticatedUser(username, password) != null;
    }

    public User findAuthenticatedUser(String username, String password) {
        String sql = "SELECT user_id, username, password_hash, first_name, last_name, email, is_onboarded, " +
                "phone, address_line_1, address_line_2, city, state, zip, summary, ollama_api_key " +
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
                
                // Supports both newly SHA-256 hashed passwords and legacy plain-text test rows
                if (storedHash == null || (!storedHash.equals(hashedInput) && !storedHash.equals(password))) {
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
                user.setOllamaApiKey(rs.getString("ollama_api_key"));
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            return null;
        }
    }

    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, first_name, last_name, email, is_onboarded, phone, " +
                "address_line_1, address_line_2, city, state, zip, summary, ollama_api_key " +
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
                user.setOllamaApiKey(rs.getString("ollama_api_key"));
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

    /**
     * Hashes a plain-text password using SHA-256 and returns a 64-character hex string.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(plainTextPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Checks if a string is already a 64-character hexadecimal SHA-256 hash.
     */
    private static boolean isSha256Hash(String value) {
        return value != null && value.matches("^[a-fA-F0-9]{64}$");
    }
}
