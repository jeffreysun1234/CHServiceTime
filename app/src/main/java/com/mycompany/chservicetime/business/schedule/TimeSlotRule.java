package com.mycompany.chservicetime.business.schedule;

import com.mycompany.chservicetime.util.DateUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;


/**
 * Created by szhx on 4/6/2016.
 */
public class TimeSlotRule {
    private static final String TAG = makeLogTag("TimeSlotRule");

    /**
     * @param originalTimeSectors TimeSlot is ordered by begintime and endtime ascending.
     *                            Data format: In 24 hours, [0] is begin time, [1] is end time.
     * @param currentTimeInt      In 24 hours format, for example, 920 means 9:20am.
     * @return ServiceTime.nextAlarmTime : null means "No more alarm today". </p>
     * ServiceTime.currentOperation : null means keeping current state. no operation
     */
    public static ServiceTime getServiceTime(ArrayList<int[]> originalTimeSectors, int currentTimeInt) {
        ServiceTime serviceTime = new ServiceTime();

        serviceTime.currentTime = currentTimeInt;

        // Time Slots is no available, then keeping current state.
        if (originalTimeSectors == null) {
            serviceTime.nextAlarmTime = null;
            serviceTime.currentOperation = ServiceTime.INVALID;
            return serviceTime;
        }

        // all time slots are unactivated on today then setting the phone to the normal state.
        if (originalTimeSectors.size() == 0) {
            serviceTime.nextAlarmTime = DateUtils.getBeginTimeOfNextDay();
            serviceTime.currentOperation = ServiceTime.NO_OPERATION;
            return serviceTime;
        }

        /*
          merge overlapping TimeSlots
        */
        ArrayList<int[]> silentTimeSlotList = new ArrayList<int[]>();
        int[] timeSlotTemp = new int[2];

        timeSlotTemp[0] = originalTimeSectors.get(0)[0]; // begin time
        timeSlotTemp[1] = originalTimeSectors.get(0)[1]; // end time
        for (int i = 1; i < originalTimeSectors.size(); i++) {
            if (originalTimeSectors.get(i)[0] <= timeSlotTemp[1]) // there is a overlap part in a time slot.
            {
                // If the preceding time slot includes the current time slot, then skip the current time slot.
                // If two TimeSlots are cross, extends the end time to the end time of the next time slot.
                if (timeSlotTemp[1] < originalTimeSectors.get(i)[1]) {
                    timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                }
            } else {
                silentTimeSlotList.add(timeSlotTemp);
                timeSlotTemp = new int[2];
                timeSlotTemp[0] = originalTimeSectors.get(i)[0];
                timeSlotTemp[1] = originalTimeSectors.get(i)[1];
            }
        }
        silentTimeSlotList.add(timeSlotTemp);

        int timePoint = getTimePoint(silentTimeSlotList, serviceTime);

        try {
            LOGD(TAG, currentTimeInt + " --> "
                    + DateUtils.format(serviceTime.nextAlarmTime) + " === " + timePoint + " --> "
                    + serviceTime.currentOperation);
        } catch (ParseException e) {
        }

        return serviceTime;
    }

    /**
     * Get the most recent time point in the TimeSlot list based on the current time.
     *
     * @param timeSlotList
     * @param serviceTime  this data includes currentTime as a parameter. HHmm in 24 hour.
     * @return If the current time is not in TimeSlots, return -1;
     */
    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static int getTimePoint(ArrayList<int[]> timeSlotList, ServiceTime serviceTime) {
        int vTimePoint = -1;

//        Trace.beginSection("GetTimePoint");
//        try{

        System.out.println(Arrays.deepToString(timeSlotList.toArray()));
        LOGD(TAG, "TimeSlotList: " + Arrays.deepToString(timeSlotList.toArray()));

        for (int[] timeSlotTemp : timeSlotList) {
            // array structure is [beginTime, endTime].
            if (serviceTime.currentTime < timeSlotTemp[1]) {
                serviceTime.currentOperation = ServiceTime.Vibrate;
                serviceTime.nextAlarmTime = DateUtils.getCurrentTimestampOvernight(timeSlotTemp[1]);
                vTimePoint = timeSlotTemp[1];
                if (serviceTime.currentTime < timeSlotTemp[0]) {
                    serviceTime.currentOperation = ServiceTime.Normal;
                    serviceTime.nextAlarmTime = DateUtils.getCurrentTimestampOvernight(timeSlotTemp[0]);
                    vTimePoint = timeSlotTemp[0];
                }
                break;
            }
        }

        //If the current time is not in TimeSlots, that means "No more alarm today".
        if (vTimePoint == -1) {
            serviceTime.currentOperation = ServiceTime.NO_OPERATION;
            serviceTime.nextAlarmTime = DateUtils.getBeginTimeOfNextDay();
        }

        serviceTime.nextAlarmTimeInt = vTimePoint;

//        } finally {
//            Trace.endSection(); // ends "Get Time Point"
//        }

        return vTimePoint;
    }

