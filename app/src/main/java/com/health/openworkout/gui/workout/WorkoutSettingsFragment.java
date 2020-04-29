/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;

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
    private VideoView videoView;

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

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        timeModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workoutItem.setTimeMode(isChecked);
                refreshTimeModeState();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageFileDialog();
            }
        });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoFileDialog();
            }
        });

        setMode(WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getMode());

        return root;
    }

    @Override
    protected void onNewImagePath(Uri uri) {
        imgView.setImageURI(uri);
    }

    @Override
    protected void onNewVideoPath(Uri uri) {
        videoView.setVideoURI(uri);
        videoView.start();
    }

    @Override
    protected String getTitle() {
        return workoutItem.getName();
    }

    @Override
    protected void loadFromDatabase(SETTING_MODE mode) {
        long workoutItemId = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getWorkoutItemId();

        switch (mode) {
            case ADD:
                if (workoutItemId != -1L) {
                    workoutItem = OpenWorkout.getInstance().getWorkoutItem(workoutItemId).clone();
                    workoutItem.setWorkoutItemId(0);
                } else {
                    workoutItem = new WorkoutItem();
                }
                break;
            case EDIT:
                workoutItem = OpenWorkout.getInstance().getWorkoutItem(workoutItemId);
                break;
        }

        if (workoutItem.isImagePathExternal()) {
            imgView.setImageURI(Uri.parse(workoutItem.getImagePath()));
        } else {
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
            } catch (IOException ex) {
                Timber.e(ex);
            }
        }

        if (workoutItem.isVideoPathExternal()) {
            videoView.setVideoURI(Uri.parse(workoutItem.getVideoPath()));
        } else {
            if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                videoView.setVideoPath("content://com.health.openworkout.videoprovider/video/male/" + workoutItem.getVideoPath());
            } else {
                videoView.setVideoPath("content://com.health.openworkout.videoprovider/video/female/" + workoutItem.getVideoPath());
            }
        }

        videoView.start();

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
    protected boolean saveToDatabase(SETTING_MODE mode) {
        boolean checkFormat = true;

        workoutItem.setName(nameView.getText().toString());
        workoutItem.setDescription(descriptionView.getText().toString());
        if (prepTimeView.getText().toString().isEmpty()) {
            prepTimeView.setError(getString(R.string.error_empty_text));
            checkFormat = false;
        } else {
            workoutItem.setPrepTime(Integer.valueOf(prepTimeView.getText().toString()));
        }
        if (workoutTimeView.getText().toString().isEmpty()) {
            workoutTimeView.setError(getString(R.string.error_empty_text));
            checkFormat = false;
        } else {
            workoutItem.setWorkoutTime(Integer.valueOf(workoutTimeView.getText().toString()));
        }
        if (breakTimeView.getText().toString().isEmpty()) {
            breakTimeView.setError(getString(R.string.error_empty_text));
            checkFormat = false;
        } else {
            workoutItem.setBreakTime(Integer.valueOf(breakTimeView.getText().toString()));
        }
        if (repetitionCountView.getText().toString().isEmpty()) {
            repetitionCountView.setError(getString(R.string.error_empty_text));
            checkFormat = false;
        } else {
            workoutItem.setRepetitionCount(Integer.valueOf(repetitionCountView.getText().toString()));
        }

        if (!getImagePath().isEmpty()) {
            workoutItem.setImagePath(getImagePath());
            workoutItem.setImagePathExternal(true);
        }

        if (!getVideoPath().isEmpty()) {
            workoutItem.setVideoPath(getVideoPath());
            workoutItem.setVideoPathExternal(true);
        }

        workoutItem.setTimeMode(timeModeView.isChecked());

        switch (mode) {
            case ADD:
                long workoutSessionId = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getSessionWorkoutId();

                workoutItem.setWorkoutSessionId(workoutSessionId);
                OpenWorkout.getInstance().insertWorkoutItem(workoutItem);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                break;
            case EDIT:
                OpenWorkout.getInstance().updateWorkoutItem(workoutItem);
                break;
        }

        return checkFormat;
    }
}
