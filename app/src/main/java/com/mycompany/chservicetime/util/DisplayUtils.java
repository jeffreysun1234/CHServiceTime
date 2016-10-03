package com.mycompany.chservicetime.util;

import java.util.Locale;

/**
 * Created by szhx on 4/13/2016.
 */
public class DisplayUtils {
    public static String daysToText(String days) {
        String[] weekText = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        StringBuffer daysText = new StringBuffer();
        for (int i = 0; i < days.length(); i++) {
            if (Character.getNumericValue(days.charAt(i)) == 1)
                daysText.append(weekText[i]).append(", ");
        }
        if (daysText.length() > 0)
            daysText.deleteCharAt(daysText.length() - 2);

        return daysText.toString();
    }

    public static String repeatFlagToText(boolean repeatFlag) {
        return repeatFlag ? "Repeat weekly" : "";
    }

    public static String buildTimePeriodString(int beginTimeHour, int beginTimeMinute, int endTimeHour, int
            endTimeMinute) {
        return String.format(Locale.US, "%02d", beginTimeHour)
                + ":"
                + String.format(Locale.US, "%02d", beginTimeMinute)
                + " --- "
                + String.format(Locale.US, "%02d", endTimeHour)
                + ":"
                + String.format(Locale.US, "%02d", endTimeMinute);
    }
}
