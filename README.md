    private static final String CLIENT_ID = "XOegTr5X4D9uamO3fpDPfQ";
    private static final String CLIENT_SECRET = "Yxr5IFbfJ3cLLjwhO53AwoTLPDi8SA";
    private static final String REDDIT_USERNAME = "True-Television-4314";

    // The User-Agent header is mandatory and must be unique and descriptive.
    // Format: <platform>:<appId>:<version> (by /u/<username>)
    private static final String USER_AGENT = String.format("java:com.example.redditauth:v1.0 (by /u/%s)", REDDIT_USERNAME);

    private static final String TOKEN_ENDPOINT = "https://www.reddit.com/api/v1/access_token";

The Client ID generated during app registration(https://www.reddit.com/prefs/apps?solution=d83bdbdd07b77eefd83bdbdd07b77eef&js_challenge=1&token=54dba411ecc9fd270bca6277dc2a436114acbdb6757f94424e0e44ea0877cd9f)
For Reddit the Client ID is sent via email and is called 'APP ID' in the email