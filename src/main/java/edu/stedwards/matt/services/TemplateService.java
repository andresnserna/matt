package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stedwards.matt.dao.AwardsCertsDAO;
import edu.stedwards.matt.dao.EducationDAO;
import edu.stedwards.matt.dao.ExperienceDAO;
import edu.stedwards.matt.dao.ProjectsDAO;
import edu.stedwards.matt.dao.SkillDAO;
import edu.stedwards.matt.dao.TemplateDAO;
import edu.stedwards.matt.dao.UserDAO;
import edu.stedwards.matt.models.entity.AwardsCerts;
import edu.stedwards.matt.models.entity.Education;
import edu.stedwards.matt.models.entity.Experience;
import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.models.entity.Projects;
import edu.stedwards.matt.models.entity.Skill;
import edu.stedwards.matt.models.entity.Template;
import edu.stedwards.matt.models.entity.User;
import org.jsoup.Jsoup;

/** Builds application-specific prompts and sends them to the local Ollama instance. */
public class TemplateService {
    private static final String OLLAMA_GENERATE_URL = "http://localhost:11434/api/generate";
    private static final String OLLAMA_CHAT_URL = "http://localhost:11434/api/chat";
    private static final String MODEL_NAME = "llama3.2:3b";
    private static final Pattern BRACKET_FIELD_PATTERN = Pattern.compile("\\[([^\\[\\]]+)\\]");

    private final TemplateStorageService storageService = new TemplateStorageService();
    private final TemplateFieldParserService fieldParser = new TemplateFieldParserService();

    /** Loads a saved template and generates a job-specific version via conversational field-by-field AI filling. */
    public String generateApplicationDocument(Template template, JobPosting job, User signedInUser) throws Exception {
        return generateApplicationDocument(template, job, signedInUser, null);
    }

    /** Overload with optional field-by-field progress callback (completedFields, totalFields). */
    public String generateApplicationDocument(
            Template template,
            JobPosting job,
            User signedInUser,
            BiConsumer<Integer, Integer> progressCallback) throws Exception {

        if (template == null || template.getTemplateId() <= 0) {
            throw new IllegalArgumentException("Select a saved template before generating a document.");
        }
        if (job == null) throw new IllegalArgumentException("A saved job posting is required.");
        if (signedInUser == null || signedInUser.getUserID() <= 0) {
            throw new IllegalArgumentException("A signed-in user is required.");
        }
        if (job.getUserId() > 0 && job.getUserId() != signedInUser.getUserID()) {
            throw new IllegalArgumentException("The selected job posting does not belong to this user.");
        }

        Template ownedTemplate = new TemplateDAO().getTemplateById(template.getTemplateId(), signedInUser.getUserID());
        if (ownedTemplate == null) {
            throw new IllegalArgumentException("The selected template could not be found for this user.");
        }

        String htmlTemplate = storageService.loadTemplateFile(ownedTemplate.getFileLoc());
        if (htmlTemplate == null || htmlTemplate.isBlank() || htmlTemplate.contains("File not found.")) {
            throw new IllegalStateException("The selected template file could not be loaded.");
        }

        User user = new UserDAO().getUserById(signedInUser.getUserID());
        if (user == null) {
            throw new IllegalStateException("The user's profile could not be loaded.");
        }

        String userContext = buildUserContext(user);
        String jobContext = buildJobContext(job);

        System.out.println("===== Applicant context sent to Ollama =====\n" + userContext);
        System.out.println("===== Job context sent to Ollama =====\n" + jobContext);

        String filledHtml = generateMaterialConversational(htmlTemplate, userContext, jobContext, progressCallback);

        List<String> missing = findUnfilledFields(filledHtml);
        if (!missing.isEmpty()) {
            System.out.println("Warning: Unfilled fields after conversational pass: " + missing);
        }
        return filledHtml;
    }

