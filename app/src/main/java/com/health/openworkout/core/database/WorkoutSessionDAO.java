/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.health.openworkout.core.datatypes.WorkoutSession;

import java.util.List;

@Dao
public interface WorkoutSessionDAO {
    @Insert
    long insert(WorkoutSession workoutSession);

    @Update
    void update(WorkoutSession workoutSession);

    @Delete
    void delete(WorkoutSession workoutSession);

    @Query("DELETE FROM WorkoutSession WHERE trainingPlanId = :trainingPlanId")
    void deleteAll(long trainingPlanId);

    @Query("SELECT * FROM WorkoutSession WHERE workoutSessionId=:workoutSessionId")
    WorkoutSession get(long workoutSessionId);

    @Query("SELECT * FROM WorkoutSession WHERE trainingPlanId = :trainingPlanId ORDER BY orderNr")
    List<WorkoutSession> getAll(long trainingPlanId);
}
