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

package com.health.openworkout.core.session;

import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.workout.AbdominalCrunch;
import com.health.openworkout.core.workout.HighKnees;
import com.health.openworkout.core.workout.JumpingJack;
import com.health.openworkout.core.workout.Lunge;
import com.health.openworkout.core.workout.Plank;
import com.health.openworkout.core.workout.PushUpRotation;
import com.health.openworkout.core.workout.PushUps;
import com.health.openworkout.core.workout.Squat;
import com.health.openworkout.core.workout.StepUp;
import com.health.openworkout.core.workout.TricepsDip;

public class BeginnersSession extends WorkoutSession {

    private float stressFac;

    public BeginnersSession(int dayNr, float stressFac) {
        this.stressFac = stressFac;

        switch (dayNr) {
            case 0:
                addWorkoutTime(new JumpingJack(), 15);
                addWorkoutRep(new StepUp(), 5);
                addWorkoutRep(new PushUps(), 2);
                addWorkoutRep(new AbdominalCrunch(), 5);
                addWorkoutTime(new Plank(), 5);
                break;
            case 1:
                addWorkoutTime(new JumpingJack(), 15);
                addWorkoutRep(new StepUp(), 10);
                addWorkoutRep(new PushUps(), 3);
                addWorkoutRep(new AbdominalCrunch(), 10);
                addWorkoutTime(new Plank(), 10);
                break;
            case 2:
                addWorkoutTime(new JumpingJack(), 18);
                addWorkoutRep(new TricepsDip(), 4);
                addWorkoutRep(new Squat(), 10);
                addWorkoutRep(new AbdominalCrunch(), 10);
                addWorkoutTime(new Plank(), 15);
                break;
            case 3:
                addWorkoutTime(new JumpingJack(), 22);
                addWorkoutTime(new HighKnees(), 10);
                addWorkoutRep(new Lunge(), 12);
                addWorkoutRep(new PushUpRotation(), 4);
                addWorkoutTime(new Plank(), 15);
                break;
            case 4:
                addWorkoutTime(new JumpingJack(), 25);
                addWorkoutRep(new PushUps(), 4);
                addWorkoutRep(new AbdominalCrunch(), 12);
                addWorkoutRep(new Squat(), 12);
                addWorkoutTime(new Plank(), 18);
                break;
            case 5:
                addWorkoutRep(new PushUps(), 4);
                addWorkoutRep(new AbdominalCrunch(), 15);
                addWorkoutRep(new Squat(), 15);
                addWorkoutTime(new HighKnees(), 15);
                addWorkoutTime(new Plank(), 20);
                break;
            case 6:
                addWorkoutTime(new JumpingJack(), 30);
                addWorkoutRep(new TricepsDip(), 5);
                addWorkoutRep(new Lunge(), 16);
                addWorkoutRep(new AbdominalCrunch(), 16);
                addWorkoutTime(new Plank(), 22);
                break;
            case 7:
                addWorkoutTime(new JumpingJack(), 35);
                addWorkoutRep(new StepUp(), 16);
                addWorkoutTime(new HighKnees(), 15);
                addWorkoutRep(new PushUpRotation(), 6);
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
