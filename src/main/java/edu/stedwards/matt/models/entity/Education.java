package edu.stedwards.matt.models.entity;

public class Education {
    private int educationId;
    private int userId;
    private String institution;
    private String degree;
    private String startDate;
    private String endDate;
    private String gpa;

    // getters
    public int getEducationID(){
        return educationId;
    }

    public int getUserID(){
        return userId;
    }

    public String getInstitution(){
        return institution;
    }

    public String getDegree(){
        return degree;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getEndDate(){
        return endDate;
    }

    public String getGpa(){
        return gpa;
    }

    // setters
    public void setEducationID(int educationId){
        this.educationId = educationId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setInstitution(String institution){
        this.institution = institution;
    }

    public void setDegree(String degree){
        this.degree = degree;
    }

    public void setStartDate(String startDate){
        this.startDate = startDate;
    }

    public void setEndDate(String endDate){
        this.endDate = endDate;
    }

    public void setGpa(String gpa){
        this.gpa = gpa;
    }
}