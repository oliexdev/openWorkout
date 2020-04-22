/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;

import java.util.List;

public class TrainingFragment extends Fragment {
    @Keep
    public enum TRAINING_FRAGMENT_MODE {SELECT, EDIT}

    private RecyclerView trainingsView;
    private TRAINING_FRAGMENT_MODE fragmentMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        trainingsView = root.findViewById(R.id.trainingsView);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        fragmentMode = TrainingFragmentArgs.fromBundle(getArguments()).getFragmentMode();

        init();

        return root;
    }

    public void init() {
        final List<TrainingPlan> trainingPlanList = OpenWorkout.getInstance().getTrainingPlans();

        TrainingsAdapter trainingsAdapter = new TrainingsAdapter(getContext(), trainingPlanList);
        trainingsView.setAdapter(trainingsAdapter);

        trainingsAdapter.setOnItemClickListener(new TrainingsAdapter.OnTrainingClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                TrainingPlan trainingPlan = trainingPlanList.get(position);

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
