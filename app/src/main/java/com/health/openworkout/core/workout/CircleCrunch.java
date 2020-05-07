/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class CircleCrunch extends WorkoutItem {
    public CircleCrunch() {
        super();
        setName(getContext().getString(R.string.workout_name_circle_crunch));
        setDescription(getContext().getString(R.string.workout_description_circle_crunch));
        setImagePath("circle_crunch.png");
        setVideoPath("circle_crunch.mp4");
        setTimeMode(false);
    }
}
