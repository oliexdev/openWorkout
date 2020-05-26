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

package com.health.openworkout.core.datatypes;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;

import java.util.ArrayList;
import java.util.List;

@Keep
@Entity
public class TrainingPlan implements Comparable<TrainingPlan>, Cloneable {
    @PrimaryKey(autoGenerate = true)
    private long trainingPlanId;

    @ColumnInfo
    private long orderNr;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String imagePath;
    @ColumnInfo
    private boolean isImagePathExternal;
    @ColumnInfo
    private int countFinishedTraining;
    @Ignore
    private List<WorkoutSession> workoutSessions;
    @Ignore
    private transient final Context context;

    public TrainingPlan() {
        context = OpenWorkout.getInstance().getContext();

        orderNr = -1L;
        countFinishedTraining = 0;
        workoutSessions = new ArrayList<>();
        isImagePathExternal = false;
        name = "<" + context.getString(R.string.label_new_training_plan) + ">";
        imagePath = "defaultTraining.png";
    }

    @Override
    public TrainingPlan clone() {
        TrainingPlan clone;
        try {
            clone = (TrainingPlan) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("failed to clone TrainingPlan", e);
        }

        for (WorkoutSession workoutSession : clone.workoutSessions) {
            workoutSession.setWorkoutSessionId(0);

            for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                workoutItem.setWorkoutItemId(0);
            }
        }

        return clone;
    }

    public long getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(long orderNr) {
        this.orderNr = orderNr;
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

    public boolean isImagePathExternal() {
        return isImagePathExternal;
    }

    public void setImagePathExternal(boolean imagePathExternal) {
        isImagePathExternal = imagePathExternal;
    }

    public int getCountFinishedTraining() {
        return countFinishedTraining;
    }

    public void setCountFinishedTraining(int countFinishedTraining) {
        this.countFinishedTraining = countFinishedTraining;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(TrainingPlan o) {
        if (this.orderNr == -1L || o.orderNr == -1L) {
            return (int)(this.trainingPlanId - o.trainingPlanId);
        }

        return (int)(this.orderNr - o.orderNr);
    }
}
