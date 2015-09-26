package pw.latematt.xiv.utils;

/**
 * @author Matthew
 */
public class Timer {
    private long lastMS;

    public long getCurrentMS() {
        return System.nanoTime() / 1000000;
    }

    public long getLastMS() {
        return lastMS;
    }

    public boolean hasReached(long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

    public void setLastMS(long currentMS) {
        lastMS = currentMS;
    }
}
