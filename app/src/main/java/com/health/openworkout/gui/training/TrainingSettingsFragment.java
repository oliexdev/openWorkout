/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;

import java.io.IOException;
import java.io.InputStream;

import timber.log.Timber;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class TrainingSettingsFragment extends Fragment {
    @Keep
    enum TRAINING_MODE {VIEW, EDIT, ADD}

    private TRAINING_MODE mode;
    private TrainingPlan trainingPlan;

    private ImageView imgView;
    private TextView nameView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trainingsettings, container, false);
        setHasOptionsMenu(true);

        mode = TrainingSettingsFragmentArgs.fromBundle(getArguments()).getMode();
        long trainingPlanId = TrainingSettingsFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);

        switch (mode) {
            case ADD:
                trainingPlan = new TrainingPlan();
                break;
            case EDIT:
                trainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlanId);
                break;
        }

        loadFromDatabase();

        return root;
    }

    private void loadFromDatabase() {
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

    private void saveToDatabase() {
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
        // close keyboard
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        switch (item.getItemId()) {
            case R.id.save:
                saveToDatabase();
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), trainingPlan.getName()), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), trainingPlan.getName()), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
