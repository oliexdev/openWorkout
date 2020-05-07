/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.session;

import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.workout.AbdominalCrunch;
import com.health.openworkout.core.workout.BicycleCrunch;
import com.health.openworkout.core.workout.Burpee;
import com.health.openworkout.core.workout.CircleCrunch;
import com.health.openworkout.core.workout.CrossJumps;
import com.health.openworkout.core.workout.HighKnees;
import com.health.openworkout.core.workout.JumpingJack;
import com.health.openworkout.core.workout.Lunge;
import com.health.openworkout.core.workout.PikeWalk;
import com.health.openworkout.core.workout.Plank;
import com.health.openworkout.core.workout.PushUpRotation;
import com.health.openworkout.core.workout.QuickSteps;
import com.health.openworkout.core.workout.SidePlank;
import com.health.openworkout.core.workout.Squat;
import com.health.openworkout.core.workout.WallSit;

public class AbdominalMuscleSession extends WorkoutSession {

    private float stressFac;

    public AbdominalMuscleSession(int dayNr, float stressFac) {
        this.stressFac = stressFac;

        switch (dayNr) {
            case 0:
                addWorkoutTime(new JumpingJack(), 15);
                addWorkoutRep(new CircleCrunch(), 5);
                addWorkoutRep(new PikeWalk(), 2);
                addWorkoutTime(new SidePlank(), 5);
                addWorkoutRep(new AbdominalCrunch(), 5);
                addWorkoutTime(new Plank(), 5);
                break;
            case 1:
                addWorkoutTime(new JumpingJack(), 15);
                addWorkoutRep(new AbdominalCrunch(), 10);
                addWorkoutRep(new SidePlank(), 3);
                addWorkoutTime(new HighKnees(), 15);
                addWorkoutRep(new CircleCrunch(), 10);
                addWorkoutTime(new Plank(), 10);
                break;
            case 2:
                addWorkoutTime(new JumpingJack(), 18);
                addWorkoutRep(new Burpee(), 4);
                addWorkoutTime(new SidePlank(), 10);
                addWorkoutRep(new PushUpRotation(), 10);
                addWorkoutRep(new AbdominalCrunch(), 10);
                addWorkoutTime(new Plank(), 15);
                break;
            case 3:
                addWorkoutTime(new JumpingJack(), 22);
                addWorkoutRep(new BicycleCrunch(), 10);
                addWorkoutRep(new Lunge(), 12);
                addWorkoutRep(new PikeWalk(), 4);
                addWorkoutTime(new Plank(), 15);
                break;
            case 4:
                addWorkoutTime(new JumpingJack(), 25);
                addWorkoutTime(new CrossJumps(), 20);
                addWorkoutTime(new SidePlank(), 20);
                addWorkoutRep(new AbdominalCrunch(), 12);
                addWorkoutRep(new Squat(), 12);
                addWorkoutTime(new Plank(), 18);
                break;
            case 5:
                addWorkoutTime(new HighKnees(), 30);
                addWorkoutRep(new AbdominalCrunch(), 15);
                addWorkoutRep(new PikeWalk(), 15);
                addWorkoutTime(new QuickSteps(), 15);
                addWorkoutRep(new CircleCrunch(), 15);
                addWorkoutTime(new Plank(), 20);
                break;
            case 6:
                addWorkoutTime(new JumpingJack(), 30);
                addWorkoutRep(new Burpee(), 5);
                addWorkoutTime(new WallSit(), 20);
                addWorkoutRep(new AbdominalCrunch(), 16);
                addWorkoutTime(new Plank(), 22);
                break;
            case 7:
                addWorkoutTime(new JumpingJack(), 35);
                addWorkoutRep(new AbdominalCrunch(), 16);
                addWorkoutTime(new HighKnees(), 15);
                addWorkoutRep(new CircleCrunch(), 15);
                addWorkoutTime(new SidePlank(), 20);
                addWorkoutTime(new Plank(), 25);
                break;
        }
    }

    private void addWorkoutTime(WorkoutItem workoutItem, int time) {
        workoutItem.setWorkoutTime(Math.round(time * stressFac));
        addWorkout(workoutItem);
    }

    private void addWorkoutRep(WorkoutItem workoutItem, int rep) {
        workoutItem.setTimeMode(false);
        workoutItem.setRepetitionCount(Math.round(rep * stressFac));
        addWorkout(workoutItem);
    }
}
