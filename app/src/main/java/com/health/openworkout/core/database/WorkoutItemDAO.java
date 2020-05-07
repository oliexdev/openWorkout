/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.health.openworkout.core.datatypes.WorkoutItem;

import java.util.List;

@Dao
public interface WorkoutItemDAO {
    @Insert
    long insert(WorkoutItem workoutItem);

    @Insert
    void insertAll(List<WorkoutItem> workoutItemList);

    @Update
    void update(WorkoutItem workoutItem);

    @Delete
    void delete(WorkoutItem workoutItem);

    @Query("DELETE FROM WorkoutItem")
    void clear();

    @Query("DELETE FROM WorkoutItem WHERE workoutSessionId = :workoutSessionId")
    void deleteAll(long workoutSessionId);

    @Query("SELECT * FROM WorkoutItem WHERE workoutItemId=:workoutItemId")
    WorkoutItem get(long workoutItemId);

    @Query("SELECT * FROM WorkoutItem WHERE workoutSessionId = :workoutSessionId ORDER BY orderNr")
    List<WorkoutItem> getAll(long workoutSessionId);

    @Query("SELECT * FROM WorkoutItem GROUP BY name")
    List<WorkoutItem> getAllUnique();
}
