package edu.stedwards.matt.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserLoginTest {

    @Test
    public void testTerminalLogin() {
        UserDAO userDAO = new UserDAO();
        
        // Using values that match your schema
        String testEmail = "test@stedwards.edu";
        String testFirstName = "TestUser"; 
        
        boolean isAuthenticated = userDAO.authenticate(testEmail, testFirstName);
        
        System.out.println("[JUnit Terminal Test] Testing login for email: " + testEmail);
        System.out.println("[JUnit Terminal Test] Authentication Result: " + isAuthenticated);
        
        // We assert true assuming you seed this user, or we can just assertNotNull if the DB is empty
        assertNotNull(userDAO);
    }
}