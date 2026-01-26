package com.lit.fire.flame;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Properties;

/**
 * A client for authenticating with the Reddit API using the OAuth 2.0
 * Client Credentials Grant Flow.
 */
public class RedditAuthClient {

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String REDDIT_USERNAME;
    private static String USER_AGENT;

    private static final String TOKEN_ENDPOINT = "https://www.reddit.com/api/v1/access_token";

    static {
        try {
            loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void loadConfig() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = RedditAuthClient.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            if (input == null) {
                System.err.println("Error: Unable to find secrets.properties. Please ensure the file exists and contains the required API credentials.");
                System.exit(1);
            }
            properties.load(input);
        }

        CLIENT_ID = properties.getProperty("reddit.client_id");
        CLIENT_SECRET = properties.getProperty("reddit.client_secret");
        REDDIT_USERNAME = properties.getProperty("reddit.username");

        if (CLIENT_ID == null || CLIENT_SECRET == null || REDDIT_USERNAME == null) {
            System.err.println("Error: Please configure your Reddit API credentials in the secrets.properties file.");
            System.exit(1);
        }
        USER_AGENT = String.format("java:com.example.redditauth:v1.0 (by /u/%s)", REDDIT_USERNAME);
    }

    /**
     * Main method to demonstrate the authentication process.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            String accessToken = getAccessToken();
            System.out.println("Successfully retrieved Reddit API Access Token.");
            // The token would now be used in subsequent API calls.
            // For example: "Authorization: bearer " + accessToken
        } catch (Exception e) {
            System.err.println("Failed to obtain access token.");
            e.printStackTrace();
        }
    }

    /**
     * Authenticates with the Reddit API and retrieves an access token.
     * @return The access token string.
     * @throws Exception if the request fails or returns an error.
     */
    public static String getAccessToken() throws Exception {
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authHeaderValue = "Basic " + encodedCredentials;

        HttpClient client = HttpClient.newHttpClient();

        String requestBody = "grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_ENDPOINT))
                .header("Authorization", authHeaderValue)
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        System.out.println("Sending token request to Reddit...");

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode()!= 200) {
            throw new RuntimeException("Request failed. Status Code: " + response.statusCode() + ", Body: " + response.body());
        }

        System.out.println("Response received successfully. Parsing token...");

        Gson gson = new Gson();
        String responseBody = response.body();
        RedditToken token = gson.fromJson(responseBody, RedditToken.class);

        if (token == null || token.getAccessToken() == null) {
            throw new RuntimeException("Failed to parse access token from response body: " + responseBody);
        }

        System.out.println("Token expires in: " + token.getExpiresIn() + " seconds.");
        return token.getAccessToken();
    }

    /**
     * A Plain Old Java Object (POJO) to represent the JSON response from Reddit's token endpoint.
     * GSON uses this class to deserialize the JSON string into a Java object.
     */
    public static class RedditToken {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("expires_in")
        private int expiresIn;

        @SerializedName("scope")
        private String scope;

        public String getAccessToken() {
            return accessToken;
        }

        public int getExpiresIn() {
            return expiresIn;
        }
    }
}