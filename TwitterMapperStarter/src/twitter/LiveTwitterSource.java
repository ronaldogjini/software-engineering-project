package twitter;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import util.Logger;

/**
 * Encapsulates the connection to Twitter
 *
 * Terms to include in the returned tweets can be set with setFilterTerms
 *
 * Implements Observable - each received tweet is signalled to all observers
 */
public class LiveTwitterSource extends TwitterSource {
    private TwitterStream twitterStream;
    private StatusListener listener;

    public LiveTwitterSource() {
        initializeTwitterStream();
    }

    protected void sync() {
        FilterQuery filter = new FilterQuery();
        // https://stackoverflow.com/questions/21383345/using-multiple-threads-to-get-data-from-twitter-using-twitter4j
        String[] queriesArray = terms.toArray(new String[0]);
        filter.track(queriesArray);

        Logger.printLiveSyncing(terms);

        twitterStream.filter(filter);
    }

    private void initializeListener() {
        listener = new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                // This method is called each time a tweet is delivered by the twitter API
                if (status.getPlace() != null) {
                    handleTweet(status);
                }
           }
        };
    }

    // Create ConfigurationBuilder and pass in necessary credentials to authorize properly, then create TwitterStream.
    private void initializeTwitterStream() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setOAuthConsumerKey("AJZOnrnb269MvsaiTHHnQHqsB")
                .setOAuthConsumerSecret("tY2yepSydhQvoOKhJdEbfEMTryeVOHfSBNCptPAT6HDVnaho25")
                .setOAuthAccessToken("1293893809-ywazlRsTSOBMF7xVTMQFOGvIzgQIncdciUpNJT3")
                .setOAuthAccessTokenSecret("DmRXmWuPKK40VyAjZrSFLWtyAIKm5hTkwsRPIYADAkW76");

        // Pass the ConfigurationBuilder in when constructing TwitterStreamFactory.
        twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        initializeListener();
        twitterStream.addListener(listener);
    }
}
