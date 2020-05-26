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
