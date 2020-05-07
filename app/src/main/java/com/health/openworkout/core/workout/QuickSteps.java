/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class QuickSteps extends WorkoutItem {
    public QuickSteps() {
        super();
        setName(getContext().getString(R.string.workout_name_quick_steps));
        setDescription(getContext().getString(R.string.workout_description_quick_steps));
        setImagePath("quick_steps.png");
        setVideoPath("quick_steps.mp4");
    }
}