    /**
     * @param currentDayInWeek Sunday is 1, Monday is 2, ... , Saturday is 7<p>
     *                         Code example: <p>
     *                         Calendar calendar = Calendar.getInstance();<p>
     *                         calendar.setTimeInMillis(System.currentTimeMillis());<p>
     *                         int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);<p>
     * @param activationFlag
     * @return a array list of begin time ( array index is 0) and end time (array index is 1).
     * Time is in 24 hour format.
     */
//    public ArrayList<int[]> getRequiredTimeSlots(int currentDayInWeek, boolean activationFlag) {
//        ArrayList<int[]> timeSectors = new ArrayList<int[]>();
//
//        ColumnIndexCache mColumnIndexCache = new ColumnIndexCache();
//
//        // Get all time slots.
//        Cursor cursor = mTimeSlotDataSource.getAllTimeSlot();
//        if (cursor != null) {
//            TimeSlot vTimeSlot;
//            int vBeginTime;
//            int vEndTime;
//
//            while (cursor.moveToNext()) {
//                vTimeSlot = ModelConverter.cursorToTimeSlotModel(cursor, mColumnIndexCache);
//
//                // Filter by day and activationFlag
//                if ('1' == vTimeSlot.days.charAt(currentDayInWeek - 1) // currentDayInWeek from 1 to 7
//                        && vTimeSlot.activationFlag.booleanValue() == activationFlag) {
//
//                    vBeginTime = vTimeSlot.beginTimeHour * 100 + vTimeSlot.beginTimeMinute;
//                    vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;
//
//                    // Format time. If begin_time > end_time, then end_time + 24 hours. It means next day.
//                    if (vBeginTime > vEndTime) {
//                        vEndTime = vEndTime + 2400;
//                    }
//
//                    timeSectors.add(new int[]{vBeginTime, vEndTime});
//                }
//
//                // Get overnight TimeSlots and filter by activationFlag
//                if ('1' == vTimeSlot.days.charAt((currentDayInWeek - 2) == -1 ? 6 : currentDayInWeek - 2)
//                        // currentDayInWeek from 1 to 7
//                        && vTimeSlot.activationFlag.booleanValue() == activationFlag) {
//
//                    vBeginTime = vTimeSlot.beginTimeHour * 100 + vTimeSlot.beginTimeMinute;
//                    vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;
//
//                    // Format time. Begin_time is from 0am.
//                    if (vBeginTime > vEndTime) {
//                        vBeginTime = 0;
//                        vEndTime = vTimeSlot.endTimeHour * 100 + vTimeSlot.endTimeMinute;
//
//                        timeSectors.add(new int[]{vBeginTime, vEndTime});
//                    }
//                }
//            }
//        }
//
//        // Sort by begin time and end time
//        Collections.sort(timeSectors, new Comparator<int[]>() {
//            @Override
//            public int compare(int[] lhs, int[] rhs) {
//                if (lhs[0] < rhs[0] || (lhs[0] == rhs[0] && lhs[1] < rhs[1])) {
//                    return -1;
//                } else if (lhs[0] > rhs[0] || (lhs[0] == rhs[0] && lhs[1] > rhs[1])) {
//                    return 1;
//                }
//                return 0;
//            }
//        });
//
//        return timeSectors;
//    }

}
