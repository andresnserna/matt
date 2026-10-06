package edu.stedwards.matt.models.entity;

public class JobPosting {
    private int jobpostId;
    private int userId;
    private int companyId;
    private String companyName;
    private String jobTitle;
    private String location;
    private String jobUrl;
    private String jobDescription;
    private String rawText;
    private String normalizedText;
    private double matchScore;
    private double spamScore;
    private String status;
    private String dateAdded;

    // default constructor
    public JobPosting() {}

    // getters
    public int getJobpostID() {
        return jobpostId;
    }

    public int getUserID() {
        return userId;
    }

    public int getCompanyID() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getRawText() {
        return rawText;
    }

    public String getNormalizedText() {
        return normalizedText;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public double getSpamScore() {
        return spamScore;
    }

    public String getStatus() {
        return status;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    // setters
    public void setJobpostID(int jobpostId) {
        this.jobpostId = jobpostId;
    }

    public void setUserID(int userId) {
        this.userId = userId;
    }

    public void setCompanyID(int companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public void setSpamScore(double spamScore) {
        this.spamScore = spamScore;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }
}