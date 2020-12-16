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

package com.health.openworkout.gui.training;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.utils.PackageUtils;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;
import com.health.openworkout.gui.utils.FileDialogHelper;

import java.util.List;

public class TrainingFragment extends GenericFragment {
    private RecyclerView trainingsView;
    private FloatingActionButton expandableButton;
    private FloatingActionButton addButton;
    private FloatingActionButton cloudImportButton;
    private FloatingActionButton localImportButton;
    private LinearLayout addLayout, localImportLayout, cloudImportLayout;
    private Animation animFabOpen, animFabClose, animFabClock, animFabAntiClock;
    private boolean isExpandable;
    private List<TrainingPlan> trainingPlanList;
    private TrainingPlan exportTrainingPlan;

    private TrainingsAdapter trainingsAdapter;
    private FileDialogHelper fileDialogHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_training, container, false);

        fileDialogHelper = new FileDialogHelper(this);

        trainingsView = root.findViewById(R.id.trainingsView);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        isExpandable = false;

        expandableButton = root.findViewById(R.id.expandableButton);
        addButton = root.findViewById(R.id.addButton);
        addLayout = root.findViewById(R.id.addLayout);
        cloudImportButton = root.findViewById(R.id.cloudlImportButton);
        cloudImportLayout = root.findViewById(R.id.cloudImportLayout);
        localImportButton = root.findViewById(R.id.locallImportButton);
        localImportLayout = root.findViewById(R.id.localImportLayout);

        animFabClose = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close);
        animFabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open);
        animFabClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_clock);
        animFabAntiClock = AnimationUtils.loadAnimation(getContext(), R.anim.fab_rotate_anticlock);

        expandableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandable) {
                    addLayout.setVisibility(View.GONE);
                    cloudImportLayout.setVisibility(View.GONE);
                    localImportLayout.setVisibility(View.GONE);
                    addLayout.startAnimation(animFabClose);
                    cloudImportLayout.startAnimation(animFabClose);
                    localImportLayout.startAnimation(animFabClose);
                    expandableButton.startAnimation(animFabAntiClock);
                    isExpandable = false;
                } else {
                    addLayout.setVisibility(View.VISIBLE);
                    cloudImportLayout.setVisibility(View.VISIBLE);
                    localImportLayout.setVisibility(View.VISIBLE);
                    addLayout.startAnimation(animFabOpen);
                    cloudImportLayout.startAnimation(animFabOpen);
                    localImportLayout.startAnimation(animFabOpen);
                    expandableButton.startAnimation(animFabClock);
                    isExpandable = true;
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrainingFragmentDirections.ActionTrainingFragmentToTrainingSettingsFragment action = TrainingFragmentDirections.actionTrainingFragmentToTrainingSettingsFragment();
                action.setMode(GenericSettingsFragment.SETTING_MODE.ADD);
                action.setTitle(getString(R.string.label_add));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        cloudImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavDirections action = TrainingFragmentDirections.actionNavTrainingsFragmentToNavTrainingsDatabaseFragment();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        localImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialogHelper.openImportFileDialog();
            }
        });

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
    }

    @Override
    protected void onResetClick() {
        for (TrainingPlan trainingPlan : trainingPlanList) {
            trainingPlan.setCountFinishedTraining(0);

            for (WorkoutSession workoutSession : trainingPlan.getWorkoutSessions()) {
                workoutSession.setFinished(false);

                for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                    workoutItem.setFinished(false);
                    OpenWorkout.getInstance().updateWorkoutItem(workoutItem);
                }

                OpenWorkout.getInstance().updateWorkoutSession(workoutSession);
            }

            OpenWorkout.getInstance().updateTrainingPlan(trainingPlan);
        }
    }

    @Override
    protected void onPublishClick(int position) {
        exportTrainingPlan = trainingPlanList.get(position);

        final AlertDialog infoDialog = new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.ic_export)
                .setTitle(getString(R.string.label_publish) + " " + exportTrainingPlan.getName())
                .setMessage(Html.fromHtml(getString(R.string.label_publish_message)))
                //.setMessage(publishMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fileDialogHelper.openExportFileDialog(exportTrainingPlan.getName());
                    }
                })
                .create();
        infoDialog.show();

        ((TextView)infoDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onExportClick(int position) {
        exportTrainingPlan = trainingPlanList.get(position);
        fileDialogHelper.openExportFileDialog(exportTrainingPlan.getName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        fileDialogHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fileDialogHelper.onActivityResult(requestCode, resultCode, data)) {
            Uri uri = data.getData();
            PackageUtils packageUtils = new PackageUtils(getContext());

            switch (requestCode) {
                case FileDialogHelper.REQUEST_IMPORT_FILE_DIALOG:
                    packageUtils.importTrainingPlan(uri);
                    break;
                case FileDialogHelper.REQUEST_EXPORT_FILE_DIALOG:
                    packageUtils.exportTrainingPlan(exportTrainingPlan, uri);
                    break;

            }


        }
    }
}
