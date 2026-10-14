package edu.stedwards.matt.controllers;

import java.io.IOException;

import edu.stedwards.matt.dao.JobDAO;
import edu.stedwards.matt.models.entity.JobPosting;
import edu.stedwards.matt.services.DocumentExtractionService;
import edu.stedwards.matt.services.ParserService;   //src\main\java\edu\stedwards\matt\services\ParserService.Java
import edu.stedwards.matt.services.ScraperService;

public class JobUploadController {

    private ParserService parserService;
    private JobDAO jobDAO;

    public JobUploadController() {
        this.parserService = new ParserService();
        this.jobDAO = new JobDAO();
    }

    public JobPosting uploadJobFromUrl(int userId, String url) {
        ScraperService scraper = new ScraperService();
        String scrapedText = scraper.scrapeJobPosting(url);
        
        if (scrapedText == null || scrapedText.trim().isEmpty()) {
            System.out.println("Controller Error: Failed to retrieve text from URL.");
            return null;
        }
        
        // Feed the scraped text directly into your existing ingestion pipeline
        return uploadJobFromText(userId, scrapedText);
    }

    public JobPosting uploadJobFromFile(int userId, String filePath) {
        DocumentExtractionService docService = new DocumentExtractionService();
        try {
            String rawText = docService.extractText(filePath);
            return uploadJobFromText(userId, rawText); // Passes the text to your existing text upload method
        } catch (IOException e) {
            System.err.println("Upload failed: " + e.getMessage());
            return null;
        }
    }

    // simulates the API endpoint
    public JobPosting uploadJobFromText(int userId, String rawJobText) {
        
        if (rawJobText == null || rawJobText.trim().isEmpty()) {
            System.out.println("Upload rejected: Input text is empty.");
            return null; // Rejects bad input cleanly
        }

        // Send the raw text to the AI for parsing and normalization
        JobPosting job = parserService.parseRawText(rawJobText);
        
        // Set the remaining required fields for the DB
        job.setUserID(userId);
        job.setCompanyID(1); // Placeholder until company lookup logic is built
        
        // Save the job to the database
        boolean isSaved = jobDAO.insertJob(job);
        
        if (isSaved) {
            System.out.println("Job successfully saved and ready for display!");
            return job; // Return the exact record the UI needs to render
        } else {
            System.out.println("Error: Could not save job to database.");
            return null;
        }
    }
}