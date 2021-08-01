package twitter;

public class TwitterSourceFactory {

    public TwitterSource createTwitterSource(String type) {

        if(type.equals("LIVE")) {
            return new LiveTwitterSource();
        }
        else if(type.equals("PLAYBACK")) {
            return new PlaybackTwitterSource(60);
        } else {
            return null;
        }
    }
}
