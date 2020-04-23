/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;

import java.util.List;

public class WorkoutFragment extends GenericFragment {
    private RecyclerView workoutsView;
    private WorkoutSession workoutSession;
    private List<WorkoutItem> workoutItemList;

    private WorkoutsAdapter workoutsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout, container, false);
        setHasOptionsMenu(true);

        workoutsView = root.findViewById(R.id.workoutsView);

        workoutsView.setHasFixedSize(true);
        workoutsView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFromDatabase();

        return root;
    }

    @Override
    protected String getTitle() {
        return workoutSession.getName();
    }

    @Override
    protected void loadFromDatabase() {
        final long workoutSessionId = WorkoutFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();
        workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);

        workoutItemList = workoutSession.getWorkoutItems();

        workoutsAdapter = new WorkoutsAdapter(getContext(), workoutItemList, mode);
        workoutsView.setAdapter(workoutsAdapter);
    }

    @Override
    protected void saveToDatabase() {
        WorkoutSession databaseWorkoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSession.getWorkoutSessionId());
        OpenWorkout.getInstance().deleteWorkoutSession(databaseWorkoutSession);

        // set workoutItem to zero to generate a new workoutItemId in the database needed for correctly reordering because the order is based on the workoutItemId otherwise the same workingItemId is again inserted
        for (WorkoutItem workoutItem : workoutItemList) {
            workoutItem.setWorkoutItemId(0);
        }

        OpenWorkout.getInstance().insertWorkoutSession(workoutSession);
    }

    @Override
    protected GenericAdapter getAdapter() {
        return workoutsAdapter;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return workoutsView;
    }

    @Override
    protected List getItemList() {
        return workoutItemList;
    }

    @Override
    protected void onSelectClick(int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);

        WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutSlideFragment action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutSlideFragment();
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        action.setWorkoutItemId(workoutItem.getWorkoutItemId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onEditClick(int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);

        WorkoutFragmentDirections.ActionWorkoutFramgentToWorkoutSettingsFragment action = WorkoutFragmentDirections.actionWorkoutFramgentToWorkoutSettingsFragment();
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        action.setWorkoutItemId(workoutItem.getWorkoutItemId());
        action.setMode(WorkoutSettingsFragment.WORKOUT_MODE.EDIT);
        action.setTitle(getString(R.string.label_edit));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onDeleteClick(int position) {
        Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), workoutItemList.get(position).getName()), Toast.LENGTH_SHORT).show();
        workoutItemList.remove(position);
    }

    @Override
    protected void onAddClick() {
        WorkoutFragmentDirections.ActionWorkoutFramgentToWorkoutSettingsFragment action = WorkoutFragmentDirections.actionWorkoutFramgentToWorkoutSettingsFragment();
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        action.setMode(WorkoutSettingsFragment.WORKOUT_MODE.ADD);
        action.setTitle(getString(R.string.label_add));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
