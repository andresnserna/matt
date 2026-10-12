package edu.stedwards.matt.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import edu.stedwards.matt.services.DocumentExtractionService;

public class DocumentExtractionServiceTest {

    @Test
    public void testExtractTextFromTxt() throws IOException {
        DocumentExtractionService service = new DocumentExtractionService();
        
        // Create a temporary text file simulating a resume
        Path tempFile = Files.createTempFile("test_resume", ".txt");
        Files.writeString(tempFile, "Jane Doe\njane@example.com\nSkills: Java, SQL");
        
        // Extract the text using the absolute path
        String extractedText = service.extractText(tempFile.toAbsolutePath().toString());
        
        // Verify the service successfully read the text
        assertTrue(extractedText.contains("Jane Doe"));
        assertTrue(extractedText.contains("Java, SQL"));
        
        // Clean up the temporary file
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void testInvalidFileExtension() {
        DocumentExtractionService service = new DocumentExtractionService();
        
        // Ensure the service rejects non-txt/pdf files (like an image)
        assertThrows(IOException.class, () -> {
            service.extractText("C:/fake_path/resume.jpg");
        });
    }
}