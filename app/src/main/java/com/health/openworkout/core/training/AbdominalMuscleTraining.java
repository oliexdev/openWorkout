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
import com.health.openworkout.core.session.AbdominalMuscleSession;

public class AbdominalMuscleTraining extends TrainingPlan {
    public AbdominalMuscleTraining() {
        super();

        setName(getContext().getString(R.string.training_abdominal_muscle_training));
        setImagePath("abdominalMuscleTraining.png");

        float stressFac = 1.0f;

        for (int i=0; i<=21; i++) {
            // on every week increase the stress factor
            if (i % 8 == 7) {
                stressFac += 0.2f;
            }

            AbdominalMuscleSession session = new AbdominalMuscleSession(i % 8, stressFac);

            session.setName(String.format(getContext().getString(R.string.day_unit), i+1));
            addWorkoutSession(session);
        }
    }
}
