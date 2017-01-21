package com.mycompany.chservicetime.business.schedule;

/**
 * Created by szhx on 8/29/2016.
 */
public class ServiceTime {
    public static final Integer INVALID = null;
    public static final Integer Normal = 0;
    public static final Integer Vibrate = 1;
    public static final Integer Mute = 2;
    public static final Integer NO_OPERATION = -1;

    public int currentTime;
    public Integer currentOperation;
    // Timestamp
    public Long nextAlarmTime;
    // HHmm in 24 hour
    public int nextAlarmTimeInt;

    @Override
    public String toString() {
        return "ServiceTime{" +
                "currentTime=" + currentTime +
                ", currentOperation=" + currentOperation +
                ", nextAlarmTime=" + nextAlarmTime +
                ", nextAlarmTimeInt=" + nextAlarmTimeInt +
                '}';
    }
}
