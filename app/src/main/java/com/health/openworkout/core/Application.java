/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.health.openworkout.BuildConfig;
import com.health.openworkout.core.utils.PlayStoreUtils;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = sharedPreferences.getBoolean("darkTheme", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Create openWorkout instance
        OpenWorkout.createInstance(getApplicationContext());

        // Create playStore utils instance
        PlayStoreUtils.createInstance(getApplicationContext());

        // Hold on to the instance for as long as the application exists
        openWorkout = OpenWorkout.getInstance();
    }
}
