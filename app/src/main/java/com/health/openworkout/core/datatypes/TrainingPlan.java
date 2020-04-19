/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.datatypes;

import android.content.Context;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.health.openworkout.core.OpenWorkout;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TrainingPlan {
    @PrimaryKey(autoGenerate = true)
    private long trainingPlanId;

    @ColumnInfo
    private String name;

    @ColumnInfo
    private String imagePath;

    @ColumnInfo
    private int countFinishedTraining;

    @Ignore
    private List<WorkoutSession> workoutSessions;

    @Ignore
    private final Context context;

    public TrainingPlan() {
        context = OpenWorkout.getInstance().getContext();

        countFinishedTraining = 0;
        workoutSessions = new ArrayList<>();
    }

    public Context getContext() {
        return context;
    }

    public void addWorkoutSession(WorkoutSession workoutSession) {
        workoutSessions.add(workoutSession);
    }

    public void setWorkoutSessions(List<WorkoutSession> workoutSessions) {
        this.workoutSessions = workoutSessions;
    }

    public List<WorkoutSession> getWorkoutSessions() {
        return workoutSessions;
    }

    public WorkoutSession getNextWorkoutSession() {
        for (WorkoutSession workoutSession : workoutSessions) {
            if (!workoutSession.isFinished()) {
                return workoutSession;
            }
        }

        return null;
    }

    public int finishedSessionSize() {
        int finishedSize = 0;

        for (WorkoutSession workoutSession : workoutSessions) {
            if (workoutSession.isFinished()) {
                finishedSize++;
            }
        }

        return finishedSize;
    }

    public void resetFinishedSessions() {
        for (WorkoutSession workoutSession : workoutSessions) {
            workoutSession.setFinished(false);
        }
    }

    public int getWorkoutSessionSize() {
        return workoutSessions.size();
    }

    public void setTrainingPlanId(long trainingPlanId) {
        this.trainingPlanId = trainingPlanId;
    }

    public long getTrainingPlanId() {
        return trainingPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCountFinishedTraining() {
        return countFinishedTraining;
    }

    public void setCountFinishedTraining(int countFinishedTraining) {
        this.countFinishedTraining = countFinishedTraining;
    }
}
