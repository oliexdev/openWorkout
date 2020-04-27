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
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

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

        trainingsAdapter = new TrainingsAdapter(getContext(), trainingPlanList);
        trainingsAdapter.setMode(getMode());
        trainingsView.setAdapter(trainingsAdapter);
    }

    @Override
    protected void saveToDatabase() {
        for (int i=0; i<trainingPlanList.size(); i++) {
            trainingPlanList.get(i).setOrderNr(i);
            OpenWorkout.getInstance().updateTrainingPlan(trainingPlanList.get(i));
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
    protected void onSelectCallback(int position) {
        TrainingPlan trainingPlan = trainingPlanList.get(position);

        TrainingFragmentDirections.ActionTrainingFragmentToSessionFragment action = TrainingFragmentDirections.actionTrainingFragmentToSessionFragment();
        action.setTitle(trainingPlan.getName());
        action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onEditCallback(int position) {
        TrainingPlan trainingPlan = trainingPlanList.get(position);

        TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
        action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        action.setMode(GenericSettingsFragment.SETTING_MODE.EDIT);
        action.setTitle(getString(R.string.label_edit));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onDeleteCallback(int position) {
        User user = OpenWorkout.getInstance().getCurrentUser();
        long userTrainingPlanId = user.getTrainingsPlanId();
        TrainingPlan trainingPlanToBeDelete = trainingPlanList.get(position);

        if (userTrainingPlanId == trainingPlanToBeDelete.getTrainingPlanId()) {
            user.setTrainingsPlanId(-1);
            OpenWorkout.getInstance().updateUser(user);
        }

        Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), trainingPlanToBeDelete.getName()), Toast.LENGTH_SHORT).show();
        OpenWorkout.getInstance().deleteTrainingPlan(trainingPlanList.get(position));
        trainingPlanList.remove(position);
    }

    @Override
    protected void onDuplicateCallback(int position) {
        TrainingPlan origTrainingPlan = trainingPlanList.get(position);
        TrainingPlan duplicateTrainingPlan = origTrainingPlan.clone();

        duplicateTrainingPlan.setTrainingPlanId(0);
        trainingPlanList.add(position, duplicateTrainingPlan);
        saveToDatabase();

        long trainingPlanId = OpenWorkout.getInstance().insertTrainingPlan(duplicateTrainingPlan);
        duplicateTrainingPlan.setTrainingPlanId(trainingPlanId);
        getAdapter().notifyItemInserted(position+1);
    }

    @Override
    protected void onAddClick() {
        TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
        action.setMode(GenericSettingsFragment.SETTING_MODE.ADD);
        action.setTitle(getString(R.string.label_add));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
