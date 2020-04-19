/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core;

import com.health.openworkout.BuildConfig;

import timber.log.Timber;

public class Application extends android.app.Application {
    OpenWorkout openWorkout;

    private class TimberLogAdapter extends Timber.DebugTree {
        @Override
        protected boolean isLoggable(String tag, int priority) {
            if (BuildConfig.DEBUG || OpenWorkout.DEBUG_MODE) {
                return super.isLoggable(tag, priority);
            }
            return false;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new TimberLogAdapter());

        // Create openWorkout instance
        OpenWorkout.createInstance(getApplicationContext());

        // Hold on to the instance for as long as the application exists
        openWorkout = OpenWorkout.getInstance();
    }
}
