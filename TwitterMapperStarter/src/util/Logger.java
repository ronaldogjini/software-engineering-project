package util;

import twitter4j.Status;

import java.util.Set;

public class Logger {


    public static void printLiveSyncing(Set<String> terms) {
        System.out.println("Syncing live Twitter stream with " + terms);
    }

    public static void printPlaybackSyncing(Set<String> terms) {
        System.out.println("Starting playback thread with " + terms);
    }

    protected void log(Status status, Boolean doLogging) {
        if (doLogging) {
            System.out.println(status.getUser().getName() + ": " + status.getText());
        }
        ImageCache.getInstance().loadImage(status.getUser().getProfileImageURL());
    }
}
