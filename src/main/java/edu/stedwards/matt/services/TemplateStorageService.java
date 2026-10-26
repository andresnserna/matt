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
        
        //Clean up the filename so it's safe for Windows/Mac
        String safeName = template.getTemplateName().replaceAll("[^a-zA-Z0-9.-]", "_");
        
        //Save the HTML content to a physical file
        Path filePath = Paths.get(TEMPLATE_DIR + safeName + ".html");
        Files.writeString(filePath, htmlContent);
        
        //Update the Template object with the absolute path and save to SQLite
        template.setFileLoc(filePath.toAbsolutePath().toString());
        
        TemplateDAO dao = new TemplateDAO();
        dao.insertTemplate(template);
    }
    
    public String loadTemplateFile(String fileLoc) throws IOException {
        Path path = Paths.get(fileLoc);
        if (Files.exists(path)) {
            return Files.readString(path);
        }
        return "<p>File not found.</p>";
    }
}