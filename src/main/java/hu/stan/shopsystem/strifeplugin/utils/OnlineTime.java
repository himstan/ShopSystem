package hu.stan.shopsystem.strifeplugin.utils;

public class OnlineTime {

    private int days;
    private int hours;
    private int minutes;
    private int seconds;
    private int totalSeconds;
    private final int SECOND_TO_DAY = 60 * 60 * 24;
    private final int SECOND_TO_HOUR = 60 * 60;
    private final int SECOND_TO_MINUTE = 60;

    public OnlineTime(int totalSeconds) {
        this.totalSeconds = totalSeconds;
        seconds = totalSeconds;
        days = seconds / SECOND_TO_DAY;
        seconds -= days * SECOND_TO_DAY;
        hours = seconds / SECOND_TO_HOUR;
        seconds -= hours * SECOND_TO_HOUR;
        minutes = seconds / SECOND_TO_MINUTE;
        seconds -= minutes * SECOND_TO_MINUTE;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (getDays() > 0) {
            stringBuilder.append(getDays()).append(" days, ");
        }
        if (getHours() > 0) {
            stringBuilder.append(getHours()).append(" hours, ");
        }
        if (getMinutes() > 0) {
            stringBuilder.append(getMinutes()).append(" minutes, ");
        }
        stringBuilder.append(getSeconds()).append(" seconds");
        return stringBuilder.toString();
    }
}
