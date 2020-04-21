/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.training;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.session.BeginnersSession;

public class BeginnersTraining extends TrainingPlan {
    public BeginnersTraining() {
        super();

        setName(getContext().getString(R.string.training_beginners_workout));
        setImagePath("beginnersTraining.png");

        float stressFac = 1.0f;

        for (int i=0; i<=27; i++) {
            // on every week increase the stress factor
            if (i % 8 == 7) {
                stressFac += 0.2f;
            }

            BeginnersSession session = new BeginnersSession(i % 8, stressFac);

            session.setName(String.format(getContext().getString(R.string.day_unit), i+1));
            addWorkoutSession(session);
        }
    }
}
