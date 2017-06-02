package com.mycompany.chservicetime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mycompany.chservicetime.service.RingerModeChangedMonitorIntentService;

/**
 * Receive android.media.RINGER_MODE_CHANGED broadcast from system, then trigger
 * RingerModeChangedMonitorIntentService to verify if the current ringer mode is correct.
 */
public class RingerModeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.media.RINGER_MODE_CHANGED")) {
            RingerModeChangedMonitorIntentService.start(context);
        }
    }
}
