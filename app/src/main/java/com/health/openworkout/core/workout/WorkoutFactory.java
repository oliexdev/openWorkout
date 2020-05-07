/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
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

        return workoutItemList;
    }
}
