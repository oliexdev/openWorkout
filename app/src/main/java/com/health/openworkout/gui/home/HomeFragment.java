/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutSession;

public class HomeFragment extends Fragment {
    private Button startView;
    private ImageView detailTrainingView;
    private Spinner trainingNameView;
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
                userTrainingPlan = openWorkout.getTrainingPlan(user.getTrainingsPlanId());
                WorkoutSession nextUserWorkoutSession = userTrainingPlan.getNextWorkoutSession();
                HomeFragmentDirections.ActionHomeFragmentToWorkoutFragmentSlide action = HomeFragmentDirections.actionHomeFragmentToWorkoutFragmentSlide();
                action.setTitle(nextUserWorkoutSession.getName());
                action.setSessionWorkoutId(nextUserWorkoutSession.getWorkoutSessionId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        detailTrainingView = root.findViewById(R.id.detailTrainingView);
        sessionProgressBar = root.findViewById(R.id.sessionProgressBar);
        sessionView = root.findViewById(R.id.sessionView);
        trainingNameView = root.findViewById(R.id.trainingNameView);

        user = openWorkout.getCurrentUser();
        userTrainingPlan = openWorkout.getTrainingPlan(user.getTrainingsPlanId());

        //  was user training plan deleted then get a new one
        if (userTrainingPlan == null) {
            userTrainingPlan = openWorkout.getTrainingPlans().get(0);
        }

        final ArrayAdapter<TrainingPlan> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, openWorkout.getTrainingPlans());

        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        trainingNameView.setAdapter(spinnerArrayAdapter);
        trainingNameView.setSelection(0,false);

        trainingNameView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TrainingPlan selectedTrainingPlan = spinnerArrayAdapter.getItem(position);

                user.setTrainingsPlanId(selectedTrainingPlan.getTrainingPlanId());
                openWorkout.updateUser(user);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int i=0; i<spinnerArrayAdapter.getCount(); i++) {
            if (userTrainingPlan.getTrainingPlanId() == spinnerArrayAdapter.getItem(i).getTrainingPlanId()) {
                trainingNameView.setSelection(i);
            }
        }

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

        detailTrainingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToTrainingFragment();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        return root;
    }
}
