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
import org.jsoup.nodes.Document;

public class DocumentExportService {
    private static final Path DEFAULT_DOWNLOADS_DIRECTORY =
            Path.of(System.getProperty("user.home"), "Downloads");

    public void exportFinishedDocument(Stage currentStage, String documentContent, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Application Material");

        fileChooser.setInitialFileName(defaultFileName + ".html");
        
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("HTML Document", "*.html"));

        File savedFile = fileChooser.showSaveDialog(currentStage);

        if (savedFile != null) {
            try {
                Path targetPath = savedFile.toPath();
                Path parentDir = targetPath.getParent() != null ? targetPath.getParent() : DEFAULT_DOWNLOADS_DIRECTORY;
                String fileName = targetPath.getFileName().toString();
                String baseName = fileName.toLowerCase().endsWith(".html")
                        ? fileName.substring(0, fileName.length() - 5)
                        : fileName;

                Path uniquePath = FileNamingUtil.resolveUniqueFilePath(parentDir, baseName, ".html");
                Files.writeString(uniquePath, documentContent);
                System.out.println("Document successfully saved to: " + uniquePath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to export document: " + e.getMessage());
            }
        }
    }

    /**
     * Converts generated HTML to PDF and saves it under:
     * [company name]_[filename suffix]_[user's firstname].pdf (with " (i)" collision handling)
     * in the user's Downloads folder.
     */
    public Path exportAsPDF(
            String documentContent,
            String companyName,
            String filenameSuffix,
            String userFirstName) throws IOException {
        return exportAsPDF(documentContent, companyName, filenameSuffix, userFirstName, DEFAULT_DOWNLOADS_DIRECTORY);
    }

    /**
     * Same formatted naming and collision handling, saving to a custom directory (e.g., from Settings).
     */
    public Path exportAsPDF(
            String documentContent,
            String companyName,
            String filenameSuffix,
            String userFirstName,
            Path targetDirectory) throws IOException {
        String baseName = FileNamingUtil.buildBaseFileName(companyName, filenameSuffix, userFirstName);
        return writePdfToUniquePath(documentContent, baseName, targetDirectory);
    }

    /**
     * Backwards-compatible overload: saves a PDF using the supplied base filename
     * with automatic " (i)" collision handling in the Downloads folder.
     */
    public Path exportAsPDF(String documentContent, String defaultFileName) throws IOException {
        String safeName = (defaultFileName == null || defaultFileName.isBlank())
                ? "application-material"
                : defaultFileName.trim().replaceAll("[\\\\/:*?\"<>|]", "");
        if (safeName.toLowerCase().endsWith(".pdf")) {
            safeName = safeName.substring(0, safeName.length() - 4);
        }
        return writePdfToUniquePath(documentContent, safeName, DEFAULT_DOWNLOADS_DIRECTORY);
    }

    private Path writePdfToUniquePath(String documentContent, String baseName, Path targetDirectory) throws IOException {
        Path outputDir = (targetDirectory != null) ? targetDirectory : DEFAULT_DOWNLOADS_DIRECTORY;
        Files.createDirectories(outputDir);

        Path output = FileNamingUtil.resolveUniqueFilePath(outputDir, baseName, ".pdf");

        String formattedText = toPdfText(extractReadableText(documentContent));
        List<String> lines = wrapParagraphs(formattedText, 500);

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
                    if (!line.isEmpty()) {
                        stream.showText(line);
                    }
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

    /** Preserves paragraph and line breaks from HTML before writing to PDF. */
    private static String extractReadableText(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        Document doc = Jsoup.parse(html);
        doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
        doc.select("br").append("\\n");
        doc.select("p, div, li, h1, h2, h3, h4, h5, h6").prepend("\\n");
        String withNewlines = doc.text().replace("\\n", "\n");
        return withNewlines.replaceAll("\n{3,}", "\n\n").trim();
    }

    private static List<String> wrapParagraphs(String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        if (text.isBlank()) {
            lines.add("Generated document is empty.");
            return lines;
        }

        for (String paragraph : text.split("\\r?\\n")) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty()) {
                lines.add("");
                continue;
            }
            StringBuilder line = new StringBuilder();
            for (String word : trimmed.split("\\s+")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (!line.isEmpty() && PDType1Font.HELVETICA.getStringWidth(candidate) / 1000f * 11 > maxWidth) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                if (!line.isEmpty()) line.append(' ');
                line.append(word);
            }
            if (!line.isEmpty()) lines.add(line.toString());
        }
        return lines;
    }

    private static String toPdfText(String text) {
        return text.replace('\u2018', '\'').replace('\u2019', '\'')
                .replace('\u201c', '"').replace('\u201d', '"')
                .replace('\u2013', '-').replace('\u2014', '-')
                .replace('\u2022', '*')
                .replaceAll("[^\\n\\u0020-\\u007E\\u00A0-\\u00FF]", "?");
    }
}