/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.util.Collections;
import java.util.List;

public class WorkoutFragment extends Fragment {
    private RecyclerView workoutsView;
    private WorkoutSession workoutSession;
    private List<WorkoutItem> workoutItemList;

    private WorkoutsAdapter workoutsAdapter;
    private ItemTouchHelper touchHelper;

    private static GenericAdapter.FRAGMENT_MODE mode = GenericAdapter.FRAGMENT_MODE.VIEW;
    private MenuItem saveMenu;
    private MenuItem editMenu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workout, container, false);
        setHasOptionsMenu(true);

        workoutsView = root.findViewById(R.id.workoutsView);

        workoutsView.setHasFixedSize(true);
        workoutsView.setLayoutManager(new LinearLayoutManager(getContext()));

        touchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.ACTION_STATE_IDLE) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(workoutItemList, from, to);
                workoutsAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });

        loadFromDatabase();

        return root;
    }

    private void loadFromDatabase() {
        final long workoutSessionId = WorkoutFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();
        workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);

        workoutItemList = workoutSession.getWorkoutItems();

        workoutsAdapter = new WorkoutsAdapter(getContext(), workoutItemList, mode);
        workoutsView.setAdapter(workoutsAdapter);

        if (mode == GenericAdapter.FRAGMENT_MODE.VIEW) {
            touchHelper.attachToRecyclerView(null);

            workoutsAdapter.setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    WorkoutItem workoutItem = workoutItemList.get(position);

                    WorkoutFragmentDirections.ActionWorkoutFragmentToWorkoutSlideFragment action = WorkoutFragmentDirections.actionWorkoutFragmentToWorkoutSlideFragment();
                    action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                    action.setWorkoutItemId(workoutItem.getWorkoutItemId());
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });
        }

        if (mode == GenericAdapter.FRAGMENT_MODE.EDIT) {
            workoutsAdapter.setOnItemClickListener(null);

            touchHelper.attachToRecyclerView(workoutsView);

            workoutsAdapter.setOnItemEditClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    WorkoutItem workoutItem = workoutItemList.get(position);

                    WorkoutFragmentDirections.ActionWorkoutFramgentToWorkoutSettingsFragment action = WorkoutFragmentDirections.actionWorkoutFramgentToWorkoutSettingsFragment();
                    action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                    action.setWorkoutItemId(workoutItem.getWorkoutItemId());
                    action.setMode(WorkoutSettingsFragment.WORKOUT_MODE.EDIT);
                    action.setTitle(getString(R.string.label_edit));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });

            workoutsAdapter.setOnItemDeleteClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), workoutItemList.get(position).getName()), Toast.LENGTH_SHORT).show();
                    workoutItemList.remove(position);

                    workoutsAdapter.notifyItemRemoved(position);
                }
            });

            workoutsAdapter.setOnItemReorderClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    touchHelper.startDrag(workoutsView.findViewHolderForLayoutPosition(position));
                }
            });
        }
    }

    private void saveToDatabase() {
        WorkoutSession databaseWorkoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSession.getWorkoutSessionId());
        OpenWorkout.getInstance().deleteWorkoutSession(databaseWorkoutSession);

        // set workoutItem to zero to generate a new workoutItemId in the database needed for correctly reordering because the order is based on the workoutItemId otherwise the same workingItemId is again inserted
        for (WorkoutItem workoutItem : workoutItemList) {
            workoutItem.setWorkoutItemId(0);
        }

        OpenWorkout.getInstance().insertWorkoutSession(workoutSession);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        saveMenu = menu.findItem(R.id.save);
        editMenu = menu.findItem(R.id.edit);

        refreshMenuVisibility();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshMenuVisibility() {
        switch (mode) {
            case VIEW:
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                break;
            case EDIT:
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                WorkoutFragmentDirections.ActionWorkoutFramgentToWorkoutSettingsFragment action = WorkoutFragmentDirections.actionWorkoutFramgentToWorkoutSettingsFragment();
                action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                action.setMode(WorkoutSettingsFragment.WORKOUT_MODE.ADD);
                action.setTitle(getString(R.string.label_add));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                return true;
            case R.id.edit:
                mode = GenericAdapter.FRAGMENT_MODE.EDIT;
                refreshMenuVisibility();
                loadFromDatabase();
                return true;
            case R.id.save:
                mode = GenericAdapter.FRAGMENT_MODE.VIEW;
                refreshMenuVisibility();
                saveToDatabase();
                loadFromDatabase();
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), workoutSession.getName()), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), workoutSession.getName()), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
