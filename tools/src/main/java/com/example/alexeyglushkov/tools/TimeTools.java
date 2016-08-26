package com.example.alexeyglushkov.tools;

import java.util.concurrent.TimeUnit;

/**
 * Created by alexeyglushkov on 26.08.16.
 */
public final class TimeTools {
    public static String getDurationString(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(" days");
        } else if (hours > 0) {
            sb.append(hours);
            sb.append(" hours");
        } else if (minutes > 0) {
            sb.append(minutes);
            sb.append(" minutes");
        } else if (seconds > 0) {
            sb.append(seconds);
            sb.append(" seconds");
        }

        return sb.toString();
    }
}
