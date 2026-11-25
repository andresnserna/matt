package edu.stedwards.matt.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parsing helpers for the textual output returned by local Ollama commands. */
public final class OllamaUtils {

    // Matches indented "key   value" rows where key and value are separated by 2+ spaces or a tab
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^\\s+([A-Za-z0-9 _-]+?)(?:\\s{2,}|\\t+)(.+)$");

    private OllamaUtils() { }

    /**
     * Parse model architecture, parameters, and attributes from `ollama show` output.
     * Returns an ordered map of attribute names to values (empty map if output is unavailable).
     */
    public static Map<String, String> parseModelDetails(String showOutput) {
        if (showOutput == null || showOutput.isBlank()) {
            return Map.of();
        }

        String lower = showOutput.toLowerCase(Locale.ROOT);
        if (lower.contains("error:") || lower.contains("could not connect")) {
            return Map.of();
        }

        Map<String, String> details = new LinkedHashMap<>();
        List<String> capabilities = new ArrayList<>();
        String currentSection = "";

        String[] lines = showOutput.split("\\r?\\n");
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            String trimmed = line.trim();

            // Detect top-level section headers (e.g., "  Model", "  Capabilities", "  Parameters", "  License")
            if (isSectionHeader(line, trimmed)) {
                currentSection = trimmed.toLowerCase(Locale.ROOT);
                continue;
            }

            // Skip verbose/non-metadata sections
            if (currentSection.equals("license")
                    || currentSection.equals("template")
                    || currentSection.equals("system")) {
                continue;
            }

            if (currentSection.equals("capabilities")) {
                capabilities.add(trimmed);
                continue;
            }

            // Parse "key    value" pairs in Model / Parameters / default sections
            Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    // Combine repeated parameter keys (like multiple "stop" tokens) instead of overwriting
                    details.merge(key, value, (existing, next) -> existing + ", " + next);
                }
                continue;
            }

            // Fallback for "key: value" formatting
            int colonIdx = trimmed.indexOf(':');
            if (colonIdx > 0 && colonIdx < trimmed.length() - 1) {
                String key = trimmed.substring(0, colonIdx).trim();
                String value = trimmed.substring(colonIdx + 1).trim();
                if (!key.isEmpty() && !value.isEmpty()) {
                    details.putIfAbsent(key, value);
                }
            }
        }

        if (!capabilities.isEmpty()) {
            details.put("capabilities", String.join(", ", capabilities));
        }

        return details;
    }

    /**
     * Parse the active runner/model name from `ollama ps` output.
     * Returns null if Ollama is disconnected or no model is currently loaded.
     */
    public static String parseCurrentRunner(String psOutput) {
        if (psOutput == null || psOutput.isBlank() || isErrorOutput(psOutput)) {
            return null;
        }

        String[] lines = psOutput.split("\\r?\\n");
        boolean passedHeader = false;

        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            String trimmed = line.trim();
            if (!passedHeader) {
                if (isPsHeader(trimmed)) {
                    passedHeader = true;
                }
                continue;
            }

            // First non-empty row after the header contains the active model name in column 1
            String[] columns = trimmed.split("\\s+");
            if (columns.length > 0 && !columns[0].isBlank()) {
                return columns[0].trim();
            }
        }

        return null;
    }

    /**
     * Interpret `ollama ps` output as "Running", "Idle", or "Disconnected".
     */
    public static String parseStatus(String psOutput) {
        if (psOutput == null || psOutput.isBlank() || isErrorOutput(psOutput)) {
            return "Disconnected";
        }

        String[] lines = psOutput.split("\\r?\\n");
        boolean hasHeader = false;
        for (String line : lines) {
            if (line != null && isPsHeader(line.trim())) {
                hasHeader = true;
                break;
            }
        }

        if (!hasHeader) {
            return "Disconnected";
        }

        String activeRunner = parseCurrentRunner(psOutput);
        return (activeRunner != null && !activeRunner.isBlank()) ? "Running" : "Idle";
    }

    private static boolean isSectionHeader(String rawLine, String trimmed) {
        if (trimmed.contains("  ") || trimmed.contains("\t") || trimmed.contains(":")) {
            return false;
        }
        int leadingSpaces = countLeadingSpaces(rawLine);
        return leadingSpaces <= 2 && trimmed.matches("[A-Za-z][A-Za-z0-9 _-]*");
    }

    private static int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
    }

    private static boolean isPsHeader(String trimmedLine) {
        String upper = trimmedLine.toUpperCase(Locale.ROOT);
        return upper.startsWith("NAME")
                && (upper.contains("ID") || upper.contains("SIZE") || upper.contains("PROCESSOR"));
    }

    private static boolean isErrorOutput(String output) {
        String lower = output.toLowerCase(Locale.ROOT);
        return lower.contains("error:")
                || lower.contains("could not connect")
                || lower.contains("connection refused")
                || lower.contains("not recognized")
                || lower.contains("command not found")
                || lower.contains("failed to connect");
    }
}