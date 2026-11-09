package edu.stedwards.matt.models.entity;

public class Websites {
    private int websiteId;
    private int userId;
    private String platform;
    private String url;

    // getters
    public int getWebsiteID(){
        return websiteId;
    }

    public int getUserID(){
        return userId;
    }

    public String getPlatform(){
        return platform;
    }

    public String getUrl(){
        return url;
    }

    // setters
    public void setWebsiteID(int websiteId){
        this.websiteId = websiteId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setPlatform(String platform){
        this.platform = platform;
    }

    public void setUrl(String url){
        this.url = url;
    }
}
