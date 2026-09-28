package edu.stedwards.matt.models.entity;

public class AwardsCerts {
    private int awardId;
    private int userId;
    private String title;
    private String issuer;
    private String dateReceived;

    // getters
    public int getAwardID(){
        return awardId;
    }

    public int getUserID(){
        return userId;
    }

    public String getTitle(){
        return title;
    }

    public String getIssuer(){
        return issuer;
    }

    public String getDateReceived(){
        return dateReceived;
    }

    // setters
    public void setAwardID(int awardId){
        this.awardId = awardId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIssuer(String issuer){
        this.issuer = issuer;
    }

    public void setDateReceived(String dateReceived){
        this.dateReceived = dateReceived;
    }
}