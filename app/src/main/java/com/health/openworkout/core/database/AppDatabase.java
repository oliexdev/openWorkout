/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.User;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;

@Database(entities = {User.class, TrainingPlan.class, WorkoutSession.class, WorkoutItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDAO userDAO();
    public abstract TrainingPlanDAO trainingPlanDAO();
    public abstract WorkoutSessionDAO workoutSessionDAO();
    public abstract WorkoutItemDAO workoutItemDAO();
}