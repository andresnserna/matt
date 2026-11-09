package edu.stedwards.matt.models.entity;

public class AwardsCerts {
    private int awardId;
    private int userId;
    private String title;
    private String issuer;
    private String dateIssued; // Renamed to match schema
    private String description;

    // getters
    public int getAwardID() {
        return awardId;
    }

    public int getUserID() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getDateIssued() { // Renamed to match DAO
        return dateIssued;
    }

    public String getDescription() {
        return description;
    }

    // setters
    public void setAwardID(int awardId) {
        this.awardId = awardId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setDateIssued(String dateIssued) { // Renamed to match DAO
        this.dateIssued = dateIssued;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Copies the award data into a new unsaved entity, preserving its owner but not its ID.
     */
    public AwardsCerts copy() {
        AwardsCerts copy = new AwardsCerts();
        copy.setUserID(userId);
        copy.setTitle(title);
        copy.setIssuer(issuer);
        copy.setDateIssued(dateIssued);
        copy.setDescription(description);
        return copy;
    }
}