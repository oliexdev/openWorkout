/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.preference;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.health.openworkout.BuildConfig;
import com.health.openworkout.R;
import com.health.openworkout.core.utils.PackageUtils;
import com.health.openworkout.gui.utils.FileDialogHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

public class MainPreferences extends PreferenceFragmentCompat {
    private FileDialogHelper fileDialogHelper;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);

        fileDialogHelper = new FileDialogHelper(this);

        final SwitchPreferenceCompat prefDarkTheme = findPreference("darkTheme");
        prefDarkTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!prefDarkTheme.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                return true;
            }
        });

        final Preference prefReminder = findPreference("reminder");
        prefReminder.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                NavDirections action = MainPreferencesDirections.actionMainPreferencesFragmentToReminderPreferencesFragment();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                return true;
            }
        });

        final SwitchPreferenceCompat prefDebugLogging = findPreference("debugLogging");
        prefDebugLogging.setChecked(getEnabledFileDebugTree() != null);
        prefDebugLogging.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!prefDebugLogging.isChecked()) {
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
                    String fileName = String.format("openWorkout_%s.txt", format.format(new Date()));

                    fileDialogHelper.openDebugFileDialog(fileName);
                } else {
                    FileDebugTree tree = getEnabledFileDebugTree();
                    if (tree != null) {
                        Timber.d("Debug log disabled");
                        Timber.uproot(tree);
                        tree.close();
                    }
                }

                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        fileDialogHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fileDialogHelper.onActivityResult(requestCode, resultCode, data)) {
            Uri uri = data.getData();

            startLogTo(uri);
        }
    }

    private void startLogTo(Uri uri) {
        try {
            OutputStream output = getActivity().getContentResolver().openOutputStream(uri);
            Timber.plant(new FileDebugTree(output));
            Timber.d("Debug log enabled, %s v%s (%d), SDK %d, %s %s",
                    getResources().getString(R.string.app_name),
                    BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE,
                    Build.VERSION.SDK_INT, Build.MANUFACTURER, Build.MODEL);
        }
        catch (IOException ex) {
            Timber.e(ex, "Failed to open debug log %s", uri.toString());
        }
    }

    private FileDebugTree getEnabledFileDebugTree() {
        for (Timber.Tree tree : Timber.forest()) {
            if (tree instanceof FileDebugTree) {
                return (FileDebugTree) tree;
            }
        }
        return null;
    }

    class FileDebugTree extends Timber.DebugTree {
        PrintWriter writer;
        DateFormat format;

        FileDebugTree(OutputStream output) {
            writer = new PrintWriter(output, true);
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }

        void close() {
            writer.close();
        }

        private String priorityToString(int priority) {
            switch (priority) {
                case Log.ASSERT:
                    return "Assert";
                case Log.ERROR:
                    return "Error";
                case Log.WARN:
                    return "Warning";
                case Log.INFO:
                    return "Info";
                case Log.DEBUG:
                    return "Debug";
                case Log.VERBOSE:
                    return "Verbose";
            }
            return String.format("Unknown (%d)", priority);
        }

        @Override
        protected synchronized void log(int priority, String tag, String message, Throwable t) {
            final long id = Thread.currentThread().getId();
            writer.printf("%s %s [%d] %s: %s\r\n",
                    format.format(new Date()), priorityToString(priority), id, tag, message);
        }
    }
}
