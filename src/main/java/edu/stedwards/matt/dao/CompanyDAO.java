package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.Company;

public class CompanyDAO {
    
    public boolean save(Company company) {
        String sql = "INSERT INTO company (company_name, industry, description, website_url) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, company.getCompanyName());
            pstmt.setString(2, company.getIndustry());
            pstmt.setString(3, company.getDescription());
            pstmt.setString(4, company.getWebsiteUrl());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}