    /**
     * Conversational AI Generate Material Flow:
     * 1. Sends userContext, jobContext, templateFieldList, and the template in the initial turn.
     * 2. Loops through each dynamic [field] one at a time in the same Ollama /api/chat conversation,
     *    passing the preceding text so the answer flows linguistically without lying.
     * 3. Programmatically replaces each [field] in the HTML template and removes the surrounding brackets.
     */
    public String generateMaterialConversational(
            String htmlTemplate,
            String userContext,
            String jobContext,
            BiConsumer<Integer, Integer> progressCallback) {

        if (htmlTemplate == null || htmlTemplate.isBlank()) {
            return "";
        }

        // Preserve exact document order of all [bracketed] dynamic fields
        List<String> templateFieldList = extractOrderedBracketFields(htmlTemplate);
        if (templateFieldList.isEmpty()) {
            return htmlTemplate;
        }

        String workingPlainText = Jsoup.parse(htmlTemplate).text();
        List<String> messages = new ArrayList<>();

        // System instructions for the entire conversation
        messages.add(chatMessage("system",
                "You are an expert career assistant filling dynamic placeholders in a job application template one field at a time.\n"
                + "STRICT RULES:\n"
                + "1. NEVER lie, exaggerate, or invent employers, degrees, dates, projects, or skills. Use ONLY verified facts from the APPLICANT PROFILE.\n"
                + "2. Tailor your wording to match the JOB POSTING naturally.\n"
                + "3. Ensure your answer flows linguistically, grammatically, and stylistically from the preceding words and sentences.\n"
                + "4. Return ONLY the exact text that goes inside the requested field—no brackets [ ], no field label, no quotation marks, no preamble, and no explanation."));

        // Turn 1: Send userContext, jobContext, templateFieldList, and template
        String initialPrompt = "APPLICANT PROFILE (verified facts only):\n" + nullSafe(userContext) + "\n\n"
                + "JOB POSTING:\n" + nullSafe(jobContext) + "\n\n"
                + "TEMPLATE FIELD LIST (in order):\n"
                + templateFieldList.stream().map(f -> "- [" + f + "]").collect(Collectors.joining("\n")) + "\n\n"
                + "TEMPLATE:\n" + workingPlainText + "\n\n"
                + "I will now ask you to fill in one field at a time in order. Reply with only READY.";

        messages.add(chatMessage("user", initialPrompt));
        String ack = sendChatToOllama(messages);
        messages.add(chatMessage("assistant", ack));

        // Loop through every dynamic field one at a time
        Map<String, String> filledValues = new LinkedHashMap<>();
        int totalFields = templateFieldList.size();

        for (int i = 0; i < totalFields; i++) {
            String rawField = templateFieldList.get(i);
            String token = "[" + rawField + "]";
            String precedingContext = extractPrecedingContext(workingPlainText, token, 240);

            String fieldPrompt = "Fill in ONLY the field: " + token + "\n"
                    + "Preceding text right before this field: \"" + precedingContext + "\"\n"
                    + "Based on the job description and truthful applicant profile, return ONLY the exact text that should replace "
                    + token + " so it flows naturally from the preceding text. Do not include brackets, labels, or commentary.";

            messages.add(chatMessage("user", fieldPrompt));
            String aiReply = cleanFieldValue(sendChatToOllama(messages));
            messages.add(chatMessage("assistant", aiReply));

            filledValues.put(rawField, aiReply);

            // Update the working plain text so subsequent fields see the actual preceding filled text
            if (!aiReply.isBlank()) {
                workingPlainText = workingPlainText.replace(token, aiReply);
            }

            if (progressCallback != null) {
                progressCallback.accept(i + 1, totalFields);
            }
        }

        // Programmatically fill all fields in the HTML template and remove surrounding brackets
        return applyFieldsProgrammatically(htmlTemplate, filledValues, null);
    }

