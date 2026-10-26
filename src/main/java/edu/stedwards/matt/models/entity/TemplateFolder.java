package edu.stedwards.matt.models.entity;

public class TemplateFolder {
    private int folderId;
    private int userId;
    private String folderName;

    // Getters and Setters
    public int getFolderId() { return folderId; }
    public void setFolderId(int folderId) { this.folderId = folderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getFolderName() { return folderName; }
    public void setFolderName(String folderName) { this.folderName = folderName; }
}