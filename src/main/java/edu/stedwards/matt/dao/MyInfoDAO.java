package edu.stedwards.matt.dao;

import edu.stedwards.matt.db.DatabaseManager;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Loads and updates a user's résumé sections for the My Info screen. */
public class MyInfoDAO {

    public List<Education> getEducationForUser(int userId) {
        List<Education> list = new ArrayList<>();
        String sql = "SELECT institution, degree, start_date, end_date, gpa FROM education WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Education item = new Education();
                    item.setUserID(userId);
                    item.setInstitution(rs.getString("institution"));
                    item.setDegree(rs.getString("degree"));
                    item.setStartDate(rs.getString("start_date"));
                    item.setEndDate(rs.getString("end_date"));
                    item.setGpa(rs.getString("gpa"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading education: " + e.getMessage());
        }
        return list;
    }

    public void replaceEducationForUser(int userId, List<Education> items) {
        String deleteSql = "DELETE FROM education WHERE user_id = ?";
        String insertSql = "INSERT INTO education (user_id, institution, degree, start_date, end_date, gpa) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Education item : items) {
                    if (isBlank(item.getInstitution()) && isBlank(item.getDegree())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, safe(item.getInstitution()));
                    ins.setString(3, safe(item.getDegree()));
                    ins.setString(4, safe(item.getStartDate()));
                    ins.setString(5, safe(item.getEndDate()));
                    ins.setString(6, safe(item.getGpa()));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving education: " + e.getMessage());
        }
    }

    public List<Experience> getExperienceForUser(int userId) {
        List<Experience> list = new ArrayList<>();
        String sql = "SELECT job_title, company_name, start_date, end_date, description FROM experience WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Experience item = new Experience();
                    item.setUserID(userId);
                    item.setJobTitle(rs.getString("job_title"));
                    item.setCompanyName(rs.getString("company_name"));
                    item.setStartDate(rs.getString("start_date"));
                    item.setEndDate(rs.getString("end_date"));
                    item.setDescription(rs.getString("description"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading experience: " + e.getMessage());
        }
        return list;
    }

    public void replaceExperienceForUser(int userId, List<Experience> items) {
        String deleteSql = "DELETE FROM experience WHERE user_id = ?";
        String insertSql = "INSERT INTO experience (user_id, job_title, company_name, start_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Experience item : items) {
                    if (isBlank(item.getJobTitle()) && isBlank(item.getCompanyName())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, safe(item.getJobTitle()));
                    ins.setString(3, safe(item.getCompanyName()));
                    ins.setString(4, safe(item.getStartDate()));
                    ins.setString(5, safe(item.getEndDate()));
                    ins.setString(6, safe(item.getDescription()));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving experience: " + e.getMessage());
        }
    }

    public List<Projects> getProjectsForUser(int userId) {
        List<Projects> list = new ArrayList<>();
        String sql = "SELECT title, description, url FROM projects WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Projects item = new Projects();
                    item.setUserID(userId);
                    item.setTitle(rs.getString("title"));
                    item.setDescription(rs.getString("description"));
                    item.setUrl(rs.getString("url"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading projects: " + e.getMessage());
        }
        return list;
    }

    public void replaceProjectsForUser(int userId, List<Projects> items) {
        String deleteSql = "DELETE FROM projects WHERE user_id = ?";
        String insertSql = "INSERT INTO projects (user_id, title, description, url) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Projects item : items) {
                    if (isBlank(item.getTitle()) && isBlank(item.getDescription())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, safe(item.getTitle()));
                    ins.setString(3, safe(item.getDescription()));
                    ins.setString(4, safe(item.getUrl()));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving projects: " + e.getMessage());
        }
    }

    public List<Skill> getSkillsForUser(int userId) {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT skill_name, proficiency_level FROM skill WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Skill item = new Skill();
                    item.setUserID(userId);
                    item.setSkillName(rs.getString("skill_name"));
                    item.setProficiencyLevel(rs.getString("proficiency_level"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading skills: " + e.getMessage());
        }
        return list;
    }

    public void replaceSkillsForUser(int userId, List<Skill> items) {
        String deleteSql = "DELETE FROM skill WHERE user_id = ?";
        String insertSql = "INSERT INTO skill (user_id, skill_name, proficiency_level) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Skill item : items) {
                    if (isBlank(item.getSkillName())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, item.getSkillName().trim());
                    ins.setString(3, isBlank(item.getProficiencyLevel()) ? "Not Specified" : item.getProficiencyLevel().trim());
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving skills: " + e.getMessage());
        }
    }

    public List<AwardsCerts> getAwardsForUser(int userId) {
        List<AwardsCerts> list = new ArrayList<>();
        String sql = "SELECT title, issuer, date_issued, description FROM awards_certs WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    AwardsCerts item = new AwardsCerts();
                    item.setUserID(userId);
                    item.setTitle(rs.getString("title"));
                    item.setIssuer(rs.getString("issuer"));
                    item.setDateIssued(rs.getString("date_issued"));
                    item.setDescription(rs.getString("description"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading awards: " + e.getMessage());
        }
        return list;
    }

    public void replaceAwardsForUser(int userId, List<AwardsCerts> items) {
        String deleteSql = "DELETE FROM awards_certs WHERE user_id = ?";
        String insertSql = "INSERT INTO awards_certs (user_id, title, issuer, date_issued, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (AwardsCerts item : items) {
                    if (isBlank(item.getTitle())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, safe(item.getTitle()));
                    ins.setString(3, safe(item.getIssuer()));
                    ins.setString(4, safe(item.getDateIssued()));
                    ins.setString(5, safe(item.getDescription()));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving awards: " + e.getMessage());
        }
    }

    public List<Websites> getWebsitesForUser(int userId) {
        List<Websites> list = new ArrayList<>();
        String sql = "SELECT platform, url FROM websites WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Websites item = new Websites();
                    item.setUserID(userId);
                    item.setPlatform(rs.getString("platform"));
                    item.setUrl(rs.getString("url"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading websites: " + e.getMessage());
        }
        return list;
    }

    public void replaceWebsitesForUser(int userId, List<Websites> items) {
        String deleteSql = "DELETE FROM websites WHERE user_id = ?";
        String insertSql = "INSERT INTO websites (user_id, platform, url) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement del = conn.prepareStatement(deleteSql)) {
                del.setInt(1, userId);
                del.executeUpdate();
            }
            if (items == null) return;
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                for (Websites item : items) {
                    if (isBlank(item.getUrl()) && isBlank(item.getPlatform())) continue;
                    ins.setInt(1, userId);
                    ins.setString(2, safe(item.getPlatform()));
                    ins.setString(3, safe(item.getUrl()));
                    ins.addBatch();
                }
                ins.executeBatch();
            }
        } catch (SQLException e) {
            System.err.println("Error saving websites: " + e.getMessage());
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}