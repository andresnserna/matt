package edu.stedwards.matt.sprint2;

import java.util.LinkedHashMap;
import java.util.Map;

final class DemoBackend {

    enum ParseJob {
        RESUME,
        JOB_POSTING
    }

    boolean logIn(String username) {
        // TODO: Authenticate through the account service.
        return true;
    }

    Map<String, String> parse(ParseJob parseJob, String source, String data) {
        // TODO: Send the input to the appropriate parser and map its response.
        Map<String, String> fields = new LinkedHashMap<>();
        switch (parseJob) {
            case RESUME -> {
                fields.put("Full name", "Jamie Example");
                fields.put("Email", "jamie@example.com");
                fields.put("Phone", "(555) 010-1234");
                fields.put("Address line 1", "");
            }
            case JOB_POSTING -> {
                fields.put("Job title", "Software Engineer");
                fields.put("Location", "Austin, TX");
                fields.put("Company name", "");
            }
        }
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
