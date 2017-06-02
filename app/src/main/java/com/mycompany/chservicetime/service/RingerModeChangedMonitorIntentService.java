package com.mycompany.chservicetime.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.data.preference.PreferenceSupport;
import com.mycompany.chservicetime.presentation.timeslotlist.TimeSlotListActivity;
import com.mycompany.chservicetime.util.CHLog;

/**
 * Verify if the current ringer mode is correct. If not, send a notification with an action to reset
 * the ringer mode.
 */
public class RingerModeChangedMonitorIntentService extends IntentService {
    private static final String TAG = "RingerModeChangedMonito";

    public static final int NOTIFICATION_ID = 10;
    NotificationManager mNotificationManager;

    public RingerModeChangedMonitorIntentService() {
        super("RingerModeChangedMonitorIntentService");
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RingerModeChangedMonitorIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            CHLog.d(TAG, "RingerModeChangedMonito start");

            handleRingerMode();
        }
    }

    private void handleRingerMode() {
        // get the current ringer mode.
        AudioManager audioManager =
                (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int currentMode = audioManager.getRingerMode();

        // get the expect ringer mode.
        int expectMode = PreferenceSupport.getCurrentRingerMode(getApplicationContext());

        // compare two modes to decide if sending a notification
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (currentMode != expectMode) {
            sendNotification();
        } else {
            cancelNotification();
        }
    }

    // Post a notification with an action
    private void sendNotification() {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TimeSlotListActivity.class);
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TimeSlotListActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.notificaiton_reset_ringer_mode_title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText("Reset"))
                        .setContentText(getString(R.string.ringer_mode_monitor_message))
                        .setAutoCancel(true);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification n = mBuilder.build();
        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFICATION_ID, n);
    }


    private void cancelNotification() {
        mNotificationManager.cancel(NOTIFICATION_ID);
    }
}
