/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

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
import com.health.openworkout.core.datatypes.WorkoutSession;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class SessionSettingsFragment extends Fragment {
    @Keep
    enum SESSION_MODE {VIEW, EDIT, ADD}

    private SESSION_MODE mode;
    private WorkoutSession workoutSession;

    private ImageView imgView;
    private TextView nameView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sessionsettings, container, false);
        setHasOptionsMenu(true);

        mode = SessionSettingsFragmentArgs.fromBundle(getArguments()).getMode();
        long workoutSessionId = SessionSettingsFragmentArgs.fromBundle(getArguments()).getWorkoutSessionId();

        imgView = root.findViewById(R.id.imgView);
        nameView = root.findViewById(R.id.nameView);

        switch (mode) {
            case ADD:
                workoutSession = new WorkoutSession();
                break;
            case EDIT:
                workoutSession = OpenWorkout.getInstance().getWorkoutSession(workoutSessionId);
                break;
        }

        loadFromDatabase();

        return root;
    }

    private void loadFromDatabase() {
        if (workoutSession.isFinished()) {
            imgView.setImageResource(R.drawable.ic_session_done);
        } else {
            imgView.setImageResource(R.drawable.ic_session_undone);
        }


        nameView.setText(workoutSession.getName());
    }

    private void saveToDatabase() {
        workoutSession.setName(nameView.getText().toString());

        switch (mode) {
            case ADD:
                long trainingPlanId = SessionSettingsFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();

                workoutSession.setTrainingPlanId(trainingPlanId);
                OpenWorkout.getInstance().insertWorkoutSession(workoutSession);
                break;
            case EDIT:
                OpenWorkout.getInstance().updateWorkoutSession(workoutSession);
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
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), workoutSession.getName()), Toast.LENGTH_SHORT).show();
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), workoutSession.getName()), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
