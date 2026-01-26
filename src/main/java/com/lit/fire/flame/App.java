package com.lit.fire.flame;

import org.json.simple.parser.ParseException;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.Paging;
import twitter4j.v1.TimelinesResources;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.RestClient;

import java.io.IOException;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App
{
    public static void main(String[] args) throws TwitterException, IOException, ParseException {
//        twitterTest();
        redditTest();
    }

    private static void twitterTest() {
        try {
            TimelinesResources twitter = Twitter.getInstance().v1().timelines();
            // requesting page 2, number of elements per page is 40
            twitter.getMentionsTimeline(Paging.ofPage(2).count(40))
                    .forEach(tweet ->
                            System.out.printf("%s:%s%n", tweet.getUser().getScreenName(), tweet.getText()));

            // requesting page 3, since_id is (long)1000
            twitter.getMentionsTimeline(Paging.ofPage(3).sinceId(1000L))
                    .forEach(tweet ->
                            System.out.printf("%s:%s%n", tweet.getUser().getScreenName(), tweet.getText()));
        } catch (TwitterException e) {
            throw new RuntimeException(e);
        }
    }

    private static User redditTest() throws IOException, ParseException {
        String username = "True-Television-4314";
        String password = "Biryani@890";
        String clientId = "After-Ad-8656";
        String clientSecret = "D_krr3uLHf9pvVJj7o95ElOBcRSfHQ";
        String userAgent = "qwerty:com.example.ormapp:v1.0 (by /u/True-Television-4314)";

        RestClient restClient = new HttpRestClient();
        restClient.setUserAgent(userAgent);

        User user = new User(restClient, username, password);
        user.connect();

        System.out.println("Successfully connected as: " + user.getUsername());

        return user;
    }
}
