package edu.stedwards.matt.services;

import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.dao.TemplateDAO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemplateStorageService {
    
    // This creates a folder right inside your project directory to store templates
    private static final String TEMPLATE_DIR = "user_data/templates/";

    public void saveTemplate(Template template, String htmlContent) throws IOException {
        //Ensure the directory exists
        Files.createDirectories(Paths.get(TEMPLATE_DIR));
        
        // Keep an existing template's file when editing; give new templates unique files.
        Path filePath;
        if (template.getFileLoc() != null && !template.getFileLoc().isBlank()) {
            filePath = Paths.get(template.getFileLoc());
        } else {
            String safeName = template.getTemplateName().replaceAll("[^a-zA-Z0-9.-]", "_");
            filePath = Paths.get(TEMPLATE_DIR, safeName + "-" + java.util.UUID.randomUUID() + ".html");
        }

        Files.createDirectories(filePath.toAbsolutePath().getParent());
        Files.writeString(filePath, htmlContent);
        
        //Update the Template object with the absolute path and save to SQLite
        template.setFileLoc(filePath.toAbsolutePath().toString());
        
        TemplateDAO dao = new TemplateDAO();
        boolean saved = template.getTemplateId() > 0
                ? dao.updateTemplate(template)
                : dao.insertTemplate(template);
        if (!saved) {
            throw new IOException("Template metadata could not be saved to the database.");
        }
    }
    
    public String loadTemplateFile(String fileLoc) throws IOException {
        Path path = Paths.get(fileLoc);
        if (Files.exists(path)) {
            return Files.readString(path);
        }
        return "<p>File not found.</p>";
    }
}
