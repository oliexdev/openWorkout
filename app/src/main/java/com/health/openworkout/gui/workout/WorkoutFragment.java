/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;

import java.util.List;

public class WorkoutFragment extends Fragment {

    private RecyclerView workoutsView;
    private WorkoutSession workoutSession;
    private List<WorkoutItem> workoutItemList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout, container, false);

        workoutsView = root.findViewById(R.id.workoutsView);

        workoutsView.setHasFixedSize(true);
        workoutsView.setLayoutManager(new LinearLayoutManager(getContext()));

        long workoutSessionId = WorkoutFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();
        workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);

        workoutItemList = workoutSession.getWorkoutItems();

        WorkoutsAdapter workoutsAdapter = new WorkoutsAdapter(getContext(), workoutItemList);
        workoutsView.setAdapter(workoutsAdapter);

        workoutsAdapter.setOnItemClickListener(new WorkoutsAdapter.OnWorkoutClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                WorkoutItem workoutItem = workoutItemList.get(position);

                WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutSlideFragment action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutSlideFragment();
                action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                action.setWorkoutItemId(workoutItem.getWorkoutItemId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        return root;
    }
}
