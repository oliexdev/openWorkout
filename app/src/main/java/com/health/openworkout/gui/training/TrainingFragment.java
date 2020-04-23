/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

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
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.util.Collections;
import java.util.List;

public class TrainingFragment extends Fragment {
    private RecyclerView trainingsView;

    private List<TrainingPlan> trainingPlanList;

    private TrainingsAdapter trainingsAdapter;
    private ItemTouchHelper touchHelper;

    private static GenericAdapter.FRAGMENT_MODE mode = GenericAdapter.FRAGMENT_MODE.VIEW;
    private MenuItem saveMenu;
    private MenuItem editMenu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);
        setHasOptionsMenu(true);

        trainingsView = root.findViewById(R.id.trainingsView);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        touchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP, ItemTouchHelper.ACTION_STATE_IDLE) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(trainingPlanList, from, to);
                trainingsAdapter.notifyItemMoved(from, to);
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
        trainingPlanList = OpenWorkout.getInstance().getTrainingPlans();

        trainingsAdapter = new TrainingsAdapter(getContext(), trainingPlanList, mode);
        trainingsView.setAdapter(trainingsAdapter);

        if (mode == GenericAdapter.FRAGMENT_MODE.VIEW) {
            touchHelper.attachToRecyclerView(null);

            trainingsAdapter.setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    TrainingPlan trainingPlan = trainingPlanList.get(position);

                    TrainingFragmentDirections.ActionTrainingFragmentToSessionFragment action = TrainingFragmentDirections.actionTrainingFragmentToSessionFragment();
                    action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });
        }

        if (mode == GenericAdapter.FRAGMENT_MODE.EDIT) {
            trainingsAdapter.setOnItemClickListener(null);

            touchHelper.attachToRecyclerView(trainingsView);

            trainingsAdapter.setOnItemEditClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    TrainingPlan trainingPlan = trainingPlanList.get(position);

                    TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
                    action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
                    action.setMode(TrainingSettingsFragment.TRAINING_MODE.EDIT);
                    action.setTitle(getString(R.string.label_edit));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });

            trainingsAdapter.setOnItemDeleteClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), trainingPlanList.get(position).getName()), Toast.LENGTH_SHORT).show();
                    trainingPlanList.remove(position);

                    trainingsAdapter.notifyItemRemoved(position);
                }
            });

            trainingsAdapter.setOnItemReorderClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    touchHelper.startDrag(trainingsView.findViewHolderForLayoutPosition(position));
                }
            });
        }
    }

    private void saveToDatabase() {
        User user = OpenWorkout.getInstance().getCurrentUser();
        long userTrainingPlanId = user.getTrainingsPlanId();

        List<TrainingPlan> databaseTrainingPlanList = OpenWorkout.getInstance().getTrainingPlans();

        for (TrainingPlan trainingPlan : databaseTrainingPlanList) {
            OpenWorkout.getInstance().deleteTrainingPlan(trainingPlan);
        }

        for (TrainingPlan trainingPlan : trainingPlanList) {
            // update user training plan id with reordered training plan ids
            if (userTrainingPlanId == trainingPlan.getTrainingPlanId()) {
                trainingPlan.setTrainingPlanId(0);
                long newUserTrainingPlanId = OpenWorkout.getInstance().insertTrainingPlan(trainingPlan);
                user.setTrainingsPlanId(newUserTrainingPlanId);
                OpenWorkout.getInstance().updateUser(user);
            } else {
                trainingPlan.setTrainingPlanId(0);
                OpenWorkout.getInstance().insertTrainingPlan(trainingPlan);
            }
        }
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
                TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
                action.setMode(TrainingSettingsFragment.TRAINING_MODE.ADD);
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
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), getString(R.string.label_training_plans)), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), getString(R.string.label_training_plans)), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
