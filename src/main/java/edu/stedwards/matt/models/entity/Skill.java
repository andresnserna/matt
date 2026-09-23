package edu.stedwards.matt.models.entity;

public class Skill {
    private int skillId;
    private int userId;
    private String skillName;
    private String proficiencyLevel;

    // getters
    public int getSkillID(){
        return skillId;
    }

    public int getUserID(){
        return userId;
    }

    public String getSkillName(){
        return skillName;
    }

    public String getProficiencyLevel(){
        return proficiencyLevel;
    }

    // setters
    public void setSkillID(int skillId){
        this.skillId = skillId;
    }

    public void setUserID(int userId){
        this.userId = userId;
    }

    public void setSkillName(String skillName){
        this.skillName = skillName;
    }

    public void setProficiencyLevel(String proficiencyLevel){
        this.proficiencyLevel = proficiencyLevel;
    }
}