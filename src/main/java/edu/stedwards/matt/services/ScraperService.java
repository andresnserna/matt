package edu.stedwards.matt.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ScraperService {
    
    public String scrapeJobPosting(String url) {
        try {
            // Connects to the URL and extracts all visible HTML text
            Document doc = Jsoup.connect(url).get();
            return doc.text();
        } catch (Exception e) {
            System.out.println("ScraperService Error: Could not scrape URL. " + e.getMessage());
            return null;
        }
    }
}