/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

public class SessionSettingsFragment extends GenericSettingsFragment {
    private WorkoutSession workoutSession;

    private ImageView imgView;
    private TextView nameView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sessionsettings, container, false);

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);

        setMode(SessionSettingsFragmentArgs.fromBundle(getArguments()).getMode());

        return root;
    }

    @Override
    protected String getTitle() {
        return workoutSession.getName();
    }

    @Override
    protected void loadFromDatabase(SETTING_MODE mode) {
        switch (mode) {
            case ADD:
                workoutSession = new WorkoutSession();
                break;
            case EDIT:
                long workoutSessionId = SessionSettingsFragmentArgs.fromBundle(getArguments()).getWorkoutSessionId();
                workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);
                break;
        }

        if (workoutSession.isFinished()) {
            imgView.setImageResource(R.drawable.ic_session_done);
        } else {
            imgView.setImageResource(R.drawable.ic_session_undone);
        }

        nameView.setText(workoutSession.getName());
    }

    @Override
    protected boolean saveToDatabase(SETTING_MODE mode) {
        workoutSession.setName(nameView.getText().toString());

        switch (mode) {
            case ADD:
                long trainingPlanId = SessionSettingsFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();

                workoutSession.setTrainingPlanId(trainingPlanId);
                OpenWorkout.getInstance().insertWorkoutSession(workoutSession);
                break;
            case EDIT:
                OpenWorkout.getInstance().updateWorkoutSession(workoutSession);
                break;
        }

        return true;
    }



}
