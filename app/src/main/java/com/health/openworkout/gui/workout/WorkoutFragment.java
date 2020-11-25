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

package com.health.openworkout.gui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

import java.util.List;

public class WorkoutFragment extends GenericFragment {
    private RecyclerView workoutsView;
    private WorkoutSession workoutSession;
    private List<WorkoutItem> workoutItemList;

    private FloatingActionButton expandableButton;
    private FloatingActionButton addButton;
    private LinearLayout addLayout;
    private Animation animFabOpen, animFabClose, animFabClock, animFabAntiClock;
    private boolean isExpandable;

    private WorkoutsAdapter workoutsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout, container, false);

        workoutsView = root.findViewById(R.id.workoutsView);

        workoutsView.setHasFixedSize(true);
        workoutsView.setLayoutManager(new LinearLayoutManager(getContext()));

        isExpandable = false;

        expandableButton = root.findViewById(R.id.expandableButton);
        addButton = root.findViewById(R.id.addButton);
        addLayout = root.findViewById(R.id.addLayout);

        animFabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        animFabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        animFabClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        animFabAntiClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);

        expandableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandable) {
                    addLayout.setVisibility(View.GONE);
                    addLayout.startAnimation(animFabClose);
                    expandableButton.startAnimation(animFabAntiClock);
                    isExpandable = false;
                } else {
                    addLayout.setVisibility(View.VISIBLE);
                    addLayout.startAnimation(animFabOpen);
                    expandableButton.startAnimation(animFabClock);
                    isExpandable = true;
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutDatabaseFragment action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutDatabaseFragment();
                action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

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

        workoutsAdapter = new WorkoutsAdapter(getContext(), workoutItemList);
        workoutsAdapter.setMode(getMode());
        workoutsView.setAdapter(workoutsAdapter);
    }

    @Override
    protected void saveToDatabase() {
        for (int i=0; i<workoutItemList.size(); i++) {
            workoutItemList.get(i).setOrderNr(i);
            OpenWorkout.getInstance().updateWorkoutItem(workoutItemList.get(i));
        }
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
    protected void onSelectCallback(int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);

        WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutSlideFragment action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutSlideFragment();
        action.setTitle(workoutSession.getName());
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        action.setWorkoutItemId(workoutItem.getWorkoutItemId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onEditCallback(int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);

        WorkoutFragmentDirections.ActionWorkoutFramgentToWorkoutSettingsFragment action = WorkoutFragmentDirections.actionWorkoutFramgentToWorkoutSettingsFragment();
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        action.setWorkoutItemId(workoutItem.getWorkoutItemId());
        action.setMode(GenericSettingsFragment.SETTING_MODE.EDIT);
        action.setTitle(getString(R.string.label_edit));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onDeleteCallback(int position) {
        Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), workoutItemList.get(position).getName()), Toast.LENGTH_SHORT).show();
        OpenWorkout.getInstance().deleteWorkoutItem(workoutItemList.get(position));
        workoutItemList.remove(position);
    }

    @Override
    protected void onDuplicateCallback(int position) {
        WorkoutItem origWorkoutItem = workoutItemList.get(position);
        WorkoutItem duplicatedWorkoutItem = origWorkoutItem.clone();

        duplicatedWorkoutItem.setWorkoutItemId(0);
        workoutItemList.add(position, duplicatedWorkoutItem);
        saveToDatabase();

        long workoutItemId = OpenWorkout.getInstance().insertWorkoutItem(duplicatedWorkoutItem);
        duplicatedWorkoutItem.setWorkoutItemId(workoutItemId);
    }

    @Override
    protected void onResetClick() {
        for (WorkoutItem workoutItem : workoutItemList) {
            workoutItem.setFinished(false);
            OpenWorkout.getInstance().updateWorkoutItem(workoutItem);
        }
    }
}
