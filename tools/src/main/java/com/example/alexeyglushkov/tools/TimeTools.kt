package com.example.alexeyglushkov.tools

import java.util.concurrent.TimeUnit

/**
 * Created by alexeyglushkov on 26.08.16.
 */
object TimeTools {
    @JvmStatic
    fun getDurationString(millis: Long): String {
        var millis = millis
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        millis -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        val sb = StringBuilder(64)
        if (days > 0) {
            sb.append(days)
            sb.append(" days")
        } else if (hours > 0) {
            sb.append(hours)
            sb.append(" hours")
        } else if (minutes > 0) {
            sb.append(minutes)
            sb.append(" minutes")
        } else if (seconds > 0) {
            sb.append(seconds)
            sb.append(" seconds")
        }
        return sb.toString()
    }

    @JvmStatic
    fun currentTimeSeconds(): Long {
        return System.currentTimeMillis() / 1000L
    }
}