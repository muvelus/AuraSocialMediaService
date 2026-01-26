package com.lit.fire.flame;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * A reusable service class for interacting with the YouTube Data API v3.
 */
public class YouTubeService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final YouTube youtubeService;
    private final String apiKey;

    /**
     * Constructor to initialize the YouTube service.
     *
     * @param applicationName The name of the application to be sent in the User-Agent header.
     * @param apiKey Your project's API key from the Google Cloud Console.
     * @throws GeneralSecurityException If the transport cannot be initialized.
     * @throws IOException If the transport cannot be initialized.
     */
    public YouTubeService(String applicationName, String apiKey) throws GeneralSecurityException, IOException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be null or empty.");
        }
        this.apiKey = apiKey;

        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.youtubeService = new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(applicationName)
                .build();
    }

    /**
     * Searches for YouTube videos matching a given query term.
     *
     * @param queryTerm The term to search for.
     * @param maxResults The maximum number of results to return (1-50).
     * @return A list of SearchResult objects, or an empty list if no results are found or an error occurs.
     */
    public List<SearchResult> searchVideos(String queryTerm, long maxResults) {
        try {
            System.out.println("Executing search for query: " + queryTerm);

            // Create and configure the search request.
            YouTube.Search.List searchRequest = youtubeService.search()
                    .list("snippet"); // The 'part' parameter is mandatory.

            // Set the parameters for the search request.
            searchRequest.setKey(this.apiKey);
            searchRequest.setQ(queryTerm);
            searchRequest.setType("video"); // Restrict results to videos only.
            searchRequest.setMaxResults(maxResults);

            // Set the 'fields' parameter to optimize the response by requesting only the needed data.
            // This reduces payload size and processing time.
            searchRequest.setFields("items(id/videoId,snippet/title,snippet/thumbnails/default/url)");

            // Execute the request and get the response.
            SearchListResponse searchResponse = searchRequest.execute();

            // Return the list of items from the response.
            List<SearchResult> items = searchResponse.getItems();
            if (items!= null) {
                return items;
            }

        } catch (IOException e) {
            System.err.println("An IO error occurred: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during the search: " + e.getMessage());
            e.printStackTrace();
        }

        // Return an empty list in case of errors or no results.
        return Collections.emptyList();
    }
}