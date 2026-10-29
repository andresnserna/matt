package edu.stedwards.matt.controllers;

import java.util.function.Function;
import java.util.function.Supplier;

import javafx.collections.ObservableList;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Websites;

/** Handles onboarding form state changes and collection operations. */
public class OnboardingStepController {

    public <T> void ensureInitialItem(ObservableList<T> items, Supplier<T> itemFactory) {
        if (items.isEmpty()) {
            items.add(itemFactory.get());
        }
    }

    public void duplicateEducation(ObservableList<Education> items, int index) {
        duplicate(items, index, Education::copy);
    }

    public void removeEducation(ObservableList<Education> items, Education item) {
        removeKeepingOne(items, item);
    }

    public void duplicateExperience(ObservableList<Experience> items, int index) {
        duplicate(items, index, Experience::copy);
    }

    public void removeExperience(ObservableList<Experience> items, Experience item) {
        removeKeepingOne(items, item);
    }

    public void duplicateProject(ObservableList<Projects> items, int index) {
        duplicate(items, index, Projects::copy);
    }

    public void removeProject(ObservableList<Projects> items, Projects item) {
        removeKeepingOne(items, item);
    }

    public void duplicateAward(ObservableList<AwardsCerts> items, int index) {
        duplicate(items, index, AwardsCerts::copy);
    }

    public void removeAward(ObservableList<AwardsCerts> items, AwardsCerts item) {
        removeKeepingOne(items, item);
    }

    public boolean addSkill(ObservableList<Skill> skills, String name) {
        String cleanName = name == null ? "" : name.trim();
        if (cleanName.isEmpty()) return false;
        Skill skill = new Skill();
        skill.setSkillName(cleanName);
        skills.add(skill);
        return true;
    }

    public void removeSkill(ObservableList<Skill> skills, Skill skill) {
        skills.remove(skill);
    }

    public boolean addWebsite(ObservableList<Websites> websites, String platform, String url) {
        String cleanPlatform = platform == null ? "" : platform.trim();
        String cleanUrl = url == null ? "" : url.trim();
        if (cleanPlatform.isEmpty() && cleanUrl.isEmpty()) return false;
        Websites website = new Websites();
        website.setUrlType(cleanPlatform);
        website.setUrl(cleanUrl);
        websites.add(website);
        return true;
    }

    public void removeWebsite(ObservableList<Websites> websites, Websites website) {
        websites.remove(website);
    }

    public void updateEducationInstitution(Education item, String value) { item.setInstitution(value); }
    public void updateEducationDegree(Education item, String value) { item.setDegree(value); }
    public void updateEducationGpa(Education item, String value) { item.setGpa(value); }
    public void updateEducationStartDate(Education item, String value) { item.setStartDate(value); }
    public void updateEducationEndDate(Education item, String value) { item.setEndDate(value); }

    public void updateExperienceJobTitle(Experience item, String value) { item.setJobTitle(value); }
    public void updateExperienceCompany(Experience item, String value) { item.setCompanyName(value); }
    public void updateExperienceDescription(Experience item, String value) { item.setDescription(value); }
    public void updateExperienceStartDate(Experience item, String value) { item.setStartDate(value); }
    public void updateExperienceEndDate(Experience item, String value) { item.setEndDate(value); }

    public void updateProjectName(Projects item, String value) { item.setProjectName(value); }
    public void updateProjectRole(Projects item, String value) { item.setRole(value); }
    public void updateProjectLocation(Projects item, String value) { item.setLocation(value); }
    public void updateProjectDescription(Projects item, String value) { item.setDescription(value); }
    public void updateProjectStartDate(Projects item, String value) { item.setStartDate(value); }
    public void updateProjectEndDate(Projects item, String value) { item.setEndDate(value); }

    public void updateAwardTitle(AwardsCerts item, String value) { item.setTitle(value); }
    public void updateAwardIssuer(AwardsCerts item, String value) { item.setIssuer(value); }
    public void updateAwardDate(AwardsCerts item, String value) { item.setDateReceived(value); }

    private <T> void duplicate(ObservableList<T> items, int index, Function<T, T> copier) {
        T item = items.get(index);
        items.add(index + 1, copier.apply(item));
    }

    private <T> void removeKeepingOne(ObservableList<T> items, T item) {
        if (items.size() > 1) {
            items.remove(item);
        }
    }
}
