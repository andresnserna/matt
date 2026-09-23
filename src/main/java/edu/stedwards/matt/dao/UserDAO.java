package edu.stedwards.matt.dao;

import edu.stedwards.matt.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}
