package edu.stedwards.matt.controllers;

import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.services.DocumentExportService;
import edu.stedwards.matt.services.TemplateFieldParserService;
import edu.stedwards.matt.services.TemplateService;
import edu.stedwards.matt.services.TemplateStorageService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

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

    /** Clears the editor for a brand new template. */
    public void createNewTemplate(HTMLEditor htmlEditor) {
        htmlEditor.setHtmlText("");
    }

    /** Loads an existing saved template into the JavaFX HTMLEditor. */
    public void loadTemplate(HTMLEditor htmlEditor, String fileLoc) {
        try {
            String content = storageService.loadTemplateFile(fileLoc);
            htmlEditor.setHtmlText(content);
        } catch (IOException e) {
            System.err.println("Failed to load template from file: " + e.getMessage());
            htmlEditor.setHtmlText("<p><i>Error loading template file.</i></p>");
        }
    }

    /** Extracts the HTML from the editor and passes it to the storage service to save. */
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
     * Days 3-4 Task: Checks the HTML content and returns a list of any [bracketed] fields
     * that were not filled by the AI or user.
     */
    public List<String> validateMissingFields(String htmlContent) {
        return templateService.findUnfilledFields(htmlContent);
    }

    /**
     * Days 3-4 Task: Full pipeline with field-by-field progress reporting and pre-export validation.
     */
    public void generateAndExportDocument(
            Stage currentStage,
            HTMLEditor htmlEditor,
            Map<String, String> jobFields,
            String userContext,
            String defaultFileName,
            BiConsumer<Integer, Integer> progressCallback) {

        String rawHtml = htmlEditor.getHtmlText();
        System.out.println("Sending template to AI for structured key-value processing...");

        // 1. Fill the [brackets] programmatically from Ollama's structured key-value response
        String aiFilledHtml = templateService.fillTemplateWithAI(rawHtml, jobFields, userContext, progressCallback);

        // Update the editor preview so the user can see the filled document immediately
        htmlEditor.setHtmlText(aiFilledHtml);

        // 2. Validate that all [fields] were filled before exporting
        List<String> missingFields = validateMissingFields(aiFilledHtml);
        if (!missingFields.isEmpty()) {
            System.out.println("Validation warning — unfilled template fields: " + missingFields);
            Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
            warning.setTitle("Unfilled Template Fields");
            warning.setHeaderText("Some placeholders could not be filled automatically:");
            warning.setContentText("Missing fields: " + String.join(", ", missingFields)
                    + "\n\nWould you like to export anyway, or cancel to edit them in the editor first?");
            Optional<ButtonType> choice = warning.showAndWait();
            if (choice.isEmpty() || choice.get() != ButtonType.OK) {
                return;
            }
        }

        // 3. Open a FileChooser to let the user save the customized output
        exportService.exportFinishedDocument(currentStage, aiFilledHtml, defaultFileName);
    }

    /** Backwards-compatible overload without progress callback. */
    public void generateAndExportDocument(Stage currentStage, HTMLEditor htmlEditor, Map<String, String> jobFields, String userContext, String defaultFileName) {
        generateAndExportDocument(currentStage, htmlEditor, jobFields, userContext, defaultFileName, null);
    }
}