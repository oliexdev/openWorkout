/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.datatypes;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class WorkoutSession {
    @PrimaryKey(autoGenerate = true)
    private long workoutSessionId;

    @ColumnInfo
    private long trainingPlanId;

    @ColumnInfo
    public String name;

    @ColumnInfo
    private boolean finished;

    @Ignore
    private List<WorkoutItem> workoutItems;

    public WorkoutSession() {
        workoutItems = new ArrayList<>();
        finished = false;
    }

    public void setWorkoutSessionId(long workoutSessionId) {
        this.workoutSessionId = workoutSessionId;
    }

    public long getWorkoutSessionId() {
        return workoutSessionId;
    }

    public void setTrainingPlanId(long trainingPlanId) {
        this.trainingPlanId = trainingPlanId;
    }

    public long getTrainingPlanId() {
        return trainingPlanId;
    }

    public WorkoutItem addWorkout(WorkoutItem workoutItem) {
        workoutItems.add(workoutItem);

        return workoutItem;
    }

    public void setWorkoutItems(List<WorkoutItem> workoutItems) {
        this.workoutItems = workoutItems;
    }

    public List<WorkoutItem> getWorkoutItems() {
        return workoutItems;
    }

    public WorkoutItem getNextWorkoutItem() {
        for (WorkoutItem workoutItem : workoutItems) {
            if (!workoutItem.isFinished()) {
                return workoutItem;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;

        for (WorkoutItem workoutItem : workoutItems) {
            workoutItem.setFinished(finished);
        }
    }
}
