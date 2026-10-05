package edu.stedwards.matt.models.AI;

import edu.stedwards.matt.controllers.JobUploadController;
import edu.stedwards.matt.models.entity.JobPosting;

public class JobPipelineTest {
    public static void main(String[] args) {
        JobUploadController controller = new JobUploadController();
        
        // A messy, raw text paste typical of a job board
        String rawPaste = "Hiring immediately! We need a Backend Java Developer at Globex Corporation. "
                        + "This role is based in Austin, TX but allows hybrid work. "
                        + "Must have experience with Spring Boot, SQLite, and LLM integrations. "
                        + "Competitive salary and great benefits.";
        
        System.out.println("Starting job ingestion pipeline...");
        
        // Pass a dummy user ID (e.g., 1) and the raw text
        JobPosting result = controller.uploadJobFromText(1, rawPaste);
        
        if (result != null) {
            System.out.println("\n--- Pipeline Success! ---");
            System.out.println("Title:    " + result.getJobTitle());
            System.out.println("Company:  " + result.getCompanyName());
            System.out.println("Location: " + result.getLocation());
            System.out.println("Status:   " + result.getStatus());
            System.out.println("Spam Score: " + result.getSpamScore());
            System.out.println("-------------------------");
            System.out.println("This record has been saved to SQLite and is ready for the UI!");
        } else {
            System.out.println("\nPipeline failed or input was cleanly rejected.");
        }
    }
}