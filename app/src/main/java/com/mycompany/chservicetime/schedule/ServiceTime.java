package com.mycompany.chservicetime.schedule;

/**
 * Created by szhx on 8/29/2016.
 */
public class ServiceTime {
    public static final Integer INVALID = null;
    public static final Integer Vibrate = 1;
    public static final Integer Normal = 2;
    public static final Integer NO_OPERATION = 3;

    public int currentTime;
    public Integer currentOperation;
    // Timestamp
    public Long nextAlarmTime;
    // HHmm in 24 hour
    public int nextAlarmTimeInt;

}
