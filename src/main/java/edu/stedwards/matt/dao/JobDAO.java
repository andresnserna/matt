package edu.stedwards.matt.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.JobPosting;

public class JobDAO {

    // 1. Create a new job posting
    public boolean insertJob(JobPosting job) {
        String sql = "INSERT INTO job_posting (user_id, company_id, company_name, job_title, location, job_url, pay_min, pay_max, pay_period, source_file_name, responsibilities, requirements, nice_to_have, benefits, raw_text, normalized_text, match_score, spam_score, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, job.getUserId());
            pstmt.setInt(2, job.getCompanyId());
            pstmt.setString(3, job.getCompanyName());
            pstmt.setString(4, job.getJobTitle());
            pstmt.setString(5, job.getLocation());
            pstmt.setString(6, job.getJobUrl());
            pstmt.setDouble(7, job.getPayMin());
            pstmt.setDouble(8, job.getPayMax());
            pstmt.setString(9, job.getPayPeriod());
            pstmt.setString(10, job.getSourceFileName());
            pstmt.setString(11, job.getResponsibilities());
            pstmt.setString(12, job.getRequirements());
            pstmt.setString(13, job.getNiceToHave());
            pstmt.setString(14, job.getBenefits());
            pstmt.setString(15, job.getRawText());
            pstmt.setString(16, job.getNormalizedText());
            pstmt.setDouble(17, job.getMatchScore());
            pstmt.setDouble(18, job.getSpamScore());
            pstmt.setString(19, job.getStatus());
            // Note: date_added is omitted from the INSERT because SQLite handles DEFAULT CURRENT_TIMESTAMP automatically
            
            int rowsAffected = pstmt.executeUpdate();

            // Populate the entity with the generated database id so callers can reference the saved record.
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    job.setJobpostId(generatedKeys.getInt(1));
                    System.out.println("[JobDAO] Inserted job_posting row with jobpost_id=" + job.getJobpostId()
                            + ", title='" + job.getJobTitle() + "'.");
                } else {
                    System.out.println("[JobDAO] Insert succeeded but SQLite returned no generated jobpost_id.");
                }
            }

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
        System.out.println("[JobDAO] Loading job_posting row for jobpost_id=" + jobId);
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, jobId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                job = mapResultSetToJobPosting(rs);
                System.out.println("[JobDAO] Row found: id=" + job.getJobpostId()
                        + ", title=" + job.getJobTitle()
                        + ", company=" + job.getCompanyName()
                        + ", date_added=" + job.getDateAdded());
            } else {
                System.out.println("[JobDAO] No job_posting row found for jobpost_id=" + jobId);
            }
            
        } catch (SQLException e) {
            System.out.println("[JobDAO] Error fetching job id=" + jobId + ": " + e.getMessage());
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


    // 4. Update an existing job posting (Confirm Fields -> save edits)
    public boolean updateJob(JobPosting job) {
        String sql = "UPDATE job_posting SET company_name = ?, job_title = ?, location = ?, job_url = ?, pay_min = ?, pay_max = ?, pay_period = ?, responsibilities = ?, requirements = ?, nice_to_have = ?, benefits = ?, raw_text = ?, normalized_text = ?, match_score = ?, spam_score = ?, status = ? WHERE jobpost_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, job.getCompanyName());
            pstmt.setString(2, job.getJobTitle());
            pstmt.setString(3, job.getLocation());
            pstmt.setString(4, job.getJobUrl());
            pstmt.setDouble(5, job.getPayMin());
            pstmt.setDouble(6, job.getPayMax());
            pstmt.setString(7, job.getPayPeriod());
            pstmt.setString(8, job.getResponsibilities());
            pstmt.setString(9, job.getRequirements());
            pstmt.setString(10, job.getNiceToHave());
            pstmt.setString(11, job.getBenefits());
            pstmt.setString(12, job.getRawText());
            pstmt.setString(13, job.getNormalizedText());
            pstmt.setDouble(14, job.getMatchScore());
            pstmt.setDouble(15, job.getSpamScore());
            pstmt.setString(16, job.getStatus());
            pstmt.setInt(17, job.getJobpostId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating job: " + e.getMessage());
            return false;
        }
    }

    // Helper method to keep code clean and map the entire new schema
    private JobPosting mapResultSetToJobPosting(ResultSet rs) throws SQLException {
        JobPosting job = new JobPosting();
        job.setJobpostId(rs.getInt("jobpost_id"));
        job.setUserId(rs.getInt("user_id"));
        job.setCompanyId(rs.getInt("company_id"));
        job.setCompanyName(rs.getString("company_name"));
        job.setJobTitle(rs.getString("job_title"));
        job.setLocation(rs.getString("location"));
        job.setJobUrl(rs.getString("job_url"));
        
        // New Pay Fields
        job.setPayMin(rs.getDouble("pay_min"));
        job.setPayMax(rs.getDouble("pay_max"));
        job.setPayPeriod(rs.getString("pay_period"));
        job.setSourceFileName(rs.getString("source_file_name"));
        
        // New Breakdown Fields
        job.setResponsibilities(rs.getString("responsibilities"));
        job.setRequirements(rs.getString("requirements"));
        job.setNiceToHave(rs.getString("nice_to_have"));
        job.setBenefits(rs.getString("benefits"));
        
        job.setRawText(rs.getString("raw_text"));
        job.setNormalizedText(rs.getString("normalized_text"));
        job.setMatchScore(rs.getDouble("match_score"));
        job.setSpamScore(rs.getDouble("spam_score"));
        job.setStatus(rs.getString("status"));
        job.setDateAdded(rs.getString("date_added"));
        
        return job;
    }
}
