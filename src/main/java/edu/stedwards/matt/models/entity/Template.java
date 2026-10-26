package edu.stedwards.matt.models.entity;

public class Template {
    private int templateId;
    private Integer folderId; // Use Integer because folder_id can be NULL
    private int userId;
    private String templateName;
    private String templateType;
    private String fileLoc;

    // Getters and Setters
    public int getTemplateId() { return templateId; }
    public void setTemplateId(int templateId) { this.templateId = templateId; }
    
    public Integer getFolderId() { return folderId; }
    public void setFolderId(Integer folderId) { this.folderId = folderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    
    public String getFileLoc() { return fileLoc; }
    public void setFileLoc(String fileLoc) { this.fileLoc = fileLoc; }
}