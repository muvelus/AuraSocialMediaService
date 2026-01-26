package com.lit.fire.flame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * A client for searching X/Twitter posts.
 */
public class XService {

    private static String ACCESS_TOKEN;
    private static final String API_URL = "https://api.twitter.com/2";
    private static int numberOfPosts;

    public static void main(String[] args) {
        try {
            loadConfig();
            System.out.println("Initializing X Search...");
            List<String> keywords = loadKeywords();

            for (String keyword : keywords) {
                System.out.println("\nProcessing keyword: " + keyword);
                search(keyword);
            }

        } catch (Exception e) {
            System.err.println("An unrecoverable error occurred during the process.");
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = XService.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            if (input == null) {
                System.err.println("Error: Unable to find secrets.properties. Please ensure the file exists and contains the required API credentials.");
                System.exit(1);
            }
            properties.load(input);
        }

        ACCESS_TOKEN = properties.getProperty("x.access_token");
        numberOfPosts = AppProperties.getIntProperty("number.of.posts", 10);

        if (ACCESS_TOKEN == null || ACCESS_TOKEN.equals("YOUR_ACCESS_TOKEN")) {
            System.err.println("Error: Please configure your X API credentials in the secrets.properties file.");
            System.exit(1);
        }
    }

    private static List<String> loadKeywords() throws Exception {
        List<String> keywords = new ArrayList<>();
        try (InputStream input = XService.class.getClassLoader().getResourceAsStream("search_queries.txt")) {
            if (input == null) {
                System.out.println("Sorry, unable to find search_queries.txt");
                return keywords;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                keywords = reader.lines()
                        .map(line -> line.trim().replaceAll("\\s+", "").toLowerCase())
                        .collect(Collectors.toList());
            }
        }
        return keywords;
    }

    public static void search(String query) throws Exception {
        System.out.println("\nRetrieving latest " + numberOfPosts + " posts for '" + query + "'...");

        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String fields = "id,text,created_at";
        String searchUrl = String.format("%s/tweets/search/recent?query=%s&tweet.fields=%s&max_results=%d",
                API_URL, encodedQuery, fields, numberOfPosts);

        JsonObject response = sendRequest(searchUrl);

        System.out.println("Search successful. Found posts:");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(response));

        if (response != null && response.has("data")) {
            DatabaseService.saveXPosts(response.getAsJsonArray("data"), query);
        }
    }

    private static JsonObject sendRequest(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return JsonParser.parseString(response.body()).getAsJsonObject();
        }
        
        throw new RuntimeException("API Request failed. Status: " + response.statusCode() + ", Body: " + response.body());
    }
}