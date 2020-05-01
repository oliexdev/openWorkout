/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.utils.SoundUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TrophyFragment extends Fragment {

    private TextView elapsedTimeView;
    private Button okView;
    private SoundUtils soundUtils;

    private WorkoutSession workoutSession;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trophy, container, false);

        elapsedTimeView = root.findViewById(R.id.elapsedTimeView);
        okView = root.findViewById(R.id.okView);

        long workoutSessionId = TrophyFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();

        workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);

        Calendar elapsedCalendar = Calendar.getInstance();

        elapsedCalendar.setTimeInMillis(workoutSession.getElapsedSessionTime() * 1000);

        DateFormat dateFormatter= SimpleDateFormat.getTimeInstance();
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        elapsedTimeView.setText(dateFormatter.format(elapsedCalendar.getTime()));

        soundUtils = new SoundUtils();
        soundUtils.playSound(SoundUtils.SOUND.SESSION_COMPLETED);

        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
            }
        });

        return root;
    }
}
