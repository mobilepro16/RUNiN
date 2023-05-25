package com.runin.runinapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.runin.runinapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Date utils
 * Created by Cesar on 12/08/2016.
 */
public class DateUtils {
    private static final String TAG = DateUtils.class.getSimpleName();

    /**
     * Get the Age from a date
     *
     * @param _year  year selected
     * @param _month month selected
     * @param _day   day selected
     * @return Returns the age of the user born in the specified date
     */
    public static int getAge(int _year, int _month, int _day) {
        int age;

        final Calendar calendarToday = Calendar.getInstance();
        int currentYear = calendarToday.get(Calendar.YEAR);
        int currentMonth = 1 + calendarToday.get(Calendar.MONTH);
        int todayDay = calendarToday.get(Calendar.DAY_OF_MONTH);

        Log.i(TAG, String.format("Date is: %d %d %d", currentYear, currentMonth, todayDay));
        Log.i(TAG, String.format("BD   is: %d %d %d", _year, _month, _day));

        age = currentYear - _year;

        if (_month > currentMonth) {
            --age;
        }
        else if (_month == currentMonth) {
            if (_day > todayDay) {
                --age;
            }
        }
        Log.i(TAG, String.format("Age is: %d", age));
        return age;
    }

    /**
     * Returns the amount of given minutes formatted as text
     *
     * @param minutes The amount of minutes to convert
     * @return The formatted text
     */
    public static String minutesToMinSec(double minutes) {
        SimpleDateFormat formatter;
        if (minutes >= 60) {
            formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        }
        else {
            formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        }

        long millis = (long) (minutes * 60000.0);

        return formatter.format(new Date(millis));
    }

    /**
     * Convert milliseconds to time string mm:ss
     *
     * @param time The time in milliseconds
     * @return The time formatted
     */
    public static String millisToMinSec(long time) {
        SimpleDateFormat formatter;

        if (time > 3600000) {
            formatter = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        }
        else {
            formatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
        }

        Date date = new Date(time);

        return formatter.format(date);
    }

    /**
     * Convert the given amount of milliseconds on text suitable for the text to speech reader
     *
     * @param context The application context
     * @param time    The amount of milliseconds
     * @return The formatted time
     */
    public static String millisToLongTextString(Context context, long time) {
        if (time < 0) throw new IllegalStateException("Time is zero");

        long minutes = (time / 1000) / 60;
        long seconds = (time / 1000) % 60;
        Resources res = context.getResources();
        StringBuilder format = new StringBuilder();

        if (time > 0) {
            if (minutes > 0) {
                format.append(res.getQuantityString(R.plurals.long_minutes, (int) minutes)).append(" ");
            }

            if (seconds > 0) {
                format.append(res.getQuantityString(R.plurals.long_seconds, (int) seconds));
            }
        }
        else {
            format.append(res.getQuantityString(R.plurals.long_seconds, (int) seconds));
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format.toString().trim(), Locale.ROOT);
        Date date = new Date(time);

        return formatter.format(date);
    }

    public static String minutesToLongTextString(Context context, double time) {
        return millisToLongTextString(context, (long) (time * 60000.0));
    }

    /**
     * Removes the time portion of the date
     *
     * @param date Any date with date and time portions
     * @return The date without the time portion
     * @throws ParseException In case date cannot be parsed. Unlikely.
     */
    public static Date dateOnly(Date date) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return formatter.parse(formatter.format(date));
    }

    /**
     * Avoid date representation with time zones, when all we want to is convert from milliseconds to hh:mm:ss
     */
    public static String durationToMinSec(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

        if (hours > 0) return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
