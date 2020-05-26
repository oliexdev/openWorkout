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
