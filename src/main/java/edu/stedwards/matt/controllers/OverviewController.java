package edu.stedwards.matt.controllers;

import edu.stedwards.matt.models.OnboardingSession;
import edu.stedwards.matt.models.entity.*;
import edu.stedwards.matt.dao.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

public class OverviewController {

    @FXML
    public void handleNextButton(ActionEvent event) {
        OnboardingSession session = OnboardingSession.getInstance();
        
        // 1. Save the main User profile and get the new ID
        UserDAO userDAO = new UserDAO();
        User user = session.getUserProfile();
        int userId = userDAO.saveAndGetId(user); 

        if (userId != -1) {
            // 2. Loop through and save Education
            EducationDAO eduDAO = new EducationDAO();
            for (Education edu : session.getEducationList()) {
                edu.setUserID(userId);
                eduDAO.save(edu);
            }
            
            // 3. Loop through and save Experience
            ExperienceDAO expDAO = new ExperienceDAO();
            for (Experience exp : session.getExperienceList()) {
                exp.setUserID(userId);
                expDAO.save(exp);
            }

            // 4. Save Projects
            ProjectsDAO projDAO = new ProjectsDAO();
            for (Projects proj : session.getProjectsList()) {
                proj.setUserID(userId);
                projDAO.save(proj);
            }

            // 5. Save Skills (Using your existing method)
            SkillDAO skillDAO = new SkillDAO();
            for (Skill skill : session.getSkillsList()) {
                skill.setUserID(userId);
                skillDAO.save(skill);
            }

            // 6. Save Awards/Certs
            AwardsCertsDAO awardsDAO = new AwardsCertsDAO();
            for (AwardsCerts award : session.getAwardsList()) {
                award.setUserID(userId);
                awardsDAO.save(award);
            }

            // 7. Save Websites
            WebsitesDAO webDAO = new WebsitesDAO();
            for (Websites web : session.getWebsitesList()) {
                web.setUserID(userId);
                webDAO.save(web);
            }
            
            // 8. Wipe the temporary session memory clean
            session.clearSession();
            
            // 9. Navigate to the main application home
            navigateToMainApp(event);
        } else {
            System.err.println("Database error: Could not save onboarding user profile.");
        }
    }
    
    private void navigateToMainApp(ActionEvent event) { 
        // 1. Create the main application shell
        edu.stedwards.matt.ui.layouts.other.MainLayout mainLayout = new edu.stedwards.matt.ui.layouts.other.MainLayout();
        
        // 2. Set the default starting screen inside the shell
        mainLayout.showStep(new edu.stedwards.matt.ui.layouts.application.ApplicationUploadJob());
        
        // 3. Grab the active window and swap the root layout
        ((Node) event.getSource()).getScene().setRoot(mainLayout);
    }
}