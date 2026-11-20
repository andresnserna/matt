package edu.stedwards.matt.services;

import edu.stedwards.matt.models.entity.JobPosting;

/** Reads saved application scores and provides the future calculation entry points. */
public class ApplicationScoreService {

    /**
     * Returns the stored 0.0–1.0 spam confidence when available; otherwise invokes the
     * classifier calculation entry point. Called by ApplicationGenerateMaterials.
     */
    public Double getSpamScore(JobPosting job) {
        if (job == null) return null;
        Double savedScore = job.getSpamScore();
        if (isNormalizedScore(savedScore)) return savedScore;
        return calculateSpamScore(job);
    }

    /**
     * Classifier entry point for spam confidence, where 0.0 means least likely spam
     * and 1.0 means most likely spam. Called by getSpamScore when no saved score exists.
     */
    private Double calculateSpamScore(JobPosting job) {
        // TODO: javi implement
        return null;
    }

    /**
     * Returns the stored 0.0–1.0 job match score when available; otherwise invokes the
     * custom match calculation entry point. Called by ApplicationGenerateMaterials.
     */
    public Double getMatchScore(JobPosting job) {
        if (job == null) return null;
        Double savedScore = job.getMatchScore();
        if (isNormalizedScore(savedScore)) return savedScore;
        return calculateMatchScore(job);
    }

    /**
     * Custom profile-to-job evaluation entry point. Called by getMatchScore when no
     * saved score exists; future implementation should use the signed-in user's profile.
     */
    private Double calculateMatchScore(JobPosting job) {
        // TODO: javi implement
        return null;
    }

    private boolean isNormalizedScore(Double score) {
        return score != null && Double.isFinite(score) && score >= 0.0 && score <= 1.0;
    }
}
