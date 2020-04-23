/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class TrainingSettingsFragment extends GenericSettingsFragment {
    private TrainingPlan trainingPlan;

    private ImageView imgView;
    private TextView nameView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trainingsettings, container, false);

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);

        setMode(TrainingSettingsFragmentArgs.fromBundle(getArguments()).getMode());

        return root;
    }

    @Override
    protected String getTitle() {
        return trainingPlan.getName();
    }

    @Override
    protected void loadFromDatabase(SETTING_MODE mode) {
        switch (mode) {
            case ADD:
                trainingPlan = new TrainingPlan();
                break;
            case EDIT:
                long trainingPlanId = TrainingSettingsFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();

                trainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlanId);
                break;
        }

        try {
            InputStream ims = getContext().getAssets().open("image/" + trainingPlan.getImagePath());
            imgView.setImageDrawable(Drawable.createFromStream(ims, null));

            ims.close();
        }
        catch(IOException ex) {
            Timber.e(ex);
        }

        nameView.setText(trainingPlan.getName());
    }

    @Override
    protected void saveToDatabase(SETTING_MODE mode) {
        trainingPlan.setName(nameView.getText().toString());

        switch (mode) {
            case ADD:
                OpenWorkout.getInstance().insertTrainingPlan(trainingPlan);
                break;
            case EDIT:
                OpenWorkout.getInstance().updateTrainingPlan(trainingPlan);
                break;
        }
    }
}
