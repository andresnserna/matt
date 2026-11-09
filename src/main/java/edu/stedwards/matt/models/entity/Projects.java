package edu.stedwards.matt.models.entity;

public class Projects {
    private int projectId;
    private int userId;
    private String title;
    private String description;
    private String url;

    // getters
    public int getProjectID() {
        return projectId;
    }

    public int getUserID() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    // setters
    public void setProjectID(int projectId) {
        this.projectId = projectId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Copies the project data into a new unsaved entity, preserving its owner but not its ID.
     */
    public Projects copy() {
        Projects copy = new Projects();
        copy.setUserID(userId);
        copy.setTitle(title);
        copy.setDescription(description);
        copy.setUrl(url);
        return copy;
    }
}