package com.mycompany.chservicetime;

import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.DateUtils;

import java.text.ParseException;

/**
 * Created by szhx on 6/10/2017.
 */

public class TestDataHelper {

    /**
     * @param mode the sound mode.
     * @return
     */
    public static TimeSlot getTimeSlotWithCurrentTime(TimeSlot.ServiceOption mode) {
        int vBeginTime = 0;
        int vEndTime = 0;
        try {
            vBeginTime = DateUtils.getHHmm(System.currentTimeMillis());
            vEndTime = DateUtils.getHHmm(System.currentTimeMillis() + 10 * 60 * 1000); // future 10 minute
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int vBeginHour = vBeginTime / 100;
        int vBeginMinute = vBeginTime % 100;
        int vEndHour = vEndTime / 100;
        int vEndMinute = vEndTime % 100;

        return TimeSlot.createTimeSlot("111", "Work", "work time",
                vBeginHour, vBeginMinute, vEndHour, vEndMinute, "1111111",
                true, false, mode);
    }
}
