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

package com.health.openworkout.gui.session;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

import java.util.List;

public class SessionFragment extends GenericFragment {
    private RecyclerView sessionsView;

    private TrainingPlan trainingPlan;
    private List<WorkoutSession> workoutSessionList;

    private SessionsAdapter sessionsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_session, container, false);

        sessionsView = root.findViewById(R.id.sessionsView);

        sessionsView.setHasFixedSize(true);
        sessionsView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));

        loadFromDatabase();

        return root;
    }

    @Override
    protected String getTitle() {
        return trainingPlan.getName();
    }

    @Override
    protected GenericAdapter getAdapter() {
        return sessionsAdapter;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return sessionsView;
    }

    @Override
    protected List getItemList() {
        return workoutSessionList;
    }

    @Override
    protected void onSelectCallback(int position) {
        WorkoutSession workoutSession = workoutSessionList.get(position);

        SessionFragmentDirections.ActionSessionFragmentToWorkoutFragment action = SessionFragmentDirections.actionSessionFragmentToWorkoutFragment();
        action.setTitle(workoutSession.getName());
        action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onEditCallback(int position) {
        WorkoutSession workoutSession = workoutSessionList.get(position);

        SessionFragmentDirections.ActionSessionsFragmentToSessionSettingsFragment action = SessionFragmentDirections.actionSessionsFragmentToSessionSettingsFragment();
        action.setWorkoutSessionId(workoutSession.getWorkoutSessionId());
        action.setMode(GenericSettingsFragment.SETTING_MODE.EDIT);
        action.setTitle(getString(R.string.label_edit));
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }

    @Override
    protected void onDeleteCallback(int position) {
        OpenWorkout.getInstance().deleteWorkoutSession(workoutSessionList.get(position));
        Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), workoutSessionList.get(position).getName()), Toast.LENGTH_SHORT).show();
        getItemList().remove(position);
    }

    @Override
    protected void onDuplicateCallback(int position) {
        WorkoutSession origWorkoutSession = workoutSessionList.get(position);
        WorkoutSession duplicateWorkoutSession = origWorkoutSession.clone();

        duplicateWorkoutSession.setWorkoutSessionId(0);
        workoutSessionList.add(position, duplicateWorkoutSession);
        saveToDatabase();

        long workoutSessionId = OpenWorkout.getInstance().insertWorkoutSession(duplicateWorkoutSession);
        duplicateWorkoutSession.setWorkoutSessionId(workoutSessionId);
    }

    @Override
    protected void onAddClick() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(getString(R.string.label_input_create_days));
        final EditText input = new EditText(getContext());
        input.setText("3", TextView.BufferType.EDITABLE);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().isEmpty()) {
                    int startNr = trainingPlan.getWorkoutSessions().size() + 1;
                    int offsetNr = Integer.valueOf(input.getText().toString());

                    for (int nr=startNr; nr < (startNr + offsetNr); nr++) {
                        WorkoutSession workoutSession = new WorkoutSession();
                        workoutSession.setName(String.format(getString(R.string.day_unit), nr));
                        workoutSession.setTrainingPlanId(trainingPlan.getTrainingPlanId());
                        workoutSession.setOrderNr(nr);
                        trainingPlan.addWorkoutSession(workoutSession);
                        OpenWorkout.getInstance().insertWorkoutSession(workoutSession);
                        getAdapter().notifyItemInserted(nr);
                        sessionsView.scrollToPosition(startNr);
                    }

                    loadFromDatabase();
                }
            }
        });
        alert.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // empty
            }
        });
        alert.show();
    }

    @Override
    protected void onResetClick() {
        for (WorkoutSession workoutSession : workoutSessionList) {
            workoutSession.setFinished(false);

            for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                workoutItem.setFinished(false);
                OpenWorkout.getInstance().updateWorkoutItem(workoutItem);
            }

            OpenWorkout.getInstance().updateWorkoutSession(workoutSession);
        }
    }

    @Override
    protected void loadFromDatabase() {
        long trainingPlanId = SessionFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();
        trainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlanId);

        workoutSessionList = trainingPlan.getWorkoutSessions();

        sessionsAdapter = new SessionsAdapter(getContext(), workoutSessionList);
        sessionsAdapter.setMode(getMode());
        sessionsView.setAdapter(sessionsAdapter);
    }

    @Override
    protected void saveToDatabase() {
        for (int i=0; i<workoutSessionList.size(); i++) {
            workoutSessionList.get(i).setOrderNr(i);
            OpenWorkout.getInstance().updateWorkoutSession(workoutSessionList.get(i));
        }
    }

    private int getNumberOfColumns() {
        View view = View.inflate(getContext(), R.layout.item_session, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = view.getMeasuredWidth();
        int count = (getResources().getDisplayMetrics().widthPixels - sessionsView.getPaddingLeft() - sessionsView.getPaddingRight()) / width;
        int remaining = (getResources().getDisplayMetrics().widthPixels - sessionsView.getPaddingLeft() - sessionsView.getPaddingRight()) - width * count;
        if (remaining > width - 15)
            count++;
        return count;
    }
}
