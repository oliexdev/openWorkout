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

package com.health.openworkout.core.training;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.session.SevenMinutesSession;

public class SevenMinutesTraining extends TrainingPlan {
    public SevenMinutesTraining() {
        super();

        setName(getContext().getString(R.string.training_seven_minutes_workout_training));
        setImagePath("sevenMinutesTraining.png");

        for (int i=1; i<=30; i++) {
            SevenMinutesSession session = new SevenMinutesSession();

            session.setName(String.format(getContext().getString(R.string.day_unit), i));
            addWorkoutSession(session);
        }
    }
}
