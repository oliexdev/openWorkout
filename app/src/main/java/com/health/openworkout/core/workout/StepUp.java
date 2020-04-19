/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class StepUp extends WorkoutItem {
    public StepUp() {
        super();
        setName(getContext().getString(R.string.workout_name_step_up));
        setDescription(getContext().getString(R.string.workout_description_step_up));
        setImagePath("step_up.png");
        setVideoPath("step_up.mp4");
    }
}
