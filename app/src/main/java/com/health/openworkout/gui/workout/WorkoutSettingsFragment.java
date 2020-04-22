/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.alphamovie.lib.AlphaMovieView;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class WorkoutSettingsFragment extends Fragment {
    enum WORKOUT_MODE {VIEW, EDIT, ADD}

    private WORKOUT_MODE mode;
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
        setHasOptionsMenu(true);

        mode = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getMode();
        long workoutItemId = WorkoutSettingsFragmentArgs.fromBundle(getArguments()).getWorkoutItemId();

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

        switch (mode) {
            case ADD:
                workoutItem = new WorkoutItem();
                break;
            case EDIT:
                workoutItem = OpenWorkout.getInstance().getWorkoutItem(workoutItemId);
                break;
        }

        loadFromDatabase();

        return root;
    }

    private void loadFromDatabase() {
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

    private void saveToDatabase() {
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        MenuItem editMenu = menu.findItem(R.id.edit);
        editMenu.setVisible(false);

        MenuItem addMenu = menu.findItem(R.id.add);
        addMenu.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveToDatabase();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                return true;
            case R.id.reset:
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
