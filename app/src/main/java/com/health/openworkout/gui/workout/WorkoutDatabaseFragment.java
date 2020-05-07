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
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

import java.util.List;

public class WorkoutDatabaseFragment extends Fragment {
    private RecyclerView workoutsView;
    private List<WorkoutItem> workoutItemList;

    private WorkoutsDatabaseAdapter workoutsDatabaseAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workoutdatabase, container, false);

        workoutsView = root.findViewById(R.id.workoutsView);

        workoutsView.setHasFixedSize(true);
        workoutsView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFromDatabase();

        return root;
    }

    protected void loadFromDatabase() {
        workoutItemList = OpenWorkout.getInstance().getAllUniqueWorkoutItems();
        WorkoutItem emptyWorkout = new WorkoutItem();
        emptyWorkout.setWorkoutItemId(-1L);
        workoutItemList.add(0, emptyWorkout);

        workoutsDatabaseAdapter = new WorkoutsDatabaseAdapter(getContext(), workoutItemList);

        workoutsDatabaseAdapter.setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                final long workoutSessionId = WorkoutFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();

                WorkoutDatabaseFragmentDirections.ActionWorkoutDatabaseFragmentToWorkoutSettingsFragment action = WorkoutDatabaseFragmentDirections.actionWorkoutDatabaseFragmentToWorkoutSettingsFragment();
                action.setMode(GenericSettingsFragment.SETTING_MODE.ADD);
                action.setTitle(getString(R.string.label_add));
                action.setSessionWorkoutId(workoutSessionId);
                action.setWorkoutItemId(workoutItemList.get(position).getWorkoutItemId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        workoutsView.setAdapter(workoutsDatabaseAdapter);
    }
}
