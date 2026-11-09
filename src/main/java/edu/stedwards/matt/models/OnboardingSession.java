package edu.stedwards.matt.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.stedwards.matt.models.entity.*;
import  edu.stedwards.matt.models.entity.User;

public class OnboardingSession {
    private static OnboardingSession instance;
    
    // Hold the actual entity objects instead of strings
    private User userProfile;
    private ObservableList<Education> educationList;
    private ObservableList<Experience> experienceList;
    private ObservableList<Projects> projectsList;
    private ObservableList<Skill> skillsList;
    private ObservableList<AwardsCerts> awardsList;
    private ObservableList<Websites> websitesList;

    private OnboardingSession() {
        userProfile = new User();
        educationList = FXCollections.observableArrayList();
        experienceList = FXCollections.observableArrayList();
        projectsList = FXCollections.observableArrayList();
        skillsList = FXCollections.observableArrayList();
        awardsList = FXCollections.observableArrayList();
        websitesList = FXCollections.observableArrayList();
    }

    public static OnboardingSession getInstance() {
        if (instance == null) {
            instance = new OnboardingSession();
        }
        return instance;
    }

    // Getters for the UI to bind to
    public User getUserProfile() { return userProfile; }
    public ObservableList<Education> getEducationList() { return educationList; }
    public ObservableList<Experience> getExperienceList() { return experienceList; }
    public ObservableList<Projects> getProjectsList() { return projectsList; }
    public ObservableList<Skill> getSkillsList() { return skillsList; }
    public ObservableList<AwardsCerts> getAwardsList() { return awardsList; }
    public ObservableList<Websites> getWebsitesList() { return websitesList; }

    public void clearSession() {
        userProfile = new User();
        educationList.clear();
        experienceList.clear();
        projectsList.clear();
        skillsList.clear();
        awardsList.clear();
        websitesList.clear();
    }
}