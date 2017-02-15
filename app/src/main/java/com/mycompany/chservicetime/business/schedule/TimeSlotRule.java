package com.mycompany.chservicetime.business.schedule;

import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.CHLog;
import com.mycompany.chservicetime.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.observables.ConnectableObservable;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;
import static java.lang.System.out;


/**
 * Created by szhx on 4/6/2016.
 */
public class TimeSlotRule {
    private static final String TAG = makeLogTag("TimeSlotRule");

    /**
     * If there are more than one operation in a same time, the greater ordinar is higher priority.
     *
     * @param originalTimeSectors TimeSlot is ordered by begintime and endtime ascending.
     *                            Data format: In 24 hours, [0] is begin time, [1] is end time.
     * @param currentTimeInt      In 24 hours format, for example, 920 means 9:20am.
     * @return ServiceTime.nextAlarmTime : null means "No more alarm today". </p>
     * ServiceTime.currentOperation : null means keeping current state. no operation
     */
    public static ServiceTime getServiceTime(List<int[]> originalTimeSectors, int currentTimeInt) {
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
        ArrayList<int[]> operationTimeSlotList = new ArrayList<int[]>();
        int[] timeSlotTemp = new int[3];

        timeSlotTemp[0] = originalTimeSectors.get(0)[0]; // begin time
        timeSlotTemp[1] = originalTimeSectors.get(0)[1]; // end time
        timeSlotTemp[2] = originalTimeSectors.get(0)[2]; // operation
        for (int i = 1; i < originalTimeSectors.size(); i++) {
            if (originalTimeSectors.get(i)[0] <= timeSlotTemp[1]) // there is a overlap part in a time slot.
            {
                // If the preceding time slot includes the current time slot,
                // then compare operation to slips or skip the current time slot by operation.
                // If two TimeSlots are cross, then compare operation to extends the end time
                // to the end time of the next time slot or slips the time slot by operation
                if (timeSlotTemp[1] < originalTimeSectors.get(i)[1]) { // cross
                    if (timeSlotTemp[2] < originalTimeSectors.get(i)[2]) { //  later overlay previous
                        operationTimeSlotList.add(
                                new int[]{timeSlotTemp[0], originalTimeSectors.get(i)[0], timeSlotTemp[2]});
                        timeSlotTemp[0] = originalTimeSectors.get(i)[0];
                        timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                        timeSlotTemp[2] = originalTimeSectors.get(i)[2];
                    } else if (timeSlotTemp[2] == originalTimeSectors.get(i)[2]) { // equal
                        timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                    } else { // previous overlay later
                        operationTimeSlotList.add(
                                new int[]{timeSlotTemp[0], timeSlotTemp[1], timeSlotTemp[2]});
                        timeSlotTemp[0] = timeSlotTemp[1];
                        timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                        timeSlotTemp[2] = originalTimeSectors.get(i)[2];
                    }
                } else if (timeSlotTemp[1] > originalTimeSectors.get(i)[1]) { // include
                    if (timeSlotTemp[2] < originalTimeSectors.get(i)[2]) { //  later overlay previous
                        operationTimeSlotList.add(
                                new int[]{timeSlotTemp[0], originalTimeSectors.get(i)[0], timeSlotTemp[2]});
                        operationTimeSlotList.add(
                                new int[]{originalTimeSectors.get(i)[0], originalTimeSectors.get(i)[1],
                                        originalTimeSectors.get(i)[2]});
                        timeSlotTemp[0] = originalTimeSectors.get(i)[1];
                    }
                } else { // timeSlotTemp[1] == originalTimeSectors.get(i)[1]
                    if (timeSlotTemp[2] < originalTimeSectors.get(i)[2]) { //  later overlay previous
                        operationTimeSlotList.add(
                                new int[]{timeSlotTemp[0], originalTimeSectors.get(i)[0], timeSlotTemp[2]});
                        timeSlotTemp[0] = originalTimeSectors.get(i)[1];
                        timeSlotTemp[2] = originalTimeSectors.get(i)[2];
                    }
                }
            } else {
                operationTimeSlotList.add(timeSlotTemp);
                timeSlotTemp = new int[3];
                timeSlotTemp[0] = originalTimeSectors.get(i)[0];
                timeSlotTemp[1] = originalTimeSectors.get(i)[1];
                timeSlotTemp[2] = originalTimeSectors.get(i)[2];
            }
        }
        operationTimeSlotList.add(timeSlotTemp);

        int timePoint = getTimePoint(operationTimeSlotList, serviceTime);

        CHLog.d(TAG, serviceTime.toString());

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

        out.println(Arrays.deepToString(timeSlotList.toArray()));
        CHLog.d(TAG, "TimeSlotList: " + Arrays.deepToString(timeSlotList.toArray()));

        for (int[] timeSlotTemp : timeSlotList) {
            // array structure is [beginTime, endTime].
            if (serviceTime.currentTime < timeSlotTemp[1]) {
                serviceTime.currentOperation = timeSlotTemp[2];
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
     * Get all TimeSlots of a specific day.
     *
     * @param dayInWeek      Sunday is 1, Monday is 2, ... , Saturday is 7<p>
     *                       Code example: DateUtils.getDayInWeek(...);
     * @param activationFlag
     * @return a array list of begin time ( array index is 0) and end time (array index is 1).
     * Time is in 24 hour format.
     */
    public static List<int[]> getRequiredTimeSlots(Observable<List<TimeSlot>> timeSlots,
                                                   int dayInWeek, boolean activationFlag) {
        final List<int[]> timeSectors = new ArrayList<int[]>();

        ConnectableObservable<TimeSlot> originalTimeSlots = timeSlots.flatMap(Observable::from)
                .publish();

        // Get normal TimeSlots and filter by activationFlag
        Observable<int[]> timeSectors1 = originalTimeSlots
                .filter(vTimeSlot -> // currentDayInWeek from 1 to 7
                        '1' == vTimeSlot.days().charAt(dayInWeek - 1)
                                && vTimeSlot.activation_flag().booleanValue() == activationFlag)
                .map(vTimeSlot -> {
                    int vBeginTime = vTimeSlot.begin_time_hour() * 100 + vTimeSlot.begin_time_minute();
                    int vEndTime = vTimeSlot.end_time_hour() * 100 + vTimeSlot.end_time_minute();

                    // Format time. If begin_time > end_time, then end_time + 24 hours. It means next day.
                    if (vBeginTime > vEndTime) {
                        vEndTime = vEndTime + 2400;
                    }
                    return new int[]{vBeginTime, vEndTime};
                });

        timeSectors1.subscribe((int[] vTimeSector) -> System.out.println(vTimeSector));

        // Get overnight TimeSlots and filter by activationFlag
        Observable<int[]> timeSectors2 = originalTimeSlots
                .filter(vTimeSlot ->
                        // currentDayInWeek from 1 to 7. If yesterday is set overnight, we need to get it.
                        '1' == vTimeSlot.days().charAt((dayInWeek - 2) == -1 ? 6 : dayInWeek - 2)
                                && vTimeSlot.activation_flag().booleanValue() == activationFlag)
                .map(vTimeSlot -> {
                    int vBeginTime = vTimeSlot.begin_time_hour() * 100 + vTimeSlot.begin_time_minute();
                    int vEndTime = vTimeSlot.end_time_hour() * 100 + vTimeSlot.end_time_minute();

                    // Format time. Begin_time is from 0am.
                    if (vBeginTime > vEndTime) {
                        vBeginTime = 0;
                        vEndTime = vTimeSlot.end_time_hour() * 100 + vTimeSlot.end_time_minute();
                        return new int[]{vBeginTime, vEndTime};
                    }
                    return null;
                });

        timeSectors2.subscribe((int[] vTimeSector) ->
                System.out.println("Sector1 : " + vTimeSector.toString()));

        Observable.concat(timeSectors1, timeSectors2)
                .filter(vTimeSector -> vTimeSector != null)
                .sorted((lhs, rhs) -> {
                    if (lhs[0] < rhs[0] || (lhs[0] == rhs[0] && lhs[1] < rhs[1])) {
                        return -1;
                    } else if (lhs[0] > rhs[0] || (lhs[0] == rhs[0] && lhs[1] > rhs[1])) {
                        return 1;
                    }
                    return 0;
                })
                .forEach(timeSectors::add);

        originalTimeSlots.connect();

        return timeSectors;
    }

    public static List<int[]> getRequiredTimeSlots(List<TimeSlot> timeSlots,
                                                   int dayInWeek, boolean activationFlag) {
        List<int[]> timeSectors = new ArrayList<int[]>();

        int vBeginTime;
        int vEndTime;

        for (TimeSlot vTimeSlot : timeSlots) {
            // Filter by day and activationFlag
            if ('1' == vTimeSlot.days().charAt(dayInWeek - 1) // currentDayInWeek from 1 to 7
                    && vTimeSlot.activation_flag().booleanValue() == activationFlag) {
                vBeginTime = vTimeSlot.begin_time_hour() * 100 + vTimeSlot.begin_time_minute();
                vEndTime = vTimeSlot.end_time_hour() * 100 + vTimeSlot.end_time_minute();
                // Format time. If begin_time > end_time, then end_time + 24 hours. It means next day.
                if (vBeginTime > vEndTime) {
                    vEndTime = vEndTime + 2400;
                }
                timeSectors.add(new int[]{vBeginTime, vEndTime, vTimeSlot.service_option().ordinal()});
            }

            // Get overnight TimeSlots and filter by activationFlag
            // currentDayInWeek from 1 to 7, Sunday, Monday, ... Saturday
            if ('1' == vTimeSlot.days().charAt((dayInWeek - 2) == -1 ? 6 : dayInWeek - 2)
                    && vTimeSlot.activation_flag().booleanValue() == activationFlag) {
                vBeginTime = vTimeSlot.begin_time_hour() * 100 + vTimeSlot.begin_time_minute();
                vEndTime = vTimeSlot.end_time_hour() * 100 + vTimeSlot.end_time_minute();
                // Format time. Begin_time is from 0am.
                if (vBeginTime > vEndTime) {
                    vBeginTime = 0;
                    timeSectors.add(new int[]{vBeginTime, vEndTime, vTimeSlot.service_option().ordinal()});
                }
            }
        }

        // Sort by begin time and end time
        Collections.sort(timeSectors, new Comparator<int[]>() {
                    @Override
                    public int compare(int[] lhs, int[] rhs) {
                        if (lhs[0] < rhs[0] || (lhs[0] == rhs[0] && lhs[1] < rhs[1])
                                || (lhs[0] == rhs[0] && lhs[1] == rhs[1] && lhs[2] < rhs[2])) {
                            return -1;
                        } else if (lhs[0] > rhs[0] || (lhs[0] == rhs[0] && lhs[1] > rhs[1])
                                || (lhs[0] == rhs[0] && lhs[1] == rhs[1] && lhs[2] > rhs[2])) {
                            return 1;
                        }
                        return 0;
                    }
                }
        );

        return timeSectors;
    }
}
