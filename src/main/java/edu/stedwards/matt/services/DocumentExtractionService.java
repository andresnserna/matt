package edu.stedwards.matt.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class DocumentExtractionService {

    /**
     * Extracts text from a given file path. Supports .txt and .pdf files.
     */
    public String extractText(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be empty.");
        }

        Path path = Paths.get(filePath.trim());
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("File does not exist or is invalid: " + filePath);
        }

        String lowerPath = filePath.toLowerCase();
        if (lowerPath.endsWith(".pdf")) {
            return extractTextFromPdf(path.toFile());
        } else if (lowerPath.endsWith(".txt")) {
            return Files.readString(path);
        } else {
            throw new IOException("Unsupported file format. Please upload .txt or .pdf");
        }
    }

    private String extractTextFromPdf(File file) throws IOException {
        // PDDocument auto-closes via try-with-resources
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}