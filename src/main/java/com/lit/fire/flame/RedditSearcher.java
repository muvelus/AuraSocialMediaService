package com.lit.fire.flame;

import net.dean722.jr0w.RedditClient;
import net.dean722.jr0w.http.NetworkAdapter;
import net.dean722.jr0w.http.OkHttpNetworkAdapter;
import net.dean722.jr0w.http.UserAgent;
import net.dean722.jr0w.models.Listing;
import net.dean722.jr0w.models.SearchSort;
import net.dean722.jr0w.models.Submission;
import net.dean722.jr0w.models.TimePeriod;
import net.dean722.jr0w.oauth.Credentials;
import net.dean722.jr0w.oauth.OAuthHelper;
import net.dean722.jr0w.pagination.SearchPaginator;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class RedditSearcher {

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static String USERNAME;
    private static String PASSWORD;

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
        try (InputStream input = RedditSearcher.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            if (input == null) {
                System.err.println("Error: Unable to find secrets.properties. Please ensure the file exists and contains the required API credentials.");
                System.exit(1);
            }
            properties.load(input);
        }

        CLIENT_ID = properties.getProperty("reddit.client_id");
        CLIENT_SECRET = properties.getProperty("reddit.client_secret");
        USERNAME = properties.getProperty("reddit.username");
        PASSWORD = properties.getProperty("reddit.password");

        if (CLIENT_ID == null || CLIENT_SECRET == null || USERNAME == null || PASSWORD == null) {
            System.err.println("Error: Please configure your Reddit API credentials in the secrets.properties file.");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        UserAgent userAgent = new UserAgent("bot", "com.example.aura", "v1.0", USERNAME);
        Credentials credentials = Credentials.script(CLIENT_ID, CLIENT_SECRET, USERNAME, PASSWORD);

        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient redditClient = OAuthHelper.automatic(adapter, credentials);

        String searchString = "Aura AI";

        SearchPaginator search = redditClient.search()
                .query(searchString)
                .sorting(SearchSort.TOP)
                .timePeriod(TimePeriod.ALL)
                .limit(25)
                .build();

        Listing<Submission> results = search.next();
        System.out.println("Top 25 results for: " + searchString);

        for (int i = 0; i < results.size(); i++) {
            Submission post = results.get(i);
            System.out.printf("%d. [%d Upvotes] %s%n", (i + 1), post.getScore(), post.getTitle());
            System.out.println("   Link: https://reddit.com" + post.getPermalink());
        }
    }
}