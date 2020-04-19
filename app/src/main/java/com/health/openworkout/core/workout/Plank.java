/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class Plank extends WorkoutItem {
    public Plank() {
        super();
        setName(getContext().getString(R.string.workout_name_plank));
        setDescription(getContext().getString(R.string.workout_description_plank));
        setImagePath("plank.png");
        setVideoPath("plank.mp4");
    }
}
