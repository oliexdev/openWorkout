/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
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
