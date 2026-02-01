package com.lit.fire.flame;

import com.lit.fire.api.SocialMediaScanner;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static class ScannableService {
        private final SocialMediaScanner scanner;
        private final String name;
        private long nextScanTime;

        public ScannableService(SocialMediaScanner scanner, String name) {
            this.scanner = scanner;
            this.name = name;
            this.nextScanTime = System.currentTimeMillis();
        }

        public void tryScan() {
            if (System.currentTimeMillis() >= nextScanTime) {
                try {
                    System.out.println("Scanning " + name + "...");
                    scanner.scan();
                    System.out.println("Successfully scanned " + name + ".");
                    nextScanTime = System.currentTimeMillis() + 60 * 60 * 1000; // 1 hour
                } catch (Exception e) {
                    System.err.println("An error occurred during " + name + " scanning: " + e.getMessage());
                    e.printStackTrace();
                    nextScanTime = System.currentTimeMillis() + 6 * 60 * 60 * 1000; // 6 hours
                }
            }
        }
    }

    public static void main(String[] args) {
        List<ScannableService> services = new ArrayList<>();
        services.add(new ScannableService(new InstagramService(), "Instagram"));
        services.add(new ScannableService(new RedditAuthClientWithSearch(), "Reddit"));
        services.add(new ScannableService(new XService(), "X"));
        services.add(new ScannableService(new YouTubeMain(), "YouTube"));

        while (true) {
            for (ScannableService service : services) {
                service.tryScan();
            }

            try {
                Thread.sleep(60 * 1000); // Check every minute
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
