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

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;
import com.health.openworkout.gui.utils.FileDialogHelper;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

public class TrainingSettingsFragment extends GenericSettingsFragment {
    private TrainingPlan trainingPlan;

    private ImageView imgView;
    private TextView nameView;

    private FileDialogHelper fileDialogHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trainingsettings, container, false);

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);

        fileDialogHelper = new FileDialogHelper(this);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialogHelper.openImageFileDialog();
            }
        });

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
            if (trainingPlan.isImagePathExternal()) {
                Uri imgUri = Uri.parse(trainingPlan.getImagePath());
                imgView.setImageURI(imgUri);
            } else {
                InputStream ims = getContext().getAssets().open("image/" + trainingPlan.getImagePath());
                imgView.setImageDrawable(Drawable.createFromStream(ims, null));

                ims.close();
            }
        } catch (IOException ex) {
            Timber.e(ex);
        }
          catch (SecurityException ex) {
            imgView.setImageResource(R.drawable.ic_no_file);
            Toast.makeText(getContext(), getContext().getString(R.string.error_no_access_to_file) + " " + trainingPlan.getImagePath(), Toast.LENGTH_SHORT).show();
            Timber.e(ex);
        }

        nameView.setText(trainingPlan.getName());
    }

    @Override
    protected boolean saveToDatabase(SETTING_MODE mode) {
        trainingPlan.setName(nameView.getText().toString());

        switch (mode) {
            case ADD:
                OpenWorkout.getInstance().insertTrainingPlan(trainingPlan);
                break;
            case EDIT:
                OpenWorkout.getInstance().updateTrainingPlan(trainingPlan);
                break;
        }

        return true;
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
            String imgPath = uri.toString();
            imgView.setImageURI(uri);
            trainingPlan.setImagePath(imgPath);
            trainingPlan.setImagePathExternal(true);
        }
    }
}
