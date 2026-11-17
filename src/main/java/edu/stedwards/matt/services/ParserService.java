package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stedwards.matt.models.entity.JobPosting;

public class ParserService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llama3.2:3b"; 
    // private static final String MODEL_NAME = "gemma4:31b-cloud";

    private String extractResponseText(String json) {
        Matcher responseMatcher = Pattern.compile("\"response\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"")
                .matcher(json);
        if (!responseMatcher.find()) return "";

        String encoded = responseMatcher.group(1);
        StringBuilder response = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < encoded.length(); i++) {
            char ch = encoded.charAt(i);
            if (escaped) {
                switch (ch) {
                    case 'n' -> response.append('\n');
                    case 'r' -> response.append('\r');
                    case 't' -> response.append('\t');
                    case '"', '\\', '/' -> response.append(ch);
                    case 'b' -> response.append('\b');
                    case 'f' -> response.append('\f');
                    default -> response.append('\\').append(ch);
                }
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else {
                response.append(ch);
            }
        }
        if (escaped) response.append('\\');
        return response.toString();
    }

    private boolean hasResponseField(String json) {
        return Pattern.compile("\"response\"\\s*:").matcher(json).find();
    }

    // --- JOB POSTING PARSING ---

    private void mapAiResponseToEntity(String aiText, JobPosting job) {
        try {
            String fieldLabels = "Title|Company|Location|PayMin|PayMax|PayPeriod|Responsibilities|Requirements|NiceToHave|Benefits";
            Pattern fields = Pattern.compile("(?i)(?:^|[|\\n])\\s*(" + fieldLabels + ")\\s*:\\s*(.*?)(?=\\s*(?:[|\\n])\\s*(?:" + fieldLabels + ")\\s*:|$)");
            Matcher matcher = fields.matcher(aiText);
            while (matcher.find()) {
                String value = matcher.group(2).trim();
                switch (matcher.group(1).toLowerCase()) {
                    case "title" -> job.setJobTitle(value);
                    case "company" -> job.setCompanyName(value);
                    case "location" -> job.setLocation(value);
                    case "paymin" -> { try { job.setPayMin(Double.parseDouble(value.replaceAll("[^\\d.]", ""))); } catch (Exception ignored) { } }
                    case "paymax" -> { try { job.setPayMax(Double.parseDouble(value.replaceAll("[^\\d.]", ""))); } catch (Exception ignored) { } }
                    case "payperiod" -> job.setPayPeriod(value);
                    case "responsibilities" -> job.setResponsibilities(value);
                    case "requirements" -> job.setRequirements(value);
                    case "nicetohave" -> job.setNiceToHave(value);
                    case "benefits" -> job.setBenefits(value);
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse AI format structure.");
        }
    }

    private boolean verifyNoHallucinations(JobPosting job) {
        String originalText = job.getRawText().toLowerCase();
        boolean valid = true;
        
        if (job.getCompanyName() != null && !originalText.contains(job.getCompanyName().toLowerCase())) {
            job.setCompanyName("Validation Failed");
            job.setSpamScore(job.getSpamScore() + 25.0);
            valid = false;
        }
        
        return valid;
    }

    public JobPosting parseJobDescription(String rawText) {
        JobPosting job = new JobPosting();
        job.setRawText(rawText);

        String cleanText = rawText.replace("\"", "'").replace("\n", " ").replace("\r", " ");
        String prompt = "Extract the following from the job description: Title, Company, Location, PayMin (number only), PayMax (number only), PayPeriod, Responsibilities, Requirements, NiceToHave, Benefits. "
                      + "Format EXACTLY as: Title: [val] | Company: [val] | Location: [val] | PayMin: [val] | PayMax: [val] | PayPeriod: [val] | Responsibilities: [val] | Requirements: [val] | NiceToHave: [val] | Benefits: [val]. "
                      + "Do not add any other conversational text. Text: " + cleanText;

        String jsonPayload = "{"
                + "\"model\": \"" + MODEL_NAME + "\","
                + "\"prompt\": \"" + prompt + "\","
                + "\"stream\": false"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    System.err.println("Ollama job parse returned HTTP " + response.statusCode());
                    job.setStatus("Needs Review");
                    return job;
                }
                String aiResponse = extractResponseText(response.body());
                if (aiResponse.isBlank()) {
                    System.err.println("Ollama response was empty or had no response field (HTTP "
                            + response.statusCode() + ", body length " + response.body().length()
                            + ", response field present: " + hasResponseField(response.body()) + ").");
                    job.setStatus("Needs Review");
                    return job;
                }
                job.setNormalizedText(aiResponse);
                
                mapAiResponseToEntity(aiResponse, job);

                if (job.getJobTitle() == null && job.getCompanyName() == null && job.getLocation() == null
                        && job.getResponsibilities() == null && job.getRequirements() == null) {
                    System.err.println("Ollama returned text, but no job fields could be mapped. Check job.normalizedText.");
                    job.setStatus("Needs Review");
                    return job;
                }

                if (verifyNoHallucinations(job)) {
                    job.setStatus("Parsed");
                    return job; 
                } else {
                    System.out.println("Hallucination detected on attempt " + attempt + ". Retrying...");
                }
            } catch (Exception e) {
                System.out.println("Error communicating with Ollama: " + e.getMessage());
                break; 
            }
        }

        System.out.println("Max retries reached. Flagging job for manual review.");
        job.setStatus("Needs Review");
        return job;
    }

    // --- RESUME / USER PROFILE PARSING ---

    private boolean verifyResumeNoHallucinations(Map<String, String> resumeData, String rawText) {
        String originalText = rawText.toLowerCase();
        boolean valid = true;
        
        String email = resumeData.get("Email");
        if (email != null && !email.isBlank() && !originalText.contains(email.toLowerCase())) {
            resumeData.put("Email", "Validation Failed");
            valid = false;
        }
        
        String lastName = resumeData.get("LastName");
        if (lastName != null && !lastName.isBlank() && !originalText.contains(lastName.toLowerCase())) {
            resumeData.put("LastName", "Validation Failed");
            valid = false;
        }

        return valid;
    }

    public Map<String, String> parseResume(String rawText) {
        Map<String, String> resumeData = new LinkedHashMap<>();
        
        String cleanText = rawText.replace("\"", "'").replace("\n", " ").replace("\r", " ");
        // 1. Updated Prompt to ask for the new entities (Education, Job, Project, Award, Website)
        String prompt = "Extract candidate details from this resume: FirstName, LastName, Email, Phone, Address1, Address2, City, State, Zip, Summary, Skills (comma-separated), EducationInstitution, EducationDegree, JobCompany, JobTitle, ProjectTitle, ProjectUrl, AwardTitle, WebsitePlatform, WebsiteUrl. "
                      + "Format EXACTLY as: FirstName: [val] | LastName: [val] | Email: [val] | Phone: [val] | Address1: [val] | Address2: [val] | City: [val] | State: [val] | Zip: [val] | Summary: [val] | Skills: [val] | EducationInstitution: [val] | EducationDegree: [val] | JobCompany: [val] | JobTitle: [val] | ProjectTitle: [val] | ProjectUrl: [val] | AwardTitle: [val] | WebsitePlatform: [val] | WebsiteUrl: [val]. "
                      + "Do not add any other conversational text. If a value is missing, leave it blank. Resume: " + cleanText;

        String jsonPayload = "{"
                + "\"model\": \"" + MODEL_NAME + "\","
                + "\"prompt\": \"" + prompt + "\","
                + "\"stream\": false"
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(OLLAMA_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                String aiResponse = extractResponseText(response.body());
                
                String normalizedText = aiResponse.replace("|", "\n");
                String[] lines = normalizedText.split("\n");
                
                // 2. Updated parsing loop to catch the new fields and map them to the keys the UI expects
                for (String line : lines) {
                    String lowerLine = line.toLowerCase();
                    if (lowerLine.contains("firstname:")) resumeData.put("FirstName", line.substring(lowerLine.indexOf("firstname:") + 10).trim());
                    else if (lowerLine.contains("lastname:")) resumeData.put("LastName", line.substring(lowerLine.indexOf("lastname:") + 9).trim());
                    else if (lowerLine.contains("email:")) resumeData.put("Email", line.substring(lowerLine.indexOf("email:") + 6).trim());
                    else if (lowerLine.contains("phone:")) resumeData.put("Phone", line.substring(lowerLine.indexOf("phone:") + 6).trim());
                    else if (lowerLine.contains("address1:")) resumeData.put("Address1", line.substring(lowerLine.indexOf("address1:") + 9).trim());
                    else if (lowerLine.contains("address2:")) resumeData.put("Address2", line.substring(lowerLine.indexOf("address2:") + 9).trim());
                    else if (lowerLine.contains("city:")) resumeData.put("City", line.substring(lowerLine.indexOf("city:") + 5).trim());
                    else if (lowerLine.contains("state:")) resumeData.put("State", line.substring(lowerLine.indexOf("state:") + 6).trim());
                    else if (lowerLine.contains("zip:")) resumeData.put("Zip", line.substring(lowerLine.indexOf("zip:") + 4).trim());
                    else if (lowerLine.contains("summary:")) resumeData.put("Summary", line.substring(lowerLine.indexOf("summary:") + 8).trim());
                    else if (lowerLine.contains("skills:")) resumeData.put("Skills", line.substring(lowerLine.indexOf("skills:") + 7).trim());
                    
                    // New Parsing Hooks
                    else if (lowerLine.contains("educationinstitution:")) resumeData.put("Education_Institution", line.substring(lowerLine.indexOf("educationinstitution:") + 21).trim());
                    else if (lowerLine.contains("educationdegree:")) resumeData.put("Education_Degree", line.substring(lowerLine.indexOf("educationdegree:") + 16).trim());
                    else if (lowerLine.contains("jobcompany:")) resumeData.put("Experience_Company", line.substring(lowerLine.indexOf("jobcompany:") + 11).trim());
                    else if (lowerLine.contains("jobtitle:")) resumeData.put("Experience_Title", line.substring(lowerLine.indexOf("jobtitle:") + 9).trim());
                    else if (lowerLine.contains("projecttitle:")) resumeData.put("Project_Title", line.substring(lowerLine.indexOf("projecttitle:") + 13).trim());
                    else if (lowerLine.contains("projecturl:")) resumeData.put("Project_Url", line.substring(lowerLine.indexOf("projecturl:") + 11).trim());
                    else if (lowerLine.contains("awardtitle:")) resumeData.put("Award_Title", line.substring(lowerLine.indexOf("awardtitle:") + 11).trim());
                    else if (lowerLine.contains("websiteplatform:")) resumeData.put("Website_Platform", line.substring(lowerLine.indexOf("websiteplatform:") + 16).trim());
                    else if (lowerLine.contains("websiteurl:")) resumeData.put("Website_Url", line.substring(lowerLine.indexOf("websiteurl:") + 11).trim());
                }

                if (verifyResumeNoHallucinations(resumeData, rawText)) {
                    return resumeData; 
                } else {
                    System.out.println("Resume hallucination detected on attempt " + attempt + ". Retrying...");
                }

            } catch (Exception e) {
                System.out.println("Error communicating with Ollama for resume parsing: " + e.getMessage());
                break;
            }
        }

        System.out.println("Max retries reached for resume. Flagging for manual review.");
        return resumeData;
    }
}
