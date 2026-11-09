package edu.stedwards.matt.services;

import java.util.List;

import edu.stedwards.matt.dao.AwardsCertsDAO;
import edu.stedwards.matt.dao.EducationDAO;
import edu.stedwards.matt.dao.ExperienceDAO;
import edu.stedwards.matt.dao.ProjectsDAO;
import edu.stedwards.matt.dao.SkillDAO;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.dao.WebsitesDAO;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.models.entity.Websites;

public class OnboardingService {

    /**
     * Orchestrates saving all onboarding data to the database.
     * Returns true if successful, false if the User ID could not be generated.
     */
    public boolean saveCompleteOnboarding(
            List<Education> education,
            List<Experience> experiences,
            List<Projects> projects,
            List<Skill> skills,
            List<AwardsCerts> awards,
            List<Websites> websites) {

        UserDAO userDAO = new UserDAO();
        User placeholderUser = new User();
        
        // Temporary placeholder generation until auth is fully hooked up to the wizard
        placeholderUser.setUsername("user_" + System.currentTimeMillis());
        placeholderUser.setFirstName("New");
        placeholderUser.setLastName("User");
        
        int userId = userDAO.saveAndGetId(placeholderUser);
        if (userId == -1) {
            System.err.println("Failed to create User ID! Aborting saves.");
            return false;
        }

        if (education != null) {
            EducationDAO eduDAO = new EducationDAO();
            for (Education edu : education) {
                edu.setUserID(userId);
                eduDAO.save(edu);
            }
        }
        
        if (experiences != null) {
            ExperienceDAO expDAO = new ExperienceDAO();
            for (Experience exp : experiences) {
                exp.setUserID(userId);
                expDAO.save(exp);
            }
        }

        if (projects != null) {
            ProjectsDAO projDAO = new ProjectsDAO();
            for (Projects proj : projects) {
                proj.setUserID(userId);
                projDAO.save(proj);
            }
        }

        if (skills != null) {
            SkillDAO skillDAO = new SkillDAO();
            for (Skill skill : skills) {
                skill.setUserID(userId);
                skillDAO.save(skill);
            }
        }

        if (awards != null) {
            AwardsCertsDAO awardDAO = new AwardsCertsDAO();
            for (AwardsCerts award : awards) {
                award.setUserID(userId);
                awardDAO.save(award);
            }
        }

        if (websites != null) {
            WebsitesDAO webDAO = new WebsitesDAO();
            for (Websites web : websites) {
                web.setUserID(userId);
                webDAO.save(web);
            }
        }

        return true;
    }
}