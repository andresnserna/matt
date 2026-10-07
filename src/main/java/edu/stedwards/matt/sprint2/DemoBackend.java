package edu.stedwards.matt.sprint2;

import java.util.LinkedHashMap;
import java.util.Map;

final class DemoBackend {

    boolean logIn(String username) {
        // TODO: Authenticate through the account service.
        return true;
    }

    Map<String, String> parseResume(String resumeData) {
        // TODO: Send resume data to the resume parser and map its response.
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Full name", "Jamie Example");
        fields.put("Email", "jamie@example.com");
        fields.put("Phone", "(555) 010-1234");
        fields.put("Address line 1", "");
        return fields;
    }

    Map<String, String> parseJobPosting(String source, String postingData) {
        // TODO: Load the posting from its source and map the parser response.
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("Job title", "Software Engineer");
        fields.put("Location", "Austin, TX");
        fields.put("Company name", "");
        return fields;
    }

    boolean saveProfile(Map<String, String> fields) {
        // TODO: Persist the profile through the profile service.
        return true;
    }

    boolean saveApplication(Map<String, String> fields) {
        // TODO: Persist the application through the application service.
        return true;
    }

    void generateMaterials(String templateName, Map<String, String> fields) {
        // TODO: Call the material-generation service.
    }
}
