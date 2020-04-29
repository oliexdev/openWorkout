/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(AlarmHandler.INTENT_EXTRA_ALARM)) {
            handleAlarm(context);
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            scheduleAlarms(context);
        }
    }

    private void handleAlarm(Context context) {
        AlarmHandler alarmHandler = new AlarmHandler();
        alarmHandler.showAlarmNotification(context);
    }

    private void scheduleAlarms(Context context) {
        AlarmHandler alarmHandler = new AlarmHandler();

        alarmHandler.scheduleAlarms(context);
    }
}
