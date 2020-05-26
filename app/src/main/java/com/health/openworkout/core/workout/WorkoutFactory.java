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

package com.health.openworkout.core.workout;

import com.health.openworkout.core.datatypes.WorkoutItem;

import java.util.ArrayList;
import java.util.List;

public class WorkoutFactory {
    public List<WorkoutItem> getAllWorkoutItems() {
        List<WorkoutItem> workoutItemList = new ArrayList<>();

        workoutItemList.add(new AbdominalCrunch());
        workoutItemList.add(new AirSquatBentArms());
        workoutItemList.add(new BicycleCrunch());
        workoutItemList.add(new BoxJump());
        workoutItemList.add(new Burpee());
        workoutItemList.add(new CircleCrunch());
        workoutItemList.add(new CrossJumps());
        workoutItemList.add(new CrossJumpsRotation());
        workoutItemList.add(new HighKnees());
        workoutItemList.add(new JumpingJack());
        workoutItemList.add(new JumpPushUps());
        workoutItemList.add(new Lunge());
        workoutItemList.add(new PikeWalk());
        workoutItemList.add(new Plank());
        workoutItemList.add(new PushUpRotation());
        workoutItemList.add(new PushUps());
        workoutItemList.add(new QuickSteps());
        workoutItemList.add(new SidePlank());
        workoutItemList.add(new Squat());
        workoutItemList.add(new StepUp());
        workoutItemList.add(new TricepsDip());
        workoutItemList.add(new WallSit());
        workoutItemList.add(new DonkeyKick());
        workoutItemList.add(new LungeKick());
        workoutItemList.add(new MountainClimbers());
        workoutItemList.add(new RussianTwist());

        return workoutItemList;
    }
}
