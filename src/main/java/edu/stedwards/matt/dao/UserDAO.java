/**
 * Architecture layer: model
 * Data-access component that persists and retrieves User records.
 */
package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.User;

/**
 * Handles raw database access for User records.
 * This class is called by the service layer when app logic needs to read or write user data,
 * and it should expose narrow repository methods such as find, save, update, and delete.
 * It is not responsible for UI work, validation rules, or orchestration of user flows.
 */


public class UserDAO {

    public void printAllUsers() {
        String sql = "SELECT * FROM user_profile";
        
        // The try-with-resources block automatically closes the connection when finished
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.println("Found user: " + rs.getString("first_name"));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Find user by ID 
    public User findById(int userId) {
        String sql = "SELECT * FROM user_profile WHERE user_id = ?";
        User user = null;

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserID(rs.getInt("user_id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setSummary(rs.getString("summary"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean authenticate(String email, String passwordHash) {
        String sql = "SELECT * FROM user_profile WHERE email = ? AND password_hash = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, passwordHash);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next(); // Returns true if a match is found
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Creates a new user 
    public boolean save(User user) {
        String sql = "INSERT INTO user_profile (first_name, last_name, email, phone_number, summary) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getSummary());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing user 
    public boolean update(User user) {
        String sql = "UPDATE user_profile SET first_name = ?, last_name = ?, email = ?, phone_number = ?, summary = ? WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getSummary());
            pstmt.setInt(6, user.getUserID());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Delete a user record
    public boolean delete(int userId) {
        String sql = "DELETE FROM user_profile WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    
}