    /** Formats stored profile fields and related records as labeled, readable facts for the model. */
    public String buildUserContext(User user) {
        int userId = user.getUserID();
        StringBuilder context = new StringBuilder();
        context.append("=== CANDIDATE PERSONAL INFO ===\n");
        append(context, "Full Name", join(user.getFirstName(), user.getLastName()));
        append(context, "First Name", user.getFirstName());
        append(context, "Last Name", user.getLastName());
        append(context, "Email", user.getEmail());
        append(context, "Phone", user.getPhoneNumber());
        append(context, "Street Address", join(user.getAddressLine1(), user.getAddressLine2()));
        append(context, "City/State/Zip", join(user.getCity(), user.getState(), user.getZip()));
        append(context, "Professional Summary", user.getSummary());

        List<Education> education = new EducationDAO().getForUser(userId);
        context.append("\n=== CANDIDATE EDUCATION ===\n");
        if (education.isEmpty()) context.append("None provided\n");
        for (int i = 0; i < education.size(); i++) {
            Education item = education.get(i);
            context.append("[Education #").append(i + 1).append("] ");
            context.append("Degree: ").append(nullSafe(item.getDegree()))
                   .append(" | Institution: ").append(nullSafe(item.getInstitution()))
                   .append(" | GPA: ").append(nullSafe(item.getGpa()))
                   .append(" | Dates: ").append(nullSafe(item.getStartDate())).append(" - ").append(nullSafe(item.getEndDate()))
                   .append("\n");
        }

        List<Experience> experience = new ExperienceDAO().getForUser(userId);
        context.append("\n=== CANDIDATE WORK EXPERIENCE ===\n");
        if (experience.isEmpty()) context.append("None provided\n");
        for (int i = 0; i < experience.size(); i++) {
            Experience item = experience.get(i);
            context.append("[Experience #").append(i + 1).append("] ");
            context.append("Title: ").append(nullSafe(item.getJobTitle()))
                   .append(" | Company: ").append(nullSafe(item.getCompanyName()))
                   .append(" | Dates: ").append(nullSafe(item.getStartDate())).append(" - ").append(nullSafe(item.getEndDate()))
                   .append("\n   Highlights: ").append(nullSafe(item.getDescription())).append("\n");
        }

        List<Skill> skills = new SkillDAO().getForUser(userId);
        context.append("\n=== CANDIDATE SKILLS ===\n");
        if (skills.isEmpty()) {
            context.append("None provided\n");
        } else {
            String skillList = skills.stream()
                    .map(s -> s.getSkillName() + (s.getProficiencyLevel() != null && !s.getProficiencyLevel().isBlank() ? " (" + s.getProficiencyLevel() + ")" : ""))
                    .collect(Collectors.joining(", "));
            context.append("Skills List: ").append(skillList).append("\n");
        }

        List<Projects> projects = new ProjectsDAO().getForUser(userId);
        context.append("\n=== CANDIDATE PROJECTS ===\n");
        if (projects.isEmpty()) context.append("None provided\n");
        for (int i = 0; i < projects.size(); i++) {
            Projects project = projects.get(i);
            context.append("[Project #").append(i + 1).append("] ")
                   .append("Title: ").append(nullSafe(project.getTitle()))
                   .append(" | URL: ").append(nullSafe(project.getUrl()))
                   .append(" | Details: ").append(nullSafe(project.getDescription())).append("\n");
        }

        List<AwardsCerts> awards = new AwardsCertsDAO().getForUser(userId);
        context.append("\n=== CANDIDATE AWARDS & CERTIFICATIONS ===\n");
        if (awards.isEmpty()) context.append("None provided\n");
        for (int i = 0; i < awards.size(); i++) {
            AwardsCerts award = awards.get(i);
            context.append("[Award #").append(i + 1).append("] ")
                   .append("Title: ").append(nullSafe(award.getTitle()))
                   .append(" | Issuer: ").append(nullSafe(award.getIssuer()))
                   .append(" | Date: ").append(nullSafe(award.getDateIssued())).append("\n");
        }

        return context.toString();
    }

    /** Formats job fields by label so job requirements are distinct from applicant facts. */
    public String buildJobContext(JobPosting job) {
        StringBuilder context = new StringBuilder();
        context.append("=== TARGET JOB POSTING ===\n");
        append(context, "Target Job Title", job.getJobTitle());
        append(context, "Target Company Name", job.getCompanyName());
        append(context, "Job Location", job.getLocation());
        if (job.getPayMin() > 0 || job.getPayMax() > 0) {
            String minimum = job.getPayMin() > 0 ? String.valueOf(job.getPayMin()) : "not specified";
            String maximum = job.getPayMax() > 0 ? String.valueOf(job.getPayMax()) : "not specified";
            String period = job.getPayPeriod() == null || job.getPayPeriod().isBlank() ? "" : " / " + job.getPayPeriod();
            append(context, "Compensation Range", minimum + " to " + maximum + period);
        }
        append(context, "Key Responsibilities", job.getResponsibilities());
        append(context, "Required Qualifications", job.getRequirements());
        append(context, "Nice-to-Have Qualifications", job.getNiceToHave());
        append(context, "Benefits", job.getBenefits());
        append(context, "Original Job Description", job.getRawText());
        return context.toString();
    }

    /** Programmatically replaces every [field] in the HTML template and removes the brackets. */
    public String applyFieldsProgrammatically(
            String htmlTemplate,
            Map<String, String> fieldValues,
            BiConsumer<Integer, Integer> progressCallback) {

        if (htmlTemplate == null || htmlTemplate.isBlank()) return "";

        List<String> allFields = extractOrderedBracketFields(htmlTemplate);
        int total = allFields.size();
        int current = 0;
        String updatedHtml = htmlTemplate;

        for (String rawField : allFields) {
            current++;
            String replacement = findMatchingValue(rawField, fieldValues);
            if (replacement != null && !replacement.isBlank()) {
                updatedHtml = updatedHtml.replace("[" + rawField + "]", replacement);
            }
            if (progressCallback != null) {
                progressCallback.accept(current, total);
            }
        }
        return updatedHtml;
    }

