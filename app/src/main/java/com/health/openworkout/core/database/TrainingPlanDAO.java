/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.health.openworkout.core.datatypes.TrainingPlan;

import java.util.List;

@Dao
public interface TrainingPlanDAO {
    @Insert
    long insert(TrainingPlan trainingPlan);

    @Update
    void update(TrainingPlan trainingPlan);

    @Delete
    void delete(TrainingPlan trainingPlan);

    @Query("SELECT * FROM TrainingPlan WHERE trainingPlanId=:trainingPlanId")
    TrainingPlan get(long trainingPlanId);

    @Query("SELECT * FROM TrainingPlan")
    List<TrainingPlan> getAll();
}
