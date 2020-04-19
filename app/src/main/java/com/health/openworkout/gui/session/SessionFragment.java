/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.WorkoutSession;

import java.util.List;

import timber.log.Timber;

public class SessionFragment extends Fragment {
    private GridView sessionsView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_session, container, false);

        sessionsView = root.findViewById(R.id.sessionsView);

        init();

        return root;
    }

    public void init() {
        long trainingPlanId = SessionFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();
        TrainingPlan trainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlanId);

        List<WorkoutSession> workoutSessionList = trainingPlan.getWorkoutSessions();

        sessionsView.setAdapter(new SessionsAdapter(getContext(), workoutSessionList));

        sessionsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                WorkoutSession workoutSession = (WorkoutSession)sessionsView.getItemAtPosition(position);
                Timber.d("SELECTED WORKOUT SESSION ID " + workoutSession.getTrainingPlanId());

                SessionFragmentDirections.ActionSessionFragmentToWorkoutFragment action = SessionFragmentDirections.actionSessionFragmentToWorkoutFragment();
                action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });
    }
}
