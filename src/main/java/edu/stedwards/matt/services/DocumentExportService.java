package edu.stedwards.matt.services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DocumentExportService {

    public void exportFinishedDocument(Stage currentStage, String documentContent, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Application Material");
        
        // Suggest a default filename for the user
        fileChooser.setInitialFileName(defaultFileName + ".html");
        
        // Force it to save as an HTML file
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Document", "*.html"));
        
        // Show the save window
        File savedFile = fileChooser.showSaveDialog(currentStage);
        
        if (savedFile != null) {
            try {
                // Write the final AI-filled text to the user's chosen location
                Files.writeString(savedFile.toPath(), documentContent);
                System.out.println("Document successfully saved to: " + savedFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to export document: " + e.getMessage());
            }
        }
    }
}