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
