/*
 * Copyright (C) 2020 olie.xdev <olie.xdev@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.health.openworkout.core;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = sharedPreferences.getBoolean("darkTheme", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        // Create openWorkout instance
        OpenWorkout.createInstance(getApplicationContext());

        // Hold on to the instance for as long as the application exists
        openWorkout = OpenWorkout.getInstance();
    }
}
