package util;

import java.util.Set;

public class Logger {


    public static void printLiveSyncing(Set<String> terms) {
        System.out.println("Syncing live Twitter stream with " + terms);
    }

    public static void printPlaybackSyncing(Set<String> terms) {
        System.out.println("Starting playback thread with " + terms);
    }
}
