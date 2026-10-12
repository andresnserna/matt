package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.stedwards.matt.models.entity.JobPosting;


public class ParserService {

    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llama3.2:3b"; // Change to your preferred local model


    // pull the "response" field out of Ollama's raw JSON string
    private String extractResponseText(String json) {
        String targetKey = "\"response\":\"";
        int startIndex = json.indexOf(targetKey) + targetKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (startIndex > targetKey.length() - 1 && endIndex > startIndex) {
            // Un-escape newline characters from the JSON response
            return json.substring(startIndex, endIndex).replace("\\n", " ");
        }
        return "";
    }

    // split the AI's formatted string into the JobPosting object
    private void mapAiResponseToEntity(String aiText, JobPosting job) {
        try {
            // Normalize the text to handle both newlines and pipe characters
            String normalizedText = aiText.replace("|", "\n");
            String[] lines = normalizedText.split("\n");
            
            for (String line : lines) {
                if (line.toLowerCase().contains("title:")) {
                    job.setJobTitle(line.substring(line.toLowerCase().indexOf("title:") + 6).trim());
                }
                else if (line.toLowerCase().contains("company:")) {
                    job.setCompanyName(line.substring(line.toLowerCase().indexOf("company:") + 8).trim());
                }
                else if (line.toLowerCase().contains("location:")) {
                    job.setLocation(line.substring(line.toLowerCase().indexOf("location:") + 9).trim());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse AI format structure.");
        }
    }

    // Validate that the AI did not hallucinate 
    private boolean verifyNoHallucinations(JobPosting job) {
        String originalText = job.getRawText().toLowerCase();
        boolean valid = true;
        
        // Check Company
        if (job.getCompanyName() != null && !originalText.contains(job.getCompanyName().toLowerCase())) {
            job.setCompanyName("Validation Failed");
            job.setSpamScore(job.getSpamScore() + 25.0);
            valid = false;
        }
        
        // Check Location
        if (job.getLocation() != null && !originalText.contains(job.getLocation().toLowerCase())) {
            job.setLocation("Validation Failed");
            job.setSpamScore(job.getSpamScore() + 25.0);
            valid = false;
        }

        return valid;
    }

    public JobPosting parseRawText(String rawText) {
        JobPosting job = new JobPosting();
        job.setRawText(rawText);

        String cleanText = rawText.replace("\"", "'").replace("\n", " ").replace("\r", " ");
        String prompt = "Extract the job title, company name, and location from the following text. "
                      + "Format the output EXACTLY as: Title: [title] | Company: [company] | Location: [location]. "
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
                

                //System.out.println("RAW JSON RESPONSE: " + response.body()); // debugging line

                String aiResponse = extractResponseText(response.body());
                job.setNormalizedText(aiResponse);
                
                // Clear any bad data from previous failed attempts
                job.setJobTitle(null);
                job.setCompanyName(null);
                job.setLocation(null);
                
                mapAiResponseToEntity(aiResponse, job);

                // Check if the current attempt is clean
                if (verifyNoHallucinations(job)) {
                    job.setStatus("Parsed");
                    return job; // Success! Break out and return the valid job.
                } else {
                    System.out.println("Hallucination detected on attempt " + attempt + ". Retrying...");
                }

            } catch (Exception e) {
                System.out.println("Error communicating with Ollama: " + e.getMessage());
                break; // Break the loop early if there's a hard network/Ollama error
            }
        }

        // If the loop finishes all 3 attempts and still hallucinates, fallback to human review
        System.out.println("Max retries reached. Flagging job for manual review.");
        job.setStatus("Needs Review");
        return job;
    }

    // Validate that the AI did not hallucinate resume fields
    private boolean verifyResumeNoHallucinations(Map<String, String> resumeData, String rawText) {
        String originalText = rawText.toLowerCase();
        boolean valid = true;
        
        // Check Email
        String email = resumeData.get("Email");
        if (email != null && !email.isBlank() && !originalText.contains(email.toLowerCase())) {
            resumeData.put("Email", "Validation Failed");
            valid = false;
        }
        
        // Check Name (Ensures at least the first or last name exists in the text)
        String name = resumeData.get("Full Name");
        if (name != null && !name.isBlank()) {
            String[] nameParts = name.toLowerCase().split(" ");
            boolean nameFound = false;
            for (String part : nameParts) {
                if (originalText.contains(part)) {
                    nameFound = true;
                    break;
                }
            }
            if (!nameFound) {
                resumeData.put("Full Name", "Validation Failed");
                valid = false;
            }
        }

        return valid;
    }


    // Parse Resume
    public Map<String, String> parseResume(String rawText) {
        Map<String, String> resumeData = new LinkedHashMap<>();
        resumeData.put("Full Name", "");
        resumeData.put("Email", "");
        resumeData.put("Skills", "");

        String cleanText = rawText.replace("\"", "'").replace("\n", " ").replace("\r", " ");
        String prompt = "Extract the candidate's full name, email address, and a comma-separated list of top 3 skills from the following resume text. "
                      + "Format the output EXACTLY as: Name: [name] | Email: [email] | Skills: [skills]. "
                      + "Do not add any other conversational text. Resume: " + cleanText;

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
                
                for (String line : lines) {
                    if (line.toLowerCase().contains("name:")) {
                        resumeData.put("Full Name", line.substring(line.toLowerCase().indexOf("name:") + 5).trim());
                    } else if (line.toLowerCase().contains("email:")) {
                        resumeData.put("Email", line.substring(line.toLowerCase().indexOf("email:") + 6).trim());
                    } else if (line.toLowerCase().contains("skills:")) {
                        resumeData.put("Skills", line.substring(line.toLowerCase().indexOf("skills:") + 7).trim());
                    }
                }

                if (verifyResumeNoHallucinations(resumeData, rawText)) {
                    return resumeData; // Success
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