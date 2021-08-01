package twitter;

import twitter4j.Status;
import util.ImageCache;

import java.util.*;


public abstract class TwitterSource extends Observable {
    protected boolean doLogging = true;
    // The set of terms to look for in the stream of tweets
    protected Set<String> terms = new HashSet<>();

    // Called each time a new set of filter terms has been established
    abstract protected void sync();

    public void setFilterTerms(Collection<String> newTerms) {
        terms.clear();
        terms.addAll(newTerms);
        sync();
    }

    public List<String> getFilterTerms() {
        return new ArrayList<>(terms);
    }

    // This method is called each time a tweet is delivered to the application.
    protected void handleTweet(Status s) {
        setChanged();
        notifyObservers(s);
    }
}
