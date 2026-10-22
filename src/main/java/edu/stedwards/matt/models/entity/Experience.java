package edu.stedwards.matt.models.entity;

public class Experience {
    private int experienceId;
    private int userId;
    private String jobTitle;
    private String companyName;
    private String startDate;
    private String endDate;
    private String description;

    // getters
    public int getExperienceID(){
        return experienceId;
    }

    public int getUserID(){
        return userId;
    }

    public String getJobTitle(){
        return jobTitle;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public String getDescription(){
        return description;
    }

    // setters
    public void setExperienceID(int experienceId){
        this.experienceId = experienceId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setJobTitle(String jobTitle){
        this.jobTitle = jobTitle;
    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    public void setDescription(String description){
        this.description = description;
    }

    /**
     * Copies the experience data into a new unsaved entity, preserving its owner but not its ID.
     */
    public Experience copy(){
        Experience copy = new Experience();
        copy.setUserID(userId);
        copy.setJobTitle(jobTitle);
        copy.setCompanyName(companyName);
        copy.setStartDate(startDate);
        copy.setEndDate(endDate);
        copy.setDescription(description);
        return copy;
    }
}
