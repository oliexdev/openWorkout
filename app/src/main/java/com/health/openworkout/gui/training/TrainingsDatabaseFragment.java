/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.training;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.GitHubFile;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.utils.PackageUtils;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;
import com.health.openworkout.gui.datatypes.GenericSettingsFragment;
import com.health.openworkout.gui.utils.FileDialogHelper;
import com.health.openworkout.gui.workout.WorkoutDatabaseFragmentDirections;
import com.health.openworkout.gui.workout.WorkoutFragmentArgs;
import com.health.openworkout.gui.workout.WorkoutsDatabaseAdapter;

import java.io.File;
import java.util.List;

import timber.log.Timber;

public class TrainingsDatabaseFragment extends Fragment {
    private RecyclerView trainingsView;
    private ProgressBar progressBar;
    private Button buttonView;

    private FileDialogHelper fileDialogHelper;

    private List<GitHubFile> gitHubFileList;
    private TrainingDatabaseAdapter trainingDatabaseAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_trainingdatabase, container, false);

        fileDialogHelper = new FileDialogHelper(this);

        trainingsView = root.findViewById(R.id.trainingsView);
        progressBar = root.findViewById(R.id.progressBar);
        buttonView = root.findViewById(R.id.buttonView);

        progressBar.setVisibility(View.VISIBLE);

        trainingsView.setHasFixedSize(true);
        trainingsView.setLayoutManager(new LinearLayoutManager(getContext()));

        buttonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileDialogHelper.openImportFileDialog();
            }
        });

        loadFromDatabase();

        return root;
    }

    protected void loadFromDatabase() {
        PackageUtils packageUtils = new PackageUtils(getContext());

        packageUtils.setOnGitHubCallbackListener(new PackageUtils.OnGitHubCallbackListener() {
            @Override
            public void onGitHubFileList(List<GitHubFile> receivedGitHubFileList) {
                gitHubFileList = receivedGitHubFileList;
                trainingDatabaseAdapter = new TrainingDatabaseAdapter(getContext(), gitHubFileList);

                trainingDatabaseAdapter.setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        GitHubFile gitHubFile = gitHubFileList.get(position);
                        packageUtils.downloadFile(gitHubFile);
                        trainingDatabaseAdapter.notifyItemChanged(position);
                    }
                });

                trainingsView.setAdapter(trainingDatabaseAdapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onGitHubDownloadFile(File filename) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TrainingPlan trainingPlan = packageUtils.importTrainingPlan(filename);

                        trainingDatabaseAdapter.downloadCompleted(trainingPlan);
                    }
                });
            }

            @Override
            public void onGitHubDownloadProgressUpdate(long bytesDownloaded, long bytesTotal) {
                trainingDatabaseAdapter.updateProgressBar(bytesDownloaded, bytesTotal);
                Timber.d("Download byte " + bytesDownloaded + " of " + bytesTotal);
            }

            @Override
            public void onGitHubFailure(Exception ex) {
                Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        packageUtils.getGitHubFiles();
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

            packageUtils.importTrainingPlan(uri);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
        }
    }
}
