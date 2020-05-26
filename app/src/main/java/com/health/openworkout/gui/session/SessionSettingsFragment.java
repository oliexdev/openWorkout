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
