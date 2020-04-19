/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.session;

import com.health.openworkout.core.datatypes.WorkoutSession;
import com.health.openworkout.core.workout.AbdominalCrunch;
import com.health.openworkout.core.workout.HighKnees;
import com.health.openworkout.core.workout.JumpingJack;
import com.health.openworkout.core.workout.Lunge;
import com.health.openworkout.core.workout.Plank;
import com.health.openworkout.core.workout.PushUpRotation;
import com.health.openworkout.core.workout.PushUps;
import com.health.openworkout.core.workout.SidePlank;
import com.health.openworkout.core.workout.Squat;
import com.health.openworkout.core.workout.StepUp;
import com.health.openworkout.core.workout.TricepsDip;
import com.health.openworkout.core.workout.WallSit;

public class SevenMinutesSession extends WorkoutSession {

    public SevenMinutesSession() {
        addWorkout(new JumpingJack());
        addWorkout(new WallSit());
        addWorkout(new PushUps());
        addWorkout(new AbdominalCrunch());
        addWorkout(new StepUp());
        addWorkout(new Squat());
        addWorkout(new TricepsDip());
        addWorkout(new Plank());
        addWorkout(new HighKnees());
        addWorkout(new Lunge());
        addWorkout(new PushUpRotation());
        addWorkout(new SidePlank());
    }
}
