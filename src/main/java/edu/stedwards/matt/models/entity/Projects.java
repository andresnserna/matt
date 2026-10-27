package edu.stedwards.matt.models.entity;

public class Projects {
    private int projectId;
    private int userId;
    private String projectName;
    private String role;
    private String location;
    private String description;
    private String url;
    private String startDate;
    private String endDate;

    // getters
    public int getProjectID(){
        return projectId;
    }

    public int getUserID(){
        return userId;
    }

    public String getProjectName(){
        return projectName;
    }

    public String getRole(){
        return role;
    }

    public String getLocation(){
        return location;
    }

    public String getDescription(){
        return description;
    }

    public String getUrl(){
        return url;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    // setters
    public void setProjectID(int projectId){
        this.projectId = projectId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setProjectName(String projectName){
        this.projectName = projectName;
    }

    public void setRole(String role){
        this.role = role;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setUrl(String url){
        this.url = url;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    /**
     * Copies the project data into a new unsaved entity, preserving its owner but not its ID.
     */
    public Projects copy(){
        Projects copy = new Projects();
        copy.setUserID(userId);
        copy.setProjectName(projectName);
        copy.setRole(role);
        copy.setLocation(location);
        copy.setDescription(description);
        copy.setUrl(url);
        copy.setStartDate(startDate);
        copy.setEndDate(endDate);
        return copy;
    }
}
