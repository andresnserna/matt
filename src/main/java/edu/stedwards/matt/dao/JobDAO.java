package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.JobPosting;

public class JobDAO {

    // 1. Create a new job posting
    public boolean insertJob(JobPosting job) {
        String sql = "INSERT INTO job_posting (user_id, company_id, company_name, job_title, location, job_url, job_description, raw_text, normalized_text, match_score, spam_score, status, date_added) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, job.getUserID());
            pstmt.setInt(2, job.getCompanyID());
            pstmt.setString(3, job.getCompanyName());
            pstmt.setString(4, job.getJobTitle());
            pstmt.setString(5, job.getLocation());
            pstmt.setString(6, job.getJobUrl());
            pstmt.setString(7, job.getJobDescription());
            pstmt.setString(8, job.getRawText());
            pstmt.setString(9, job.getNormalizedText());
            pstmt.setDouble(10, job.getMatchScore());
            pstmt.setDouble(11, job.getSpamScore());
            pstmt.setString(12, job.getStatus());
            pstmt.setString(13, job.getDateAdded());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error inserting job: " + e.getMessage());
            return false;
        }
    }

    // 2. Fetch a job by its ID
    public JobPosting getJobById(int jobId) {
        String sql = "SELECT * FROM job_posting WHERE jobpost_id = ?";
        JobPosting job = null;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                job = mapResultSetToJobPosting(rs);
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching job by ID: " + e.getMessage());
        }
        return job;
    }

    // 3. Fetch recent jobs for a specific user
    public List<JobPosting> getRecentJobsForUser(int userId, int limit) {
        String sql = "SELECT * FROM job_posting WHERE user_id = ? ORDER BY date_added DESC LIMIT ?";
        List<JobPosting> jobs = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                jobs.add(mapResultSetToJobPosting(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Error fetching recent jobs: " + e.getMessage());
        }
        return jobs;
    }

    // Helper method to keep code clean and avoid repeating the mapping logic
    private JobPosting mapResultSetToJobPosting(ResultSet rs) throws SQLException {
        JobPosting job = new JobPosting();
        job.setJobpostID(rs.getInt("jobpost_id"));
        job.setUserID(rs.getInt("user_id"));
        job.setCompanyID(rs.getInt("company_id"));
        job.setCompanyName(rs.getString("company_name"));
        job.setJobTitle(rs.getString("job_title"));
        job.setLocation(rs.getString("location"));
        job.setJobUrl(rs.getString("job_url"));
        job.setJobDescription(rs.getString("job_description"));
        job.setRawText(rs.getString("raw_text"));
        job.setNormalizedText(rs.getString("normalized_text"));
        job.setMatchScore(rs.getDouble("match_score"));
        job.setSpamScore(rs.getDouble("spam_score"));
        job.setStatus(rs.getString("status"));
        job.setDateAdded(rs.getString("date_added"));
        return job;
    }
}
