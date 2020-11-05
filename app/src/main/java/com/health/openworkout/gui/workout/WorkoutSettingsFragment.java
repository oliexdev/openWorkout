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

package com.health.openworkout.gui.workout;

import android.content.Intent;
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
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;
import com.health.openworkout.gui.utils.FileDialogHelper;

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
    private Switch videoModeView;
    private TableRow videoCardRow;
    private CardView videoCardView;
    private VideoView videoView;

    private FileDialogHelper fileDialogHelper;
    private boolean isImageDialogRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_workoutsettings, container, false);

        fileDialogHelper = new FileDialogHelper(this);

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);
        descriptionView = root.findViewById(R.id.descriptionView);
        prepTimeView = root.findViewById(R.id.prepTimeView);
        workoutTimeView = root.findViewById(R.id.workoutTimeView);
        breakTimeView = root.findViewById(R.id.breakTimeView);
        repetitionCountView = root.findViewById(R.id.repetitionCountView);
        timeModeView = root.findViewById(R.id.timeModeView);
        workoutTimeRow = root.findViewById(R.id.workoutTimeRow);
        repetitionCountRow = root.findViewById(R.id.repetitionCountRow);
        videoModeView = root.findViewById(R.id.videoModeView);
        videoCardRow = root.findViewById(R.id.videoCardRow);
        videoCardView = root.findViewById(R.id.videoCardView);
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

        videoModeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workoutItem.setVideoMode(isChecked);
                refreshVideoModeState();
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageDialogRequest = true;
                fileDialogHelper.openImageFileDialog();
            }
        });

        // support for SDK version <= 23 videoView onClickListener is not called
        videoCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isImageDialogRequest = false;
                        fileDialogHelper.openVideoFileDialog();
                    }
                });

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isImageDialogRequest = false;
                fileDialogHelper.openVideoFileDialog();
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

        try {
            if (workoutItem.isImagePathExternal()) {
                imgView.setImageURI(Uri.parse(workoutItem.getImagePath()));
            } else {
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
        } catch (IOException ex) {
            Timber.e(ex);
        } catch (SecurityException ex) {
            imgView.setImageResource(R.drawable.ic_no_file);
            Toast.makeText(getContext(), getContext().getString(R.string.error_no_access_to_file) + " " + workoutItem.getImagePath(), Toast.LENGTH_SHORT).show();
            Timber.e(ex);
        }

        try {
            if (workoutItem.isVideoPathExternal()) {
                videoView.setVideoURI(Uri.parse(workoutItem.getVideoPath()));
            } else {
                if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                    videoView.setVideoPath("content://com.health.openworkout.videoprovider/video/male/" + workoutItem.getVideoPath());
                } else {
                    videoView.setVideoPath("content://com.health.openworkout.videoprovider/video/female/" + workoutItem.getVideoPath());
                }
            }
        } catch (SecurityException ex) {
            videoView.setVideoURI(null);
            Toast.makeText(getContext(), getContext().getString(R.string.error_no_access_to_file) + " " + workoutItem.getVideoPath(), Toast.LENGTH_SHORT).show();
            Timber.e(ex);
        }

        videoView.start();

        nameView.setText(workoutItem.getName());
        descriptionView.setText(workoutItem.getDescription());
        prepTimeView.setText(Integer.toString(workoutItem.getPrepTime()));
        workoutTimeView.setText(Integer.toString(workoutItem.getWorkoutTime()));
        breakTimeView.setText(Integer.toString(workoutItem.getBreakTime()));
        repetitionCountView.setText(Integer.toString(workoutItem.getRepetitionCount()));
        videoModeView.setChecked(workoutItem.isVideoMode());
        timeModeView.setChecked(workoutItem.isTimeMode());

        refreshTimeModeState();
        refreshVideoModeState();
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

    private void refreshVideoModeState() {
        if (workoutItem.isVideoMode()) {
            videoCardRow.setVisibility(View.VISIBLE);
        } else {
            videoCardRow.setVisibility(View.GONE);
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

        workoutItem.setTimeMode(timeModeView.isChecked());
        workoutItem.setVideoMode(videoModeView.isChecked());

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        fileDialogHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fileDialogHelper.onActivityResult(requestCode, resultCode, data)) {
            Uri uri = data.getData();

            if (isImageDialogRequest) {
                imgView.setImageURI(uri);
                workoutItem.setImagePath(uri.toString());
                workoutItem.setImagePathExternal(true);
            } else {
                videoView.setVideoURI(uri);
                videoView.start();
                workoutItem.setVideoPath(uri.toString());
                workoutItem.setVideoPathExternal(true);
            }
        }
    }
}
