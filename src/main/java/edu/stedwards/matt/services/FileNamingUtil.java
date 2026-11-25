package edu.stedwards.matt.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNamingUtil {

    // Matches a trailing " (1)", " (2)", etc. at the end of a base filename
    private static final Pattern COPY_SUFFIX_PATTERN = Pattern.compile("^(.*) \\((\\d+)\\)$");

    /**
     * Builds the base filename under the format:
     * [company name]_[filename suffix]_[user's firstname]
     */
    public static String buildBaseFileName(String companyName, String filenameSuffix, String userFirstName) {
        String safeCompany = sanitizeSegment(companyName, "Company");
        String safeSuffix = sanitizeSegment(filenameSuffix, "Document");
        String safeFirstName = sanitizeSegment(userFirstName, "User");
        return safeCompany + "_" + safeSuffix + "_" + safeFirstName;
    }

    /**
     * Checks if targetDir/baseName + extension already exists.
     * If it does, appends " (1)". If " (i)" already exists in the name (via regex),
     * increments to " (i+1)" until a unique path is found.
     */
    public static Path resolveUniqueFilePath(Path targetDir, String baseName, String extension) {
        String cleanExt = (extension == null || extension.isBlank())
                ? ".pdf"
                : (extension.startsWith(".") ? extension : "." + extension);

        String currentBase = baseName;
        Path candidate = targetDir.resolve(currentBase + cleanExt);

        while (Files.exists(candidate)) {
            Matcher matcher = COPY_SUFFIX_PATTERN.matcher(currentBase);
            if (matcher.matches()) {
                String prefix = matcher.group(1);
                int nextCopyNumber = Integer.parseInt(matcher.group(2)) + 1;
                currentBase = prefix + " (" + nextCopyNumber + ")";
            } else {
                currentBase = currentBase + " (1)";
            }
            candidate = targetDir.resolve(currentBase + cleanExt);
        }

        return candidate;
    }

    /** Removes illegal filesystem characters and collapses whitespace. */
    private static String sanitizeSegment(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String cleaned = raw.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "")
                .replaceAll("\\s+", "_");
        return cleaned.isEmpty() ? fallback : cleaned;
    }
}