package com.flippbidd.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * A class with static util methods.
 */

public class DateUtils {

    // This class should not be initialized
    private DateUtils() {

    }


    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeAMPM(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static String formatTimeWithMarker(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    public static int getHourOfDay(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("H", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    public static int getMinute(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("m", Locale.getDefault());
        return Integer.valueOf(dateFormat.format(timeInMillis));
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     *
     * @param timeInMillis The time to convert, in milliseconds.
     * @return The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if (isToday(timeInMillis)) {
            return formatTimeAMPM(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }


    private static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());
        return calendar.getTime();
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(String date) {
        Date d = null;
        try {
            //old
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

            //new
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
//            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            df.setTimeZone(TimeZone.getDefault());
            d = df.parse(date);
            Log.e("Date ", "Date is" + date);

        } catch (ParseException e) {

        }

        long time = d.getTime();

        long now = currentDate().getTime();
        final long diff = now - time;
        if (diff < DAY_MILLIS) {
            return "Today";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else if (diff < 72 * HOUR_MILLIS) {
            return "3 day ago";
        } else if (diff < 96 * HOUR_MILLIS) {
            return "4 day ago";
        } else if (diff < 120 * HOUR_MILLIS) {
            return "5 day ago";
        } else if (diff < 144 * HOUR_MILLIS) {
            return "6 day ago";
        } else if (diff < 168 * HOUR_MILLIS) {
            return "in this week";
        } else {
            String date_after = formateDateFromstring("yyyy-MM-dd hh:mm:ss", "MM/dd/yy", date);
            return date_after;
        }
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat);
        df_input.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("DATE", "ParseException - dateFormat");
        }

        return outputDate;

    }

    public static long formatDateToLong(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e("DATE", "ParseException - dateFormat");
        }

        return parsed.getTime();

    }

    public static String timeFormat(String convertDate) {
//        String convertDate = "2021-09-04 02:58:00 PM";
        StringTokenizer tk = new StringTokenizer(convertDate);
        String date = tk.nextToken();
        String time = tk.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//        sdf.setTimeZone(TimeZone.getDefault());

        SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        sdfs.setTimeZone(TimeZone.getDefault());
//        sdfs.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dt = null;
        try {
            dt = sdf.parse(time);
            System.out.println("Time Input: " + sdf.format(dt)); // <-- I got result here
            System.out.println("Time Output: " + sdfs.format(dt)); // <-- I got result here
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdfs.format(dt);
    }

}
