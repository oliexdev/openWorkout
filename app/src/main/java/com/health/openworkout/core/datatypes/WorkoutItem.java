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

@Entity
public class WorkoutItem {
    @PrimaryKey(autoGenerate = true)
    private long workoutItemId;

    @ColumnInfo
    private long workoutSessionId;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private String description;
    @ColumnInfo
    private String imagePath;
    @ColumnInfo
    private boolean isImagePathExternal;
    @ColumnInfo
    private String videoPath;
    @ColumnInfo
    private boolean isVideoPathExternal;
    @ColumnInfo
    private int prepTime;
    @ColumnInfo
    private int workoutTime;
    @ColumnInfo
    private int breakTime;
    @ColumnInfo
    private int repetitionCount;
    @ColumnInfo
    private boolean isTimeMode;
    @ColumnInfo
    private boolean finished;

    @Ignore
    private final Context context;

    public WorkoutItem() {
        context = OpenWorkout.getInstance().getContext();
        prepTime = 5;
        workoutTime = 30;
        breakTime = 20;
        repetitionCount = 5;
        isTimeMode = true;
        finished = false;
    }

    public final Context getContext() {
        return context;
    }

    public void setWorkoutItemId(long workoutItemId) {
        this.workoutItemId = workoutItemId;
    }

    public long getWorkoutItemId() {
        return workoutItemId;
    }

    public void setWorkoutSessionId(long workoutSessionId) {
        this.workoutSessionId = workoutSessionId;
    }

    public long getWorkoutSessionId() {
        return workoutSessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setImagePath(int resId) {
        imagePath = context.getResources().getResourceEntryName(resId);
    }

    public boolean isImagePathExternal() {
        return isImagePathExternal;
    }

    public void setImagePathExternal(boolean imagePathExternal) {
        isImagePathExternal = imagePathExternal;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public boolean isVideoPathExternal() {
        return isVideoPathExternal;
    }

    public void setVideoPathExternal(boolean videoPathExternal) {
        isVideoPathExternal = videoPathExternal;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public int getWorkoutTime() {
        return workoutTime;
    }

    public void setWorkoutTime(int workoutTime) {
        this.workoutTime = workoutTime;
    }

    public int getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(int breakTime) {
        this.breakTime = breakTime;
    }

    public int getRepetitionCount() {
        return repetitionCount;
    }

    public void setRepetitionCount(int repetitionCount) {
        this.repetitionCount = repetitionCount;
    }

    public boolean isTimeMode() {
        return isTimeMode;
    }

    public void setTimeMode(boolean timeMode) {
        isTimeMode = timeMode;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
