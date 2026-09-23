package edu.stedwards.matt.models.entity;

public class Company {
    private int companyId;
    private String companyName;
    private String industry;
    private String description;
    private String websiteUrl;

    // getters
    public int getCompanyID(){
        return companyId;
    }

    public String getCompanyName(){
        return companyName;
    }

    public String getIndustry(){
        return industry;
    }

    public String getDescription(){
        return description;
    }

    public String getWebsiteUrl(){
        return websiteUrl;
    }

    // setters
    public void setCompanyID(int companyId){
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    public void setIndustry(String industry){
        this.industry = industry;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setWebsiteUrl(String websiteUrl){
        this.websiteUrl = websiteUrl;
    }
}
