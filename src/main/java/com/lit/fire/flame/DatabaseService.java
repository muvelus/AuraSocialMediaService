package com.lit.fire.flame;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class DatabaseService {

    private static Properties loadDbProperties() throws Exception {
        Properties properties = new Properties();
        try (InputStream input = DatabaseService.class.getClassLoader().getResourceAsStream("secrets.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find secrets.properties");
                return null;
            }
            properties.load(input);
        }
        return properties;
    }

    public static void saveInstagramPosts(JsonArray posts, String keyword) throws Exception {
        Properties dbProperties = loadDbProperties();
        if (dbProperties == null) {
            return;
        }

        String dbUrl = dbProperties.getProperty("db.url", "jdbc:postgresql://localhost:5432/aura");
        String dbUser = dbProperties.getProperty("db.user", "postgres");
        String dbPassword = dbProperties.getProperty("db.password", "postgres");

        String sql = "INSERT INTO instagram_posts (id, caption, media_type, media_url, permalink, timestamp, keyword) VALUES (?, ?, ?, ?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

            for (JsonElement postElement : posts) {
                JsonObject post = postElement.getAsJsonObject();

                pstmt.setString(1, post.get("id").getAsString());
                pstmt.setString(2, post.has("caption") ? post.get("caption").getAsString() : null);
                pstmt.setString(3, post.get("media_type").getAsString());
                pstmt.setString(4, post.has("media_url") ? post.get("media_url").getAsString() : null);
                pstmt.setString(5, post.get("permalink").getAsString());

                String timestampString = post.get("timestamp").getAsString();
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampString, formatter);
                pstmt.setTimestamp(6, Timestamp.from(zonedDateTime.toInstant()));
                pstmt.setString(7, keyword);

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            System.out.println("Successfully saved " + posts.size() + " posts to the database.");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveXPosts(JsonArray posts, String keyword) throws Exception {
        Properties dbProperties = loadDbProperties();
        if (dbProperties == null) {
            return;
        }

        String dbUrl = dbProperties.getProperty("db.url", "jdbc:postgresql://localhost:5432/aura");
        String dbUser = dbProperties.getProperty("db.user", "postgres");
        String dbPassword = dbProperties.getProperty("db.password", "postgres");

        String sql = "INSERT INTO x_posts (id, text, created_at, keyword) VALUES (?, ?, ?, ?) ON CONFLICT (id) DO NOTHING";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            for (JsonElement postElement : posts) {
                JsonObject post = postElement.getAsJsonObject();

                pstmt.setString(1, post.get("id").getAsString());
                pstmt.setString(2, post.has("text") ? post.get("text").getAsString() : null);
                
                String timestampString = post.get("created_at").getAsString();
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestampString, formatter);
                pstmt.setTimestamp(3, Timestamp.from(zonedDateTime.toInstant()));
                pstmt.setString(4, keyword);

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            System.out.println("Successfully saved " + posts.size() + " posts to the database.");

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}