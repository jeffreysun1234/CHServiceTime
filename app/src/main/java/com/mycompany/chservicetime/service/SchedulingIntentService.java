package com.mycompany.chservicetime.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.mycompany.chservicetime.Injection;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.preference.PreferenceSupport;
import com.mycompany.chservicetime.data.source.TimeSlotRepository;
import com.mycompany.chservicetime.receiver.AlarmReceiver;
import com.mycompany.chservicetime.schedule.ServiceTime;
import com.mycompany.chservicetime.schedule.TimeSlotRule;
import com.mycompany.chservicetime.util.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SchedulingIntentService extends IntentService {
    public static final String TAG = makeLogTag("SchedulingIntentService");

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_SET_ALARM =
            "com.mycompany.chservicetime.schedule.action.SET_ALARM";
    public static final String ACTION_STOP_ALARM =
            "com.mycompany.chservicetime.schedule.action.STOP_ALARM";
    public static final String ACTION_INIT_ALARM =
            "com.mycompany.chservicetime.schedule.action.INIT_ALARM";

    public SchedulingIntentService() {
        super("SchedulingIntentService");
    }

    public static void startActionInitAlarm(Context context) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_INIT_ALARM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action "Set Alarm" with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSetAlarm(Context context) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_SET_ALARM);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action "Stop Alarm" with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionStopAlarm(Context context) {
        Intent intent = new Intent(context, SchedulingIntentService.class);
        intent.setAction(ACTION_STOP_ALARM);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            LOGD(TAG, "SchedulingIntentService Action=" + action);

            if (ACTION_SET_ALARM.equals(action)) {
                try {
                    handleActionSetAlarm();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_STOP_ALARM.equals(action)) {
                handleActionStopAlarm();
            } else if (ACTION_INIT_ALARM.equals(action)) {
                handleActionInitAlarm();
            }

            // Release the wake lock provided by the BroadcastReceiver.
            AlarmReceiver.completeWakefulIntent(intent);
        }
    }

    private void handleActionInitAlarm() {
        new AlarmReceiver().InitAlarm(getApplicationContext());
    }

    /**
     * Handle action "Set Alarm" in the provided background thread.
     */
    private void handleActionSetAlarm() throws ParseException {
        // Data repository
        TimeSlotRepository timeSlotRepository = Injection.provideTimeSlotsRepository(getApplicationContext());

        // get the number for current time, indicating the day of the week
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentDayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // get TimeSlot list for current time.
        ArrayList<int[]> timeSlotList = timeSlotRepository.getRequiredTimeSlots(currentDayInWeek, true);

        // get ServiceTime for current time.
        ServiceTime serviceTime = TimeSlotRule.getServiceTime(timeSlotList,
                DateUtils.getHHmm(System.currentTimeMillis()));
        LOGD(TAG, "Set Alarm: timePoint=" + serviceTime.nextAlarmTimeInt
                + "[" + DateUtils.format(serviceTime.nextAlarmTime)
                + "], operation = " + serviceTime.currentOperation
                + " for current time = " + serviceTime.currentTime);


        // set alarm and save alarm text for display.
        String alarmText = getString(R.string.next_operation_invalid);
        if (serviceTime.currentOperation != ServiceTime.INVALID) {
            new AlarmReceiver().setAlarm(getApplicationContext(), serviceTime.nextAlarmTime);

            StringBuffer sb = new StringBuffer();
            if (serviceTime.currentOperation == ServiceTime.Normal
                    || serviceTime.currentOperation == ServiceTime.Vibrate) {
                sb.append(" at ")
                        .append(new SimpleDateFormat("MMM dd, HH:mm 'on' EEE", Locale.US)
                                .format(serviceTime.nextAlarmTime));
                alarmText = sb.toString();
            }
            if (serviceTime.currentOperation == ServiceTime.NO_OPERATION) {
                alarmText = getString(R.string.next_operation_no);
            }
        }
        PreferenceSupport.setNextAlarmDetail(getApplicationContext(), alarmText);

        // execute the operation
        if (serviceTime.currentOperation == ServiceTime.Vibrate) {
            RingerModeIntentService.startActionSetRingerMode(
                    getApplicationContext(),
                    RingerModeIntentService.ACTION_SET_RINGER_MODE_VIBRATE);
        } else if (serviceTime.currentOperation == ServiceTime.Normal
                || serviceTime.currentOperation == ServiceTime.NO_OPERATION) {
            RingerModeIntentService.startActionSetRingerMode(
                    getApplicationContext(),
                    RingerModeIntentService.ACTION_SET_RINGER_MODE_NORMAL);
        }
    }

    /**
     * Handle action "Stop Alarm" in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStopAlarm() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
