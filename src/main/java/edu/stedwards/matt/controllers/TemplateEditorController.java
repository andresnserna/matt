package edu.stedwards.matt.controllers;

import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.models.entity.User;
import edu.stedwards.matt.services.DocumentExportService;
import edu.stedwards.matt.services.TemplateFieldParserService;
import edu.stedwards.matt.services.TemplateService;
import edu.stedwards.matt.services.TemplateStorageService;
import java.io.IOException;
import java.nio.file.Path;
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
     * Checks the HTML content and returns a list of any [bracketed] fields
     * that were not filled by the AI or user.
     */
    public List<String> validateMissingFields(String htmlContent) {
        return templateService.findUnfilledFields(htmlContent);
    }

    /**
     * Full conversational AI generation + PDF export pipeline from saved entities.
     * Saves the PDF as [company name]_[filename suffix]_[user's firstname].pdf
     * with automatic " (i)" collision handling.
     */
    public Path generateAndExportPdf(
            Template template,
            JobPosting job,
            User signedInUser,
            BiConsumer<Integer, Integer> progressCallback) throws Exception {

        String aiFilledHtml = templateService.generateApplicationDocument(template, job, signedInUser, progressCallback);

        String suffix = (template.getFilenameSuffix() != null && !template.getFilenameSuffix().isBlank())
                ? template.getFilenameSuffix()
                : template.getTemplateName();

        return exportService.exportAsPDF(
                aiFilledHtml,
                job.getCompanyName(),
                suffix,
                signedInUser.getFirstName());
    }

    /**
     * Editor-based conversational AI generation + PDF export using the
     * [company name]_[filename suffix]_[user's firstname] naming format.
     */
    public Path generateAndExportPdf(
            HTMLEditor htmlEditor,
            Map<String, String> jobFields,
            String userContext,
            String companyName,
            String filenameSuffix,
            String userFirstName,
            BiConsumer<Integer, Integer> progressCallback) {

        String rawHtml = htmlEditor.getHtmlText();
        System.out.println("Sending template to AI for conversational field-by-field processing...");

        String aiFilledHtml = templateService.fillTemplateWithAI(rawHtml, jobFields, userContext, progressCallback);
        htmlEditor.setHtmlText(aiFilledHtml);

        if (!confirmIfFieldsMissing(aiFilledHtml)) {
            return null;
        }

        try {
            Path savedPdf = exportService.exportAsPDF(aiFilledHtml, companyName, filenameSuffix, userFirstName);
            showExportSuccessAlert(savedPdf);
            return savedPdf;
        } catch (IOException e) {
            System.err.println("Failed to export PDF: " + e.getMessage());
            showExportErrorAlert(e.getMessage());
            return null;
        }
    }

    /**
     * Backwards-compatible pipeline with progress reporting and pre-export validation.
     * Generates via conversational AI and writes the finished document directly to PDF.
     */
    public void generateAndExportDocument(
            Stage currentStage,
            HTMLEditor htmlEditor,
            Map<String, String> jobFields,
            String userContext,
            String defaultFileName,
            BiConsumer<Integer, Integer> progressCallback) {

        String rawHtml = htmlEditor.getHtmlText();
        System.out.println("Sending template to AI for conversational field-by-field processing...");

        // Fill the [brackets] programmatically from Ollama's structured key-value response
        String aiFilledHtml = templateService.fillTemplateWithAI(rawHtml, jobFields, userContext, progressCallback);

        // Update the editor preview so the user can see the filled document immediately
        htmlEditor.setHtmlText(aiFilledHtml);

        if (!confirmIfFieldsMissing(aiFilledHtml)) {
            return;
        }

        try {
            Path savedPdf = exportService.exportAsPDF(aiFilledHtml, defaultFileName);
            showExportSuccessAlert(savedPdf);
        } catch (IOException e) {
            System.err.println("Failed to export PDF: " + e.getMessage());
            showExportErrorAlert(e.getMessage());
        }
    }

    /** Backwards-compatible overload without progress callback. */
    public void generateAndExportDocument(
            Stage currentStage,
            HTMLEditor htmlEditor,
            Map<String, String> jobFields,
            String userContext,
            String defaultFileName) {
        generateAndExportDocument(currentStage, htmlEditor, jobFields, userContext, defaultFileName, null);
    }

    private boolean confirmIfFieldsMissing(String htmlContent) {
        List<String> missingFields = validateMissingFields(htmlContent);
        if (missingFields.isEmpty()) {
            return true;
        }
        System.out.println("Validation warning — unfilled template fields: " + missingFields);
        Alert warning = new Alert(Alert.AlertType.CONFIRMATION);
        warning.setTitle("Unfilled Template Fields");
        warning.setHeaderText("Some placeholders could not be filled automatically:");
        warning.setContentText("Missing fields: " + String.join(", ", missingFields)
                + "\n\nWould you like to export anyway, or cancel to edit them in the editor first?");
        Optional<ButtonType> choice = warning.showAndWait();
        return choice.isPresent() && choice.get() == ButtonType.OK;
    }

    private void showExportSuccessAlert(Path savedPdf) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("PDF Saved");
        info.setHeaderText("Application material exported successfully.");
        info.setContentText("Saved to: " + savedPdf.toAbsolutePath());
        info.showAndWait();
    }

    private void showExportErrorAlert(String errorMessage) {
        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Export Failed");
        error.setHeaderText("Could not save the PDF.");
        error.setContentText(errorMessage);
        error.showAndWait();
    }
}