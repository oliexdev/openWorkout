/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.gui.training.TrainingFragment;

public class HomeFragment extends Fragment {
    private Button startView;
    private TableRow trainingRow;
    private TextView trainingNameView;
    private ProgressBar sessionProgressBar;
    private TextView sessionView;
    private RadioGroup avatarGroup;

    private OpenWorkout openWorkout;
    private User user;
    private TrainingPlan userTrainingPlan;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        openWorkout = OpenWorkout.getInstance();

        startView = root.findViewById(R.id.startView);
        startView.setText("\n" + getString(R.string.label_start));

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToWorkoutFragmentSlide action = HomeFragmentDirections.actionHomeFragmentToWorkoutFragmentSlide();
                action.setSessionWorkoutId(userTrainingPlan.getNextWorkoutSession().getWorkoutSessionId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        trainingRow = root.findViewById(R.id.trainingRow);

        trainingRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragmentDirections.ActionHomeFragmentToTrainingFragment action = HomeFragmentDirections.actionHomeFragmentToTrainingFragment();
                action.setFragmentMode(TrainingFragment.TRAINING_FRAGMENT_MODE.SELECT);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        sessionProgressBar = root.findViewById(R.id.sessionProgressBar);
        sessionView = root.findViewById(R.id.sessionView);
        trainingNameView = root.findViewById(R.id.trainingNameView);

        user = openWorkout.getCurrentUser();
        userTrainingPlan = openWorkout.getTrainingPlan(user.getTrainingsPlanId());

        trainingNameView.setText(userTrainingPlan.getName());

        sessionView.setText("(" + Integer.toString(userTrainingPlan.finishedSessionSize()) + "/" + userTrainingPlan.getWorkoutSessionSize()+")");
        sessionProgressBar.setMax(userTrainingPlan.getWorkoutSessionSize());
        sessionProgressBar.post(new Runnable() {
            @Override
            public void run() {
                sessionProgressBar.setProgress(userTrainingPlan.finishedSessionSize());
            }
        });

        avatarGroup = root.findViewById(R.id.avatarGroup);

        if (user.isMale()) {
            avatarGroup.check(R.id.radioMale);
        } else {
            avatarGroup.check(R.id.radioFemale);
        }

        avatarGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int checkedRadioId = group.getCheckedRadioButtonId();

                switch (checkedRadioId) {
                    case R.id.radioMale:
                        user.setMale(true);
                        break;
                    case R.id.radioFemale:
                        user.setMale(false);
                        break;
                }

                openWorkout.updateUser(user);
            }
        });

        return root;
    }
}
