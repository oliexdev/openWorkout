/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.preference;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.health.openworkout.R;

public class MainPreferences extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey);

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
    }
}
