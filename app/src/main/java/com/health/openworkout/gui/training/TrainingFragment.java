/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

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
import com.health.openworkout.core.datatypes.User;

import java.util.List;

public class TrainingFragment extends Fragment {
    public enum TRAINING_FRAGMENT_MODE {SELECT, EDIT}

    private GridView trainingsView;
    private TRAINING_FRAGMENT_MODE fragmentMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        trainingsView = root.findViewById(R.id.trainingsView);

        fragmentMode = TrainingFragmentArgs.fromBundle(getArguments()).getFragmentMode();

        init();

        return root;
    }

    public void init() {
        List<TrainingPlan> trainingPlanList = OpenWorkout.getInstance().getTrainingPlans();

        trainingsView.setAdapter(new TrainingsAdapter(getContext(), trainingPlanList));

        trainingsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                TrainingPlan trainingPlan = (TrainingPlan)trainingsView.getItemAtPosition(position);

                switch (fragmentMode) {
                    case SELECT:
                        onSelectModeClick(trainingPlan);
                        break;
                    case EDIT:
                        onEditModeClick(trainingPlan);
                        break;
                }
            }
        });
    }

    private void onSelectModeClick(TrainingPlan trainingPlan) {
        User user = OpenWorkout.getInstance().getCurrentUser();
        user.setTrainingsPlanId(trainingPlan.getTrainingPlanId());
        OpenWorkout.getInstance().updateUser(user);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
    }

    private void onEditModeClick(TrainingPlan trainingPlan) {
        TrainingFragmentDirections.ActionTrainingFragmentToSessionFragment action = TrainingFragmentDirections.actionTrainingFragmentToSessionFragment();
        action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
