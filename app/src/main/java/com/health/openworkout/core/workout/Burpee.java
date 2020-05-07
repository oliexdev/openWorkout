/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class Burpee extends WorkoutItem {
    public Burpee() {
        super();
        setName(getContext().getString(R.string.workout_name_burpee));
        setDescription(getContext().getString(R.string.workout_description_burpee));
        setImagePath("burpee.png");
        setVideoPath("burpee.mp4");
        setTimeMode(false);
    }
}
