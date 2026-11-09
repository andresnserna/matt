package edu.stedwards.matt.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import edu.stedwards.matt.dao.JobDAO;
import edu.stedwards.matt.models.entity.JobPosting;

/**
 * Architecture layer: service
 * Concrete implementation of JobIngestService.
 *
 * Single receiving service for the application upload flow. Accepts a file path, URL,
 * or pasted job-description text, routes each to the Ollama-backed ParserService, and
 * returns the parsed JobPosting WITHOUT saving it to the database. Persistence is
 * deferred until the user confirms/edits the fields on the Confirm Fields screen.
 *
 * All methods perform blocking I/O (Ollama HTTP + JDBC) and must be invoked off the
 * JavaFX application thread — e.g. wrapped in a javafx.concurrent.Task by the UI layer.
 */
public class JobIngestServiceImpl implements JobIngestService {

    private final JobDAO jobDAO = new JobDAO();
    private final ParserService parserService = new ParserService();
    private final ScraperService scraperService = new ScraperService();
    private final DocumentExtractionService docService = new DocumentExtractionService();

    @Override
    public JobPosting parseJobFromText(int userId, String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            System.out.println("Parse rejected: input text is empty.");
            return null;
        }

        JobPosting job = parserService.parseJobDescription(rawText);
        if (job == null) {
            System.out.println("Parse failed: parser returned no result.");
            return null;
        }

        job.setUserId(userId);
        job.setCompanyId(1); // Placeholder until company lookup logic is built
        return job;
    }

    @Override
    public JobPosting parseJobFromUrl(int userId, String url) {
        if (url == null || url.trim().isEmpty()) {
            System.out.println("Parse rejected: URL is empty.");
            return null;
        }

        String scrapedText;
        try {
            scrapedText = scraperService.scrapeJobPosting(url);
        } catch (Exception e) {
            System.err.println("Parse failed while scraping URL: " + e.getMessage());
            return null;
        }

        if (scrapedText == null || scrapedText.trim().isEmpty()) {
            System.out.println("Parse failed: no text scraped from URL.");
            return null;
        }

        return parseJobFromText(userId, scrapedText);
    }

    @Override
    public JobPosting parseJobFromFile(int userId, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("Parse rejected: file path is empty.");
            return null;
        }

        String rawText;
        try {
            rawText = docService.extractText(filePath);
        } catch (Exception e) {
            System.err.println("Parse failed while extracting file: " + e.getMessage());
            return null;
        }

        if (rawText == null || rawText.trim().isEmpty()) {
            System.out.println("Parse failed: no text extracted from file.");
            return null;
        }

        return parseJobFromText(userId, rawText);
    }

    @Override
    public boolean saveJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            System.err.println("Save rejected: job posting is null.");
            return false;
        }
        return jobDAO.insertJob(jobPosting);
    }

    @Override
    public boolean updateJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            System.err.println("Update rejected: job posting is null.");
            return false;
        }
        return jobDAO.updateJob(jobPosting);
    }

    @Override
    public ObservableList<JobPosting> loadJobsForUser(int userId) {
        return FXCollections.observableArrayList(jobDAO.getRecentJobsForUser(userId, 50));
    }
}
