package com.ptrprograms.stayawake.Utils;

/**
 * Created by PaulTR on 6/29/14.
 */
public class TimeUtil {
    private static final String TWO_DIGITS = "%02d";
    private static final String ONE_DIGIT = "%01d";

    private static String mMinutes;
    private static String mSeconds;

    private TimeUtil() {}

    private static void setTime(long time) {
        String format;

        long seconds = time / 1000;
        long hundreds = (time - seconds * 1000) / 10;
        long minutes = seconds / 60;
        seconds = seconds - minutes * 60;
        long hours = minutes / 60;
        minutes = minutes - hours * 60;
        if (hours > 999) {
            hours = 0;
        }

        if (hundreds != 0) {
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
        }

        if (minutes >= 10 || hours > 0) {
            format = TWO_DIGITS;
            mMinutes = String.format(format, minutes);
        } else {
            format = ONE_DIGIT;
            mMinutes = String.format(format, minutes);
        }

        mSeconds = String.format( TWO_DIGITS, seconds );
    }

    public static String getTimeString(long time) {
        setTime(time);
        return String.format("%s:%s", mMinutes, mSeconds);

    }
}
