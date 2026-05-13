package com.example.safeher.utils;

public class MultiTapDetector {

    private final int requiredTaps;
    private final long windowMillis;
    private int currentTapCount;
    private long firstTapTimestamp;

    public MultiTapDetector(int requiredTaps, long windowMillis) {
        this.requiredTaps = requiredTaps;
        this.windowMillis = windowMillis;
        this.currentTapCount = 0;
        this.firstTapTimestamp = 0L;
    }

    public boolean registerTap() {
        long now = System.currentTimeMillis();

        if (firstTapTimestamp == 0L || (now - firstTapTimestamp > windowMillis)) {
            firstTapTimestamp = now;
            currentTapCount = 1;
            return false;
        }

        currentTapCount++;
        if (currentTapCount >= requiredTaps) {
            reset();
            return true;
        }

        return false;
    }

    public void reset() {
        firstTapTimestamp = 0L;
        currentTapCount = 0;
    }
}
