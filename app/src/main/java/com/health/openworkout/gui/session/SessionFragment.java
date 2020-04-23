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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.gui.datatypes.GenericAdapter;

import java.util.Collections;
import java.util.List;

public class SessionFragment extends Fragment {
    private RecyclerView sessionsView;

    private TrainingPlan trainingPlan;
    private List<WorkoutSession> workoutSessionList;

    private SessionsAdapter sessionsAdapter;
    private ItemTouchHelper touchHelper;

    private static GenericAdapter.FRAGMENT_MODE mode = GenericAdapter.FRAGMENT_MODE.VIEW;
    private MenuItem saveMenu;
    private MenuItem editMenu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_session, container, false);

        setHasOptionsMenu(true);

        sessionsView = root.findViewById(R.id.sessionsView);

        sessionsView.setHasFixedSize(true);
        sessionsView.setLayoutManager(new GridLayoutManager(getContext(), getNumberOfColumns()));

        touchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, ItemTouchHelper.ACTION_STATE_IDLE) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(workoutSessionList, from, to);
                sessionsAdapter.notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });

        loadFromDatabase();

        return root;
    }

    private void loadFromDatabase() {
        long trainingPlanId = SessionFragmentArgs.fromBundle(getArguments()).getTrainingPlanId();
        trainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlanId);

        workoutSessionList = trainingPlan.getWorkoutSessions();

        sessionsAdapter = new SessionsAdapter(getContext(), workoutSessionList, mode);
        sessionsView.setAdapter(sessionsAdapter);

        if (mode == GenericAdapter.FRAGMENT_MODE.VIEW) {
            touchHelper.attachToRecyclerView(null);

            sessionsAdapter.setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    WorkoutSession workoutSession = workoutSessionList.get(position);

                    SessionFragmentDirections.ActionSessionFragmentToWorkoutFragment action = SessionFragmentDirections.actionSessionFragmentToWorkoutFragment();
                    action.setSessionWorkoutId(workoutSession.getWorkoutSessionId());
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });
        }

        if (mode == GenericAdapter.FRAGMENT_MODE.EDIT) {
            sessionsAdapter.setOnItemClickListener(null);

            touchHelper.attachToRecyclerView(sessionsView);

            sessionsAdapter.setOnItemEditClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    WorkoutSession workoutSession = workoutSessionList.get(position);

                    SessionFragmentDirections.ActionSessionsFragmentToSessionSettingsFragment action = SessionFragmentDirections.actionSessionsFragmentToSessionSettingsFragment();
                    action.setWorkoutSessionId(workoutSession.getWorkoutSessionId());
                    action.setMode(SessionSettingsFragment.SESSION_MODE.EDIT);
                    action.setTitle(getString(R.string.label_edit));
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                }
            });

            sessionsAdapter.setOnItemDeleteClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Toast.makeText(getContext(), String.format(getString(R.string.label_delete_toast), workoutSessionList.get(position).getName()), Toast.LENGTH_SHORT).show();
                    workoutSessionList.remove(position);

                    sessionsAdapter.notifyItemRemoved(position);
                }
            });

            sessionsAdapter.setOnItemReorderClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    touchHelper.startDrag(sessionsView.findViewHolderForLayoutPosition(position));
                }
            });
        }
    }

    private void saveToDatabase() {
        TrainingPlan databaseTrainingPlan = OpenWorkout.getInstance().getTrainingPlan(trainingPlan.getTrainingPlanId());
        OpenWorkout.getInstance().deleteTrainingPlan(databaseTrainingPlan);

        // set workoutSession to zero to generate a new workoutSessionId in the database needed for correctly reordering because the order is based on the workoutSessionId otherwise the same workoutSessionId is again inserted
        for (WorkoutSession workoutSession : workoutSessionList) {
            workoutSession.setWorkoutSessionId(0);
        }

        OpenWorkout.getInstance().insertTrainingPlan(trainingPlan);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        saveMenu = menu.findItem(R.id.save);
        editMenu = menu.findItem(R.id.edit);

        refreshMenuVisibility();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshMenuVisibility() {
        switch (mode) {
            case VIEW:
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                break;
            case EDIT:
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                SessionFragmentDirections.ActionSessionsFragmentToSessionSettingsFragment action = SessionFragmentDirections.actionSessionsFragmentToSessionSettingsFragment();
                action.setTrainingPlanId(trainingPlan.getTrainingPlanId());
                action.setMode(SessionSettingsFragment.SESSION_MODE.ADD);
                action.setTitle(getString(R.string.label_add));
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
                return true;
            case R.id.edit:
                mode = GenericAdapter.FRAGMENT_MODE.EDIT;
                refreshMenuVisibility();
                loadFromDatabase();
                return true;
            case R.id.save:
                mode = GenericAdapter.FRAGMENT_MODE.VIEW;
                refreshMenuVisibility();
                saveToDatabase();
                loadFromDatabase();
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), trainingPlan.getName()), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), trainingPlan.getName()), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getNumberOfColumns() {
        View view = View.inflate(getContext(), R.layout.item_session, null);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = view.getMeasuredWidth();
        int count = getResources().getDisplayMetrics().widthPixels / width;
        int remaining = getResources().getDisplayMetrics().widthPixels - width * count;
        if (remaining > width - 15)
            count++;
        return count;
    }
}
