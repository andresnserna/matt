package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
            String[] parts = aiText.split("\\|");
            for (String part : parts) {
                if (part.contains("Title:")) job.setJobTitle(part.replace("Title:", "").trim());
                if (part.contains("Company:")) job.setCompanyName(part.replace("Company:", "").trim());
                if (part.contains("Location:")) job.setLocation(part.replace("Location:", "").trim());
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

        String cleanText = rawText.replace("\"", "'").replace("\n", " ");
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

}