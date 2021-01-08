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

package com.health.openworkout.gui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
    private ImageView startView;
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

        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userTrainingPlan = openWorkout.getTrainingPlan(user.getTrainingsPlanId());

                if (userTrainingPlan != null) {
                    WorkoutSession nextUserWorkoutSession = userTrainingPlan.getNextWorkoutSession();
                    if (nextUserWorkoutSession != null) {
                        if (!nextUserWorkoutSession.getWorkoutItems().isEmpty()) {
                            HomeFragmentDirections.ActionHomeFragmentToWorkoutFragmentSlide action = HomeFragmentDirections.actionHomeFragmentToWorkoutFragmentSlide();
                            action.setTitle(nextUserWorkoutSession.getName());
                            action.setSessionWorkoutId(nextUserWorkoutSession.getWorkoutSessionId());
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                        } else {
                            Toast.makeText(getContext(), String.format(getString(R.string.error_no_workout_items), nextUserWorkoutSession.getName()), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), String.format(getString(R.string.error_no_sessions), userTrainingPlan.getName()), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.error_no_trainings, Toast.LENGTH_SHORT).show();
                }
            }
        });

        detailTrainingView = root.findViewById(R.id.detailTrainingView);
        sessionProgressBar = root.findViewById(R.id.sessionProgressBar);
        sessionView = root.findViewById(R.id.sessionView);
        trainingNameView = root.findViewById(R.id.trainingNameView);

        user = openWorkout.getCurrentUser();
        userTrainingPlan = openWorkout.getTrainingPlan(user.getTrainingsPlanId());

        //  if user training plan was deleted
        if (userTrainingPlan == null) {
            // abort if all training plans were deleted
            if (openWorkout.getTrainingPlans().isEmpty()) {
                return root;
            } else {
                // get the first one in training plan list and update user training plan id if exist
                userTrainingPlan = openWorkout.getTrainingPlans().get(0);
                user.setTrainingsPlanId(userTrainingPlan.getTrainingPlanId());
                openWorkout.updateUser(user);
            }
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
                updateProgressBar(selectedTrainingPlan);
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

        updateProgressBar(userTrainingPlan);

        return root;
    }

    private void updateProgressBar(TrainingPlan trainingPlan) {
        sessionView.setText("(" + Integer.toString(trainingPlan.finishedSessionSize()) + "/" + trainingPlan.getWorkoutSessionSize()+")");
        sessionProgressBar.setMax(trainingPlan.getWorkoutSessionSize());
        sessionProgressBar.post(new Runnable() {
            @Override
            public void run() {
                sessionProgressBar.setProgress(trainingPlan.finishedSessionSize());
            }
        });
    }
}
