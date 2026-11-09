package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llama3.2:3b";

    private final TemplateStorageService storageService = new TemplateStorageService();
    private final TemplateFieldParserService fieldParser = new TemplateFieldParserService();

    /** Loads a saved template and generates a job-specific version from the user's stored profile. */
    public String generateApplicationDocument(Template template, JobPosting job, User signedInUser) throws Exception {
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
        if (ownedTemplate == null) throw new IllegalArgumentException("The selected template could not be found for this user.");
        String htmlTemplate = storageService.loadTemplateFile(ownedTemplate.getFileLoc());
        if (htmlTemplate == null || htmlTemplate.isBlank() || htmlTemplate.contains("File not found.")) {
            throw new IllegalStateException("The selected template file could not be loaded.");
        }

        User user = new UserDAO().getUserById(signedInUser.getUserID());
        if (user == null) throw new IllegalStateException("The user's profile could not be loaded.");
        String fields = extractTemplateFields(htmlTemplate);
        String userContext = buildUserContext(user);
        String jobContext = buildJobContext(job);
        List<String> openingWords = templateOpeningWords(htmlTemplate, 6);
        System.out.println("===== Applicant context sent to Ollama =====\n" + userContext);
        System.out.println("===== Job context sent to Ollama =====\n" + jobContext);
        System.out.println("===== Opening Words of Template =====\n" + openingWords);

        String prompt = "You are filling the user's supplied application template. Return ONLY that template, filled in. "
                + "Do not write a resume, a second document, a heading, an introduction, a summary, or commentary.\n"
                + "The first words of your response must be the template's opening words: \""
                + String.join(" ", openingWords) + "\". Do not put anything before them.\n"
                + "Only replace text inside bracketed placeholders [like this]. Preserve every other character and all HTML tags. "
                + "Do not add or remove sections, tags, formatting, or commentary. Do not return markdown fences.\n"
                + "Use relevant job-posting keywords naturally and emphasize the applicant's strongest matching skills and experience. "
                + "The APPLICANT PROFILE is the only source for the applicant's employers, schools, dates, credentials, skills, projects, and achievements. "
                + "Never use employer, school, project, or achievement examples from the job posting or your general knowledge. "
                + "Never invent facts. If a requested detail is unavailable, use a truthful transferable skill or leave that placeholder concise and neutral.\n"
                + "Known placeholders: " + fields + "\n\n"
                + "APPLICANT PROFILE (trusted facts; do not infer facts not present):\n" + userContext + "\n\n"
                + "JOB POSTING (describes the employer and role, not the applicant's history):\n" + jobContext + "\n\n"
                + "HTML TEMPLATE (preserve its structure):\n" + htmlTemplate;
        return trimBeforeTemplateStart(sendToOllama(prompt), htmlTemplate);
    }

    /** Format stored profile fields and related records as labeled, readable facts for the model. */
    public String buildUserContext(User user) {
        int userId = user.getUserID();
        StringBuilder context = new StringBuilder();
        append(context, "Name", join(user.getFirstName(), user.getLastName()));
        append(context, "Email", user.getEmail());
        append(context, "Phone", user.getPhoneNumber());
        append(context, "Address", join(user.getAddressLine1(), user.getAddressLine2(), user.getCity(), user.getState(), user.getZip()));
        append(context, "Location", join(user.getCity(), user.getState(), user.getZip()));
        append(context, "Summary", user.getSummary());

        List<Education> education = new EducationDAO().getForUser(userId);
        context.append("Education records:\n");
        if (education.isEmpty()) context.append("Education: None provided\n");
        for (int i = 0; i < education.size(); i++) {
            Education item = education.get(i);
            context.append("Education ").append(i + 1).append(":\n");
            append(context, "Institution", item.getInstitution());
            append(context, "Degree", item.getDegree());
            append(context, "GPA", item.getGpa());
            append(context, "Start date", item.getStartDate());
            append(context, "End date", item.getEndDate());
        }

        List<Experience> experience = new ExperienceDAO().getForUser(userId);
        context.append("Experience records:\n");
        if (experience.isEmpty()) context.append("Experience: None provided\n");
        for (int i = 0; i < experience.size(); i++) {
            Experience item = experience.get(i);
            context.append("Experience ").append(i + 1).append(":\n");
            append(context, "Job title", item.getJobTitle());
            append(context, "Company", item.getCompanyName());
            append(context, "Start date", item.getStartDate());
            append(context, "End date", item.getEndDate());
            append(context, "Description", item.getDescription());
        }

        List<Skill> skills = new SkillDAO().getForUser(userId);
        context.append("Skill records:\n");
        if (skills.isEmpty()) context.append("Skills: None provided\n");
        for (int i = 0; i < skills.size(); i++) {
            Skill skill = skills.get(i);
            context.append("Skill ").append(i + 1).append(":\n");
            append(context, "Skill name", skill.getSkillName());
            append(context, "Proficiency", skill.getProficiencyLevel());
        }

        List<AwardsCerts> awards = new AwardsCertsDAO().getForUser(userId);
        context.append("Award and certification records:\n");
        if (awards.isEmpty()) context.append("Awards and certifications: None provided\n");
        for (int i = 0; i < awards.size(); i++) {
            AwardsCerts award = awards.get(i);
            context.append("Award/certification ").append(i + 1).append(":\n");
            append(context, "Title", award.getTitle());
            append(context, "Issuer", award.getIssuer());
            append(context, "Date issued", award.getDateIssued());
            append(context, "Description", award.getDescription());
        }

        List<Projects> projects = new ProjectsDAO().getForUser(userId);
        context.append("Project records:\n");
        if (projects.isEmpty()) context.append("Projects: None provided\n");
        for (int i = 0; i < projects.size(); i++) {
            Projects project = projects.get(i);
            context.append("Project ").append(i + 1).append(":\n");
            append(context, "Title", project.getTitle());
            append(context, "Description", project.getDescription());
            append(context, "URL", project.getUrl());
        }
        return context.toString();
    }

    /** Format job fields by label so job requirements are distinct from applicant facts. */
    public String buildJobContext(JobPosting job) {
        StringBuilder context = new StringBuilder();
        append(context, "Job title", job.getJobTitle());
        append(context, "Company", job.getCompanyName());
        append(context, "Location", job.getLocation());
        if (job.getPayMin() > 0 || job.getPayMax() > 0) {
            String minimum = job.getPayMin() > 0 ? String.valueOf(job.getPayMin()) : "not specified";
            String maximum = job.getPayMax() > 0 ? String.valueOf(job.getPayMax()) : "not specified";
            String period = job.getPayPeriod() == null || job.getPayPeriod().isBlank() ? "" : " / " + job.getPayPeriod();
            append(context, "Pay range", minimum + " to " + maximum + period);
        }
        append(context, "Responsibilities", job.getResponsibilities());
        append(context, "Requirements", job.getRequirements());
        append(context, "Preferred qualifications", job.getNiceToHave());
        append(context, "Benefits", job.getBenefits());
        append(context, "Full posting text", job.getRawText());
        return context.toString();
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
        // Preserve opening tags directly around the matched text (for example, <p><strong>Dear...).
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

    /** Compatibility entry point for callers that already have flattened contexts. */
    public String generateDocument(String templateName, Map<String, String> jobFields, String userProfileContext) {
        return sendToOllama("Create application material for template " + templateName + ". Job information:\n"
                + jobFields + "\nApplicant information:\n" + userProfileContext);
    }

    public String fillTemplateWithAI(String htmlTemplate, Map<String, String> jobFields, String userContext) {
        String fields = extractTemplateFields(htmlTemplate);
        String prompt = "Return the complete HTML template, replacing only text inside bracketed placeholders. "
                + "Preserve all other text and HTML tags exactly. Do not invent applicant facts. Use relevant job keywords "
                + "naturally, emphasize truthful matching skills, and return no markdown fences or commentary.\n"
                + "Placeholders: " + fields + "\nApplicant facts:\n" + userContext
                + "\nJob facts:\n" + jobFields + "\nHTML template:\n" + htmlTemplate;
        return trimBeforeTemplateStart(sendToOllama(prompt), htmlTemplate);
    }

    private String sendToOllama(String prompt) {
        String payload = "{\"model\":\"" + MODEL_NAME + "\",\"prompt\":\"" + escapeJson(prompt)
                + "\",\"stream\":false}";
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(OLLAMA_URL))
                .timeout(Duration.ofMinutes(3)).header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Ollama returned HTTP " + response.statusCode() + ".");
            }
            return extractResponseText(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Document generation was interrupted.", e);
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Could not connect to Ollama at " + OLLAMA_URL + ".", e);
        }
    }

    private static String extractResponseText(String json) {
        String key = "\"response\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex < 0) throw new IllegalStateException("Ollama response did not contain generated text.");
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

    private static String join(String... values) {
        return java.util.Arrays.stream(values).filter(value -> value != null && !value.isBlank())
                .map(String::trim).collect(Collectors.joining(" | "));
    }

}
