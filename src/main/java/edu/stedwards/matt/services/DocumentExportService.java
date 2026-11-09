package edu.stedwards.matt.services;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.jsoup.Jsoup;

public class DocumentExportService {
    private static final Path DOWNLOADS_DIRECTORY = Path.of("/Users/andresserna/Downloads");

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

    /** Convert generated HTML to a readable PDF and save it in the configured Downloads folder. */
    public Path exportAsPDF(String documentContent, String defaultFileName) throws IOException {
        Files.createDirectories(DOWNLOADS_DIRECTORY);
        String safeName = defaultFileName == null || defaultFileName.isBlank()
                ? "application-material"
                : defaultFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (safeName.toLowerCase().endsWith(".pdf")) safeName = safeName.substring(0, safeName.length() - 4);
        Path output = DOWNLOADS_DIRECTORY.resolve(safeName + ".pdf");

        String text = toPdfText(Jsoup.parse(documentContent == null ? "" : documentContent).text());
        List<String> lines = wrapText(text, 500);
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            float y = page.getMediaBox().getHeight() - 54;
            PDPageContentStream stream = new PDPageContentStream(document, page);
            try {
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 11);
                stream.setLeading(15);
                stream.newLineAtOffset(54, y);
                for (String line : lines) {
                    if (y < 54) {
                        stream.endText();
                        stream.close();
                        page = new PDPage(PDRectangle.LETTER);
                        document.addPage(page);
                        y = page.getMediaBox().getHeight() - 54;
                        stream = new PDPageContentStream(document, page);
                        stream.beginText();
                        stream.setFont(PDType1Font.HELVETICA, 11);
                        stream.setLeading(15);
                        stream.newLineAtOffset(54, y);
                    }
                    stream.showText(line);
                    stream.newLine();
                    y -= 15;
                }
                stream.endText();
            } finally {
                stream.close();
            }
            document.save(output.toFile());
        }
        return output;
    }

    private static List<String> wrapText(String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text.isBlank()) {
            lines.add("Generated document is empty.");
            return lines;
        }
        StringBuilder line = new StringBuilder();
        for (String word : text.split("\\s+")) {
            String candidate = line.isEmpty() ? word : line + " " + word;
            if (!line.isEmpty() && PDType1Font.HELVETICA.getStringWidth(candidate) / 1000f * 11 > maxWidth) {
                lines.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) line.append(' ');
            line.append(word);
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    private static String toPdfText(String text) {
        return text.replace('\u2018', '\'').replace('\u2019', '\'')
                .replace('\u201c', '"').replace('\u201d', '"')
                .replace('\u2013', '-').replace('\u2014', '-')
                .replace('\u2022', '*')
                .replaceAll("[^\\u0020-\\u007E\\u00A0-\\u00FF]", "?");
    }
}
