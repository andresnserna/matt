package edu.stedwards.matt.models.entity;

public class ApplicationMaterial {
    private int materialId;
    private int jobpostId;
    private String type;
    private String fileLoc;

    // getters
    public int getMaterialID(){
        return materialId;
    }

    public int getJobpostID(){
        return jobpostId;
    }

    public String getType(){
        return type;
    }

    public String getFileLoc(){
        return fileLoc;
    }

    // setters
    public void setMaterialID(int materialId){
        this.materialId = materialId;
    }

    public void setJobpostID(int jobpostId){
        this.jobpostId = jobpostId;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setFileLoc(String fileLoc){
        this.fileLoc = fileLoc;
    }
}