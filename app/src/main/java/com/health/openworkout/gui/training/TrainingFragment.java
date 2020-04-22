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

import java.util.List;

public class TrainingFragment extends Fragment {
    @Keep
    public enum TRAINING_MODE {SELECT, EDIT}

    private RecyclerView trainingsView;
    private TRAINING_MODE mode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        trainingsView = root.findViewById(R.id.trainingsView);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

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

                TrainingFragmentDirections.ActionTrainingFragmentToSessionFragment action = TrainingFragmentDirections.actionTrainingFragmentToSessionFragment();
                action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });
    }
}
