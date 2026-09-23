package edu.stedwards.matt.services;

/**
 * Contains settings-related business logic for the app.
 * This class is called by UI or controller code when settings need to be read, changed,
 * or validated before they are saved through the DAO layer.
 * It is not responsible for direct database SQL or creating JavaFX layouts.
 */
import edu.stedwards.matt.models.entity.JobPosting;

public interface JobIngestService {
    JobPosting ingestJobFromUrl(String url);
    JobPosting ingestJobFromText(String rawText);
    boolean saveJobPosting(JobPosting jobPosting);
}