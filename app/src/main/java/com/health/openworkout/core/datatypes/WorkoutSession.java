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

import androidx.annotation.Keep;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Keep
@Entity
public class WorkoutSession implements Comparable<WorkoutSession>, Cloneable {
    @PrimaryKey(autoGenerate = true)
    private long workoutSessionId;

    @ColumnInfo
    private long trainingPlanId;
    @ColumnInfo
    private long orderNr;
    @ColumnInfo
    public String name;
    @ColumnInfo
    private boolean finished;
    @Ignore
    private List<WorkoutItem> workoutItems;

    public WorkoutSession() {
        orderNr = -1L;
        workoutItems = new ArrayList<>();
        finished = false;
    }

    @Override
    public WorkoutSession clone() {
        WorkoutSession clone;
        try {
            clone = (WorkoutSession) super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("failed to clone WorkoutSession", e);
        }

        for (WorkoutItem workoutItem : clone.workoutItems) {
            workoutItem.setWorkoutItemId(0);
        }

        return clone;
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

    public long getOrderNr() {
        return orderNr;
    }

    public void setOrderNr(long orderNr) {
        this.orderNr = orderNr;
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

    public WorkoutItem getNextWorkoutItem(long workoutItemOrderNr) {
        // Run two iterations. In the first one check only future workoutItems. In the second one, check also workoutItems at the beginning.
        for (WorkoutItem workoutItem : workoutItems) {
            if (!workoutItem.isFinished() && workoutItem.getOrderNr() > workoutItemOrderNr) {
                return workoutItem;
            }
        }
        for (WorkoutItem workoutItem : workoutItems) {
            if (!workoutItem.isFinished()) {
                return workoutItem;
            }
        }

        return null;
    }

    public long getElapsedSessionTime() {
        long elapsedSessionTime = 0;

        for (WorkoutItem workoutItem : workoutItems) {
            elapsedSessionTime += workoutItem.getElapsedTime();
        }

        return elapsedSessionTime;
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
    }

    @Override
    public int compareTo(WorkoutSession o) {
        if (this.orderNr == -1L || o.orderNr == -1L) {
            return (int)(this.workoutSessionId - o.workoutSessionId);
        }

        return (int)(this.orderNr - o.orderNr);
    }
}
