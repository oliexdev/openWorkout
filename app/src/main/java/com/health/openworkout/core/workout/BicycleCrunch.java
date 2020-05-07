/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class BicycleCrunch extends WorkoutItem {
    public BicycleCrunch() {
        super();
        setName(getContext().getString(R.string.workout_name_bicycle_crunch));
        setDescription(getContext().getString(R.string.workout_description_bicycle_crunch));
        setImagePath("bicycle_crunch.png");
        setVideoPath("bicycle_crunch.mp4");
        setTimeMode(false);
    }
}
