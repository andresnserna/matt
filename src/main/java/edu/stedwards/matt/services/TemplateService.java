package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
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
        String prompt = "You are helping prepare a truthful job application document. Return the complete HTML template.\n"
                + "Only replace the text of the bracketed placeholders [like this]. Preserve every other character and all HTML tags. "
                + "Do not add or remove sections, tags, formatting, or commentary. Do not return markdown fences.\n"
                + "Use relevant job-posting keywords naturally and emphasize the applicant's strongest matching skills and experience. "
                + "Never invent employers, dates, credentials, skills, achievements, or experience. If a requested detail is unavailable, "
                + "use a truthful transferable skill or leave the placeholder concise and neutral.\n"
                + "Known placeholders: " + fields + "\n\n"
                + "APPLICANT PROFILE (trusted facts; do not infer facts not present):\n" + buildUserContext(user) + "\n\n"
                + "JOB POSTING:\n" + buildJobContext(job) + "\n\n"
                + "HTML TEMPLATE (preserve its structure):\n" + htmlTemplate;
        return sendToOllama(prompt);
    }

    /** Format stored profile fields and related records as labeled, readable facts for the model. */
    public String buildUserContext(User user) {
        int userId = user.getUserID();
        StringBuilder context = new StringBuilder();
        append(context, "Name", join(user.getFirstName(), user.getLastName()));
        append(context, "Email", user.getEmail());
        append(context, "Phone", user.getPhoneNumber());
        append(context, "Location", join(user.getCity(), user.getState(), user.getZip()));
        append(context, "Summary", user.getSummary());

        List<Education> education = new EducationDAO().getForUser(userId);
        context.append("Education:\n");
        if (education.isEmpty()) context.append("- None provided\n");
        for (Education item : education) {
            String details = join(item.getDegree(), item.getInstitution());
            details = join(details, item.getStartDate(), item.getEndDate());
            if (item.getGpa() != null && !item.getGpa().isBlank()) details = join(details, "GPA " + item.getGpa());
            context.append("- ").append(orUnknown(details)).append('\n');
        }

        List<Experience> experience = new ExperienceDAO().getForUser(userId);
        context.append("Experience:\n");
        if (experience.isEmpty()) context.append("- None provided\n");
        for (Experience item : experience) {
            context.append("- ").append(orUnknown(join(item.getJobTitle(), item.getCompanyName(), item.getStartDate(), item.getEndDate())))
                    .append(appendDetail(item.getDescription())).append('\n');
        }

        List<Skill> skills = new SkillDAO().getForUser(userId);
        context.append("Skills:\n");
        if (skills.isEmpty()) context.append("- None provided\n");
        for (Skill skill : skills) {
            context.append("- ").append(orUnknown(join(skill.getSkillName(), skill.getProficiencyLevel()))).append('\n');
        }

        List<AwardsCerts> awards = new AwardsCertsDAO().getForUser(userId);
        context.append("Awards and certifications:\n");
        if (awards.isEmpty()) context.append("- None provided\n");
        for (AwardsCerts award : awards) {
            context.append("- ").append(orUnknown(join(award.getTitle(), award.getIssuer(), award.getDateIssued())))
                    .append(appendDetail(award.getDescription())).append('\n');
        }

        List<Projects> projects = new ProjectsDAO().getForUser(userId);
        context.append("Projects:\n");
        if (projects.isEmpty()) context.append("- None provided\n");
        for (Projects project : projects) {
            context.append("- ").append(orUnknown(project.getTitle())).append(appendDetail(project.getDescription()));
            if (project.getUrl() != null && !project.getUrl().isBlank()) context.append(" [URL: ").append(project.getUrl()).append(']');
            context.append('\n');
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
            append(context, "Pay range", job.getPayMin() + "–" + job.getPayMax() + " " + orUnknown(job.getPayPeriod()));
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
        return sendToOllama(prompt);
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

    private static String orUnknown(String value) {
        return value == null || value.isBlank() ? "Details not provided" : value;
    }

    private static String appendDetail(String detail) {
        return detail == null || detail.isBlank() ? "" : " — " + detail.trim();
    }
}
