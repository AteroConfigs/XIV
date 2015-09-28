package pw.latematt.xiv.utils;

/**
 * This class is to help with converting time to milliseconds. Used mainly for
 * Thread.sleep and other millisecond-related time measures.
 * 
 * @author Matthew/
 */
public class TimeConverter {
	private int second = 1000, minute = second * 60, hour = minute * 60, day = hour * 24, week = day * 7;

    /* x to milliseconds */
    public int daysToMilliseconds(int days) {
        return day * days;
    }

    public int hoursToMilliseconds(int hours) {
        return hour * hours;
    }

    public int minutesToMilliseconds(int minutes) {
        return minute * minutes;
    }

    public int secondsToMilliseconds(int seconds) {
        return second * seconds;
    }

    public int weeksToMilliseconds(int weeks) {
        return week * weeks;
    }

    /* milliseconds to x */
    public int millisecondsToDays(int milliseconds) {
        return milliseconds / day;
    }

    public int millisecondsToHours(int milliseconds) {
        return milliseconds / hour;
    }

    public int millisecondsToMinutes(int milliseconds) {
        return milliseconds / minute;
    }

    public int millisecondsToSeconds(int milliseconds) {
        return milliseconds / second;
    }

    public int millisecondsToWeeks(int milliseconds) {
        return milliseconds / week;
    }
}
