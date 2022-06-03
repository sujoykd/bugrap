package com.example.bugrap.util;

import java.time.Instant;
import java.util.Date;

public final class GenericUtil {

    private GenericUtil() {
        // do nothing
    }

    public static String relativeTimeSpan(Date date) {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;
        final int WEEK_MILLIS = 7 * DAY_MILLIS;
        final int MONTH_MILLIS = 30 * DAY_MILLIS;
        final int YEAR_MILLIS = 365 * DAY_MILLIS;

        final long timeMillis = date.toInstant().getEpochSecond() * 1000;
        final long nowMillis = Instant.now().getEpochSecond() * 1000;

        final long diff = nowMillis - timeMillis;

        String result = "";

        if (timeMillis <= 0) {
            result = "In the unknonwn";
        } else if (timeMillis > nowMillis) {
            result = "In the future";
        } else if (diff < MINUTE_MILLIS) {
            result = "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            result = "1 min ago";
        } else if (diff < 59 * MINUTE_MILLIS) {
            result = diff / MINUTE_MILLIS + " mins ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            result = "1 hr ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            result = diff / HOUR_MILLIS + " hrs ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            result = "Yesterday";
        } else if (diff < WEEK_MILLIS) {
            result = diff / DAY_MILLIS + "days ago";
        } else if (diff < 2 * WEEK_MILLIS) {
            result = "1 week ago";
        } else if (diff < MONTH_MILLIS) {
            result = diff / WEEK_MILLIS + "weeks ago";
        } else if (diff < 2 * MONTH_MILLIS) {
            result = "1 month ago";
        } else if (diff < YEAR_MILLIS) {
            result = diff / MONTH_MILLIS + "months ago";
        } else if (diff < 2 * YEAR_MILLIS) {
            result = "1 year ago";
        } else {
            result = diff / YEAR_MILLIS + "years ago";
        }

        return result;
    }

}