    /** Inspects an HTML string and returns a list of any [Placeholder] fields that are still unfilled. */
    public List<String> findUnfilledFields(String htmlContent) {
        List<String> unfilled = new ArrayList<>();
        if (htmlContent == null) return unfilled;
        Matcher matcher = BRACKET_FIELD_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String fieldName = matcher.group(1).trim();
            if (!unfilled.contains(fieldName)) {
                unfilled.add(fieldName);
            }
        }
        return unfilled;
    }

    /** Fills all [brackets] using the conversational field-by-field AI flow. */
    public String fillTemplateWithAI(String htmlTemplate, Map<String, String> jobFields, String userContext) {
        return fillTemplateWithAI(htmlTemplate, jobFields, userContext, null);
    }

    /** Overload that accepts a field-by-field progress listener (completedCount, totalCount). */
    public String fillTemplateWithAI(
            String htmlTemplate,
            Map<String, String> jobFields,
            String userContext,
            BiConsumer<Integer, Integer> progressCallback) {

        String jobContextStr = jobFields == null ? "" : jobFields.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));

        String filledHtml = generateMaterialConversational(htmlTemplate, userContext, jobContextStr, progressCallback);

        // Fallback: if any direct jobFields keys still remain unfilled, apply them programmatically
        if (jobFields != null && !jobFields.isEmpty()) {
            filledHtml = applyFieldsProgrammatically(filledHtml, jobFields, null);
        }
        return filledHtml;
    }

    public String extractTemplateFields(String templateHtml) {
        return fieldParser.extractFields(templateHtml).stream().collect(Collectors.joining(", "));
    }

    /** Remove any model-written preamble by anchoring on the template's first six visible words. */
    public String trimBeforeTemplateStart(String generatedOutput, String originalTemplate) {
        if (generatedOutput == null || generatedOutput.isBlank()) {
            throw new IllegalStateException("The model returned an empty document.");
        }
        List<String> openingWords = templateOpeningWords(originalTemplate, 6);
        if (openingWords.isEmpty()) throw new IllegalArgumentException("The template has no opening text to match.");

        StringBuilder expression = new StringBuilder("(?is)");
        for (int i = 0; i < openingWords.size(); i++) {
            if (i > 0) expression.append("(?:\\s|<[^>]*>|\\*|_)+");
            expression.append("\\Q").append(openingWords.get(i)).append("\\E");
        }
        Matcher match = Pattern.compile(expression.toString()).matcher(generatedOutput);
        if (!match.find()) {
            throw new IllegalStateException("The model output did not contain the template's opening text; no PDF was saved." + generatedOutput);
        }
        int start = match.start();
        int boundary = start;
        while (true) {
            int tagEnd = generatedOutput.lastIndexOf('>', boundary - 1);
            int tagStart = tagEnd >= 0 ? generatedOutput.lastIndexOf('<', tagEnd) : -1;
            if (tagStart < 0 || generatedOutput.charAt(tagStart + 1) == '/') break;
            String tag = generatedOutput.substring(tagStart, tagEnd + 1);
            if (tag.startsWith("<!--") || tag.startsWith("<!")) break;
            start = tagStart;
            boundary = tagStart;
        }
        return generatedOutput.substring(start).strip();
    }

    /** Compatibility entry point for callers that already have flattened contexts. */
    public String generateDocument(String templateName, Map<String, String> jobFields, String userProfileContext) {
        return sendToOllama("Create application material for template " + templateName + ". Job information:\n"
                + jobFields + "\nApplicant information:\n" + userProfileContext);
    }

    // ==================== Private Helpers ====================

    private static List<String> extractOrderedBracketFields(String htmlTemplate) {
        Set<String> ordered = new LinkedHashSet<>();
        Matcher matcher = BRACKET_FIELD_PATTERN.matcher(htmlTemplate == null ? "" : htmlTemplate);
        while (matcher.find()) {
            String rawInner = matcher.group(1);
            if (rawInner != null && !rawInner.isBlank()) {
                ordered.add(rawInner);
            }
        }
        return new ArrayList<>(ordered);
    }

    private static String extractPrecedingContext(String plainText, String token, int maxChars) {
        int idx = plainText.indexOf(token);
        if (idx <= 0) {
            return "(Start of document)";
        }
        int start = Math.max(0, idx - maxChars);
        return plainText.substring(start, idx).trim();
    }

    private static String cleanFieldValue(String raw) {
        if (raw == null) return "";
        return raw.trim()
                .replaceAll("^\\[|\\]$", "")
                .replaceAll("^\"|\"$", "")
                .replaceAll("\\r?\\n+", " ")
                .trim();
    }

    private String findMatchingValue(String fieldName, Map<String, String> fieldValues) {
        if (fieldValues == null) return null;
        if (fieldValues.containsKey(fieldName)) {
            return fieldValues.get(fieldName);
        }
        String cleanField = fieldName.replaceAll("^\\[|\\]$", "").trim();
        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
            String cleanKey = entry.getKey().replaceAll("^\\[|\\]$", "").trim();
            if (cleanKey.equalsIgnoreCase(cleanField)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private static String chatMessage(String role, String content) {
        return "{\"role\":\"" + role + "\",\"content\":\"" + escapeJson(content) + "\"}";
    }

    private String sendChatToOllama(List<String> messageJsonObjects) {
        String payload = "{\"model\":\"" + MODEL_NAME + "\",\"stream\":false,\"messages\":["
                + String.join(",", messageJsonObjects) + "]}";
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_CHAT_URL))
                .timeout(Duration.ofMinutes(3))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Ollama chat returned HTTP " + response.statusCode() + ".");
            }
            return extractJsonStringField(response.body(), "\"content\"");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Document generation was interrupted.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not connect to Ollama at " + OLLAMA_CHAT_URL + ".", e);
        }
    }

    private String sendToOllama(String prompt) {
        String payload = "{\"model\":\"" + MODEL_NAME + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_GENERATE_URL))
                .timeout(Duration.ofMinutes(3))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Ollama returned HTTP " + response.statusCode() + ".");
            }
            return extractJsonStringField(response.body(), "\"response\"");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Document generation was interrupted.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not connect to Ollama at " + OLLAMA_GENERATE_URL + ".", e);
        }
    }

    private static String extractJsonStringField(String json, String key) {
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) throw new IllegalStateException("Ollama response did not contain " + key + ".");
        int start = json.indexOf(':', keyIndex + key.length());
        if (start < 0) throw new IllegalStateException("Ollama response was malformed.");
        start++;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length() || json.charAt(start) != '"') throw new IllegalStateException("Ollama response was malformed.");
        StringBuilder value = new StringBuilder();
        for (int i = start + 1; i < json.length(); i++) {
            char ch = json.charAt(i);
            if (ch == '"') return value.toString();
            if (ch != '\\') {
                value.append(ch);
                continue;
            }
            if (++i >= json.length()) break;
            char escaped = json.charAt(i);
            switch (escaped) {
                case '"', '\\', '/' -> value.append(escaped);
                case 'b' -> value.append('\b');
                case 'f' -> value.append('\f');
                case 'n' -> value.append('\n');
                case 'r' -> value.append('\r');
                case 't' -> value.append('\t');
                case 'u' -> {
                    if (i + 4 >= json.length()) throw new IllegalStateException("Ollama response had an invalid unicode escape.");
                    value.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
                    i += 4;
                }
                default -> throw new IllegalStateException("Ollama response had an invalid escape sequence.");
            }
        }
        throw new IllegalStateException("Ollama response was incomplete.");
    }

    private static List<String> templateOpeningWords(String html, int wordLimit) {
        String visibleText = Jsoup.parse(html == null ? "" : html).body().text()
                .replaceAll("\\[[^\\]]*]", " ").trim();
        if (visibleText.isEmpty()) return List.of();
        return Arrays.stream(visibleText.split("\\s+"))
                .limit(wordLimit)
                .map(word -> word.replaceAll("^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}.,!?;:'’\\u2019-]+$", ""))
                .filter(word -> !word.isBlank())
                .toList();
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder(value.length() + 32);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) escaped.append(String.format("\\u%04x", (int) ch));
                    else escaped.append(ch);
                }
            }
        }
        return escaped.toString();
    }

    private static void append(StringBuilder output, String label, String value) {
        if (value != null && !value.isBlank()) output.append(label).append(": ").append(value.trim()).append('\n');
    }

    private static String nullSafe(String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    private static String join(String... values) {
        return java.util.Arrays.stream(values).filter(value -> value != null && !value.isBlank())
                .map(String::trim).collect(Collectors.joining(" | "));
    }
}