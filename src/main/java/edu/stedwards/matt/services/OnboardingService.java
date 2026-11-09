package edu.stedwards.matt.services;

import java.util.List;

import edu.stedwards.matt.dao.AwardsCertsDAO;
import edu.stedwards.matt.dao.EducationDAO;
import edu.stedwards.matt.dao.ExperienceDAO;
import edu.stedwards.matt.dao.ProjectsDAO;
import edu.stedwards.matt.dao.SkillDAO;
import edu.stedwards.matt.dao.WebsitesDAO;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;

public class OnboardingService {

    /** Saves onboarding data for an existing account and marks it complete. */
    public boolean saveCompleteOnboarding(
            int userId,
            List<Education> education,
            List<Experience> experiences,
            List<Projects> projects,
            List<Skill> skills,
            List<AwardsCerts> awards,
            List<Websites> websites) {

        if (userId <= 0) {
            System.err.println("Cannot save onboarding data without a logged-in user.");
            return false;
        }

        boolean allSaved = true;

        if (education != null) {
            EducationDAO eduDAO = new EducationDAO();
            for (Education edu : education) {
                if (!hasAny(edu.getInstitution(), edu.getDegree(), edu.getGpa(), edu.getStartDate(), edu.getEndDate())) {
                    continue;
                }
                edu.setUserID(userId);
                allSaved &= eduDAO.save(edu);
            }
        }
        
        if (experiences != null) {
            ExperienceDAO expDAO = new ExperienceDAO();
            for (Experience exp : experiences) {
                if (!hasAny(exp.getJobTitle(), exp.getCompanyName(), exp.getDescription(), exp.getStartDate(), exp.getEndDate())) {
                    continue;
                }
                exp.setUserID(userId);
                allSaved &= expDAO.save(exp);
            }
        }

        if (projects != null) {
            ProjectsDAO projDAO = new ProjectsDAO();
            for (Projects proj : projects) {
                if (!hasAny(proj.getTitle(), proj.getDescription(), proj.getUrl())) {
                    continue;
                }
                proj.setUserID(userId);
                allSaved &= projDAO.save(proj);
            }
        }

        if (skills != null) {
            SkillDAO skillDAO = new SkillDAO();
            for (Skill skill : skills) {
                if (!hasAny(skill.getSkillName())) {
                    continue;
                }
                skill.setUserID(userId);
                allSaved &= skillDAO.save(skill);
            }
        }

        if (awards != null) {
            AwardsCertsDAO awardDAO = new AwardsCertsDAO();
            for (AwardsCerts award : awards) {
                if (!hasAny(award.getTitle(), award.getIssuer(), award.getDateIssued(), award.getDescription())) {
                    continue;
                }
                award.setUserID(userId);
                allSaved &= awardDAO.save(award);
            }
        }

        if (websites != null) {
            WebsitesDAO webDAO = new WebsitesDAO();
            for (Websites web : websites) {
                if (!hasAny(web.getPlatform(), web.getUrl())) {
                    continue;
                }
                web.setUserID(userId);
                allSaved &= webDAO.save(web);
            }
        }

        if (!allSaved) {
            System.err.println("Onboarding data was not fully saved. Account remains incomplete.");
            return false;
        }
        return new UserService().markOnboarded(userId);
    }

    private static boolean hasAny(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return true;
            }
        }
        return false;
    }
}
