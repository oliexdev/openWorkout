/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alphamovie.lib.AlphaMovieView;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class WorkoutSettingsFragment extends GenericSettingsFragment {
    private WorkoutItem workoutItem;

    private ImageView imgView;
    private TextView nameView;
    private TextView descriptionView;
    private TextView prepTimeView;
    private TextView workoutTimeView;
    private TextView breakTimeView;
    private TextView repetitionCountView;
    private Switch timeModeView;
    private TableRow workoutTimeRow;
    private TableRow repetitionCountRow;
    private AlphaMovieView videoView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workoutsettings, container, false);

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);
        descriptionView = root.findViewById(R.id.descriptionView);
        prepTimeView = root.findViewById(R.id.prepTimeView);
        workoutTimeView = root.findViewById(R.id.workoutTimeView);
        breakTimeView = root.findViewById(R.id.breakTimeView);
        repetitionCountView = root.findViewById(R.id.repetitionCountView);
        timeModeView = root.findViewById(R.id.timeModeView);
        workoutTimeRow = root.findViewById(R.id.workoutTimeRow);
        repetitionCountRow = root.findViewById(R.id.repetitionCoundRow);
        videoView = root.findViewById(R.id.videoView);

        timeModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workoutItem.setTimeMode(isChecked);
                refreshTimeModeState();
            }
        });

        setMode(WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getMode());

        return root;
    }

    @Override
    protected String getTitle() {
        return workoutItem.getName();
    }

    @Override
    protected void loadFromDatabase(SETTING_MODE mode) {
        switch (mode) {
            case ADD:
                workoutItem = new WorkoutItem();
                break;
            case EDIT:
                long workoutItemId = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getWorkoutItemId();

                workoutItem = OpenWorkout.getInstance().getWorkoutItem(workoutItemId);
                break;
        }

        try {
            String subFolder;
            if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                subFolder = "male";
            } else {
                subFolder = "female";
            }

            InputStream ims = getContext().getAssets().open("image/" + subFolder + "/" + workoutItem.getImagePath());
            imgView.setImageDrawable(Drawable.createFromStream(ims, null));

            ims.close();
        }
        catch(IOException ex) {
            Timber.e(ex);
        }

        if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
            videoView.setVideoFromAssets("video/male/" + workoutItem.getVideoPath());
        } else {
            videoView.setVideoFromAssets("video/female/" + workoutItem.getVideoPath());
        }
        videoView.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.pause();
            }
        },100);

        nameView.setText(workoutItem.getName());
        descriptionView.setText(workoutItem.getDescription());
        prepTimeView.setText(Integer.toString(workoutItem.getPrepTime()));
        workoutTimeView.setText(Integer.toString(workoutItem.getWorkoutTime()));
        breakTimeView.setText(Integer.toString(workoutItem.getBreakTime()));
        repetitionCountView.setText(Integer.toString(workoutItem.getRepetitionCount()));
        timeModeView.setChecked(workoutItem.isTimeMode());

        refreshTimeModeState();
    }

    private void refreshTimeModeState() {
        if (workoutItem.isTimeMode()) {
            workoutTimeRow.setVisibility(View.VISIBLE);
            repetitionCountRow.setVisibility(View.GONE);
        } else {
            workoutTimeRow.setVisibility(View.GONE);
            repetitionCountRow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void saveToDatabase(SETTING_MODE mode) {
        workoutItem.setName(nameView.getText().toString());
        workoutItem.setDescription(descriptionView.getText().toString());
        workoutItem.setPrepTime(Integer.valueOf(prepTimeView.getText().toString()));
        workoutItem.setWorkoutTime(Integer.valueOf(workoutTimeView.getText().toString()));
        workoutItem.setBreakTime(Integer.valueOf(breakTimeView.getText().toString()));
        workoutItem.setTimeMode(timeModeView.isChecked());

        switch (mode) {
            case ADD:
                long workoutSessionId = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();

                workoutItem.setWorkoutSessionId(workoutSessionId);
                OpenWorkout.getInstance().insertWorkoutItem(workoutItem);
                break;
            case EDIT:
                OpenWorkout.getInstance().updateWorkoutItem(workoutItem);
                break;
        }
    }
}
