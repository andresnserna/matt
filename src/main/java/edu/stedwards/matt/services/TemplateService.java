package edu.stedwards.matt.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class TemplateService {
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "llama3.2:3b"; 

    
    public String generateDocument(String templateType, Map<String, String> jobFields, String userProfileContext) {
        String jobText = jobFields.getOrDefault("Raw Text", "No job description provided.");
        
        String prompt = "You are an expert career coach. Write a professional " + templateType + ". "
                      + "Do not include any conversational filler like 'Here is your letter'. "
                      + "Base it strictly on this job description: " + jobText + " "
                      + "And the applicant's background: " + userProfileContext;

        String cleanPrompt = prompt.replace("\"", "'").replace("\n", " ");
        
        String jsonPayload = "{"
                + "\"model\": \"" + MODEL_NAME + "\","
                + "\"prompt\": \"" + cleanPrompt + "\","
                + "\"stream\": false"
                + "}";

        return sendToOllama(jsonPayload);
    }

    // ---Fills in the [brackets] of an HTML template ---
    public String fillTemplateWithAI(String htmlTemplate, Map<String, String> jobFields, String userContext) {
        String jobText = jobFields.getOrDefault("Raw Text", "No job description provided.");
        
        String prompt = "You are a document generation AI. I will provide an HTML document containing bracketed placeholders like [Company Name]. "
                      + "Your job is to replace the text inside the brackets using the applicant's background and the job description provided. "
                      + "RULES:\n"
                      + "1. DO NOT change, remove, or break any HTML tags.\n"
                      + "2. DO NOT add any markdown formatting like ```html.\n"
                      + "3. Return ONLY the final raw HTML code.\n\n"
                      + "Applicant Background: " + userContext + "\n"
                      + "Job Description: " + jobText + "\n\n"
                      + "HTML Template to Fill:\n" + htmlTemplate;

        String cleanPrompt = prompt.replace("\"", "'").replace("\n", " ");
        
        String jsonPayload = "{"
                + "\"model\": \"" + MODEL_NAME + "\","
                + "\"prompt\": \"" + cleanPrompt + "\","
                + "\"stream\": false"
                + "}";

        return sendToOllama(jsonPayload);
    }

    // Sends the request and parses the JSON response 
    private String sendToOllama(String jsonPayload) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OLLAMA_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return extractResponseText(response.body());
        } catch (Exception e) {
            System.err.println("API Request Failed: " + e.getMessage());
            return "Error communicating with AI.";
        }
    }

    private String extractResponseText(String jsonResponse) {
        try {
            String target = "\"response\":\"";
            int startIndex = jsonResponse.indexOf(target) + target.length();
            int endIndex = jsonResponse.indexOf("\",\"done\"", startIndex);
            
            if (startIndex > target.length() - 1 && endIndex > startIndex) {
                return jsonResponse.substring(startIndex, endIndex)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\t", "\t");
            }
        } catch (Exception e) {
            System.err.println("Failed to parse response text.");
        }
        return jsonResponse;
    }
}
