package edu.stedwards.matt.controllers;

import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.services.TemplateStorageService;
import edu.stedwards.matt.services.TemplateService;
import edu.stedwards.matt.services.DocumentExportService;
import edu.stedwards.matt.services.TemplateFieldParserService;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class TemplateEditorController {
    
    private final TemplateStorageService storageService;
    private final TemplateService templateService;
    private final DocumentExportService exportService;
    private final TemplateFieldParserService fieldParserService;

    public TemplateEditorController() {
        this.storageService = new TemplateStorageService();
        this.templateService = new TemplateService();
        this.exportService = new DocumentExportService();
        this.fieldParserService = new TemplateFieldParserService();
    }

    /** Returns the dynamic fields currently present in serialized editor HTML. */
    public Set<String> extractDynamicFields(String htmlContent) {
        return fieldParserService.extractFields(htmlContent);
    }

    
     //Clears the editor for a brand new template.
    public void createNewTemplate(HTMLEditor htmlEditor) {
        htmlEditor.setHtmlText("");
    }

    
     //Loads an existing saved template into the JavaFX HTMLEditor.
     
    public void loadTemplate(HTMLEditor htmlEditor, String fileLoc) {
        try {
            String content = storageService.loadTemplateFile(fileLoc);
            htmlEditor.setHtmlText(content);
        } catch (IOException e) {
            System.err.println("Failed to load template from file: " + e.getMessage());
            htmlEditor.setHtmlText("<p><i>Error loading template file.</i></p>");
        }
    }

    // Extracts the HTML from the editor and passes it to the storage service to save.
    public boolean saveTemplate(HTMLEditor htmlEditor, Template template) {
        String htmlContent = htmlEditor.getHtmlText();
        try {
            storageService.saveTemplate(template, htmlContent);
            System.out.println("Template saved successfully!");
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save template: " + e.getMessage());
            return false;
        }
    }

    /**
     * The full pipeline: Extracts the HTML, sends it to Ollama to fill the [brackets], 
     * and prompts the user to save the finished document to their computer.
     */
    public void generateAndExportDocument(Stage currentStage, HTMLEditor htmlEditor, Map<String, String> jobFields, String userContext, String defaultFileName) {
        String rawHtml = htmlEditor.getHtmlText();
        System.out.println("Sending template to AI for processing...");
        
        //Fill the [brackets] using Ollama
        String aiFilledHtml = templateService.fillTemplateWithAI(rawHtml, jobFields, userContext);
        
        //Open a FileChooser to let the user save the customized output
        exportService.exportFinishedDocument(currentStage, aiFilledHtml, defaultFileName);
    }
}
