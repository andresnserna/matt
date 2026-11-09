package edu.stedwards.matt.models.entity;

public class JobPosting {
    private int jobpostId;
    private int userId;
    private int companyId;
    private String companyName;
    private String jobTitle;
    private String location;
    private String jobUrl;
    
    // New Pay Fields
    private double payMin;
    private double payMax;
    private String payPeriod;
    
    private String sourceFileName;
    
    // New Description Breakdown Fields
    private String responsibilities;
    private String requirements;
    private String niceToHave;
    private String benefits;
    
    private String rawText;
    private String normalizedText;
    private double matchScore;
    private double spamScore;
    private String status;
    private String dateAdded;

    public JobPosting() {
        this.status = "Saved"; // Default status based on your DB schema
        this.spamScore = 0.0;
    }

    // Getters and Setters
    public int getJobpostId() { return jobpostId; }
    public void setJobpostId(int jobpostId) { this.jobpostId = jobpostId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getJobUrl() { return jobUrl; }
    public void setJobUrl(String jobUrl) { this.jobUrl = jobUrl; }

    public double getPayMin() { return payMin; }
    public void setPayMin(double payMin) { this.payMin = payMin; }

    public double getPayMax() { return payMax; }
    public void setPayMax(double payMax) { this.payMax = payMax; }

    public String getPayPeriod() { return payPeriod; }
    public void setPayPeriod(String payPeriod) { this.payPeriod = payPeriod; }

    public String getSourceFileName() { return sourceFileName; }
    public void setSourceFileName(String sourceFileName) { this.sourceFileName = sourceFileName; }

    public String getResponsibilities() { return responsibilities; }
    public void setResponsibilities(String responsibilities) { this.responsibilities = responsibilities; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getNiceToHave() { return niceToHave; }
    public void setNiceToHave(String niceToHave) { this.niceToHave = niceToHave; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getNormalizedText() { return normalizedText; }
    public void setNormalizedText(String normalizedText) { this.normalizedText = normalizedText; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public double getSpamScore() { return spamScore; }
    public void setSpamScore(double spamScore) { this.spamScore = spamScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDateAdded() { return dateAdded; }
    public void setDateAdded(String dateAdded) { this.dateAdded = dateAdded; }
}