package com.lit.fire.flame;

import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

public class YouTubeMain {

    private static final String PROPERTIES_FILE = "secrets.properties";

    public static void main(String[] args) {

        String apiKey = loadApiKey();
        if (apiKey == null) {
            System.err.println("Failed to load API key. Please ensure " + PROPERTIES_FILE + " exists and contains YOUTUBE_API_KEY.");
            return;
        }

        try {
            // 1. Initialize the service.
            YouTubeService service = new YouTubeService("MyCoolieSearchApp", apiKey);

            // 2. Define the search parameters.
            String query = "Coolie Review";
            long maxVideos = 10;

            // 3. Execute the search.
            List<SearchResult> searchResults = service.searchVideos(query, maxVideos);

            // 4. Process and display the results.
            if (searchResults.isEmpty()) {
                System.out.println("No videos found for the query: '" + query + "'");
            } else {
                System.out.println("\n=============================================================");
                System.out.println(" Top " + searchResults.size() + " results for '" + query + "'");
                System.out.println("=============================================================\n");

                for (SearchResult result : searchResults) {
                    System.out.println("  - Title: " + result.getSnippet().getTitle());
                    System.out.println("    Video ID: " + result.getId().getVideoId());
                    System.out.println("    Thumbnail: " + result.getSnippet().getThumbnails().getDefault().getUrl());
                    System.out.println("\n-------------------------------------------------------------\n");
                }
            }

        } catch (Exception e) {
            System.err.println("An error occurred during service initialization or execution.");
            e.printStackTrace();
        }
    }

    /**
     * Loads the YouTube API key from a properties file in the resources folder.
     * @return The API key as a String, or null if it cannot be loaded.
     */
    private static String loadApiKey() {
        Properties prop = new Properties();
        try (InputStream input = YouTubeMain.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTIES_FILE);
                return null;
            }
            prop.load(input);
            return prop.getProperty("youtube.api_key");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}