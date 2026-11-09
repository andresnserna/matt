package edu.stedwards.matt.services;

import javafx.collections.ObservableList;
import edu.stedwards.matt.models.entity.JobPosting;

/**
 * Architecture layer: service
 * Business logic for ingesting job postings from file, URL, or pasted text.
 *
 * This is the single receiving service for the application upload flow. The UI hands it
 * a file path, URL, or raw description text; it routes each through extraction/scraping,
 * sends the resulting text to the Ollama-backed ParserService, and returns the parsed
 * JobPosting WITHOUT saving it to the database. Persistence happens later, after the user
 * confirms/edits the parsed fields on the Confirm Fields screen.
 *
 * All methods are blocking (Ollama HTTP + JDBC) and must be called off the JavaFX
 * application thread (e.g. inside a javafx.concurrent.Task).
 */
public interface JobIngestService {
    /** Parse a raw job-description text and return the parsed (unsaved) JobPosting. */
    JobPosting parseJobFromText(int userId, String rawText);

    /** Scrape a URL, parse the extracted text, and return the parsed (unsaved) JobPosting. */
    JobPosting parseJobFromUrl(int userId, String url);

    /** Extract text from a file, parse it, and return the parsed (unsaved) JobPosting. */
    JobPosting parseJobFromFile(int userId, String filePath);

    /** Persist a new job-posting record to the database (Confirm Fields -> save). */
    boolean saveJobPosting(JobPosting jobPosting);

    /** Persist edits to an existing job-posting record. */
    boolean updateJobPosting(JobPosting jobPosting);

    /** Load recently saved jobs for a user as an ObservableList (Application Home). */
    ObservableList<JobPosting> loadJobsForUser(int userId);
}