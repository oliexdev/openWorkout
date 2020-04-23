/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

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
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;

import java.util.List;

public class TrainingFragment extends GenericFragment {
    private RecyclerView trainingsView;

    private List<TrainingPlan> trainingPlanList;

    private TrainingsAdapter trainingsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        trainingsView = root.findViewById(R.id.trainingsView);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFromDatabase();

        return root;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.label_training_plans);
    }

    @Override
    protected void loadFromDatabase() {
        trainingPlanList = OpenWorkout.getInstance().getTrainingPlans();

        trainingsAdapter = new TrainingsAdapter(getContext(), trainingPlanList, mode);
        trainingsView.setAdapter(trainingsAdapter);
    }

    @Override
    protected void saveToDatabase() {
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
    protected GenericAdapter getAdapter() {
        return trainingsAdapter;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return trainingsView;
    }

    @Override
    protected List getItemList() {
        return trainingPlanList;
    }

    @Override
    protected void onSelectClick(int position) {
        TrainingPlan trainingPlan = trainingPlanList.get(position);

        TrainingFragmentDirections.ActionTrainingFragmentToSessionFragment action = TrainingFragmentDirections.actionTrainingFragmentToSessionFragment();
        action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onEditClick(int position) {
        TrainingPlan trainingPlan = trainingPlanList.get(position);

        TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
        action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        action.setMode(TrainingSettingsFragment.TRAINING_MODE.EDIT);
        action.setTitle(getString(R.string.label_edit));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onDeleteClick(int position) {
        Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), trainingPlanList.get(position).getName()), Toast.LENGTH_SHORT).show();
        trainingPlanList.remove(position);
    }

    @Override
    protected void onAddClick() {
        TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
        action.setMode(TrainingSettingsFragment.TRAINING_MODE.ADD);
        action.setTitle(getString(R.string.label_add));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
