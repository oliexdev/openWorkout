/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class SidePlank extends WorkoutItem {
    public SidePlank() {
        super();
        setName(getContext().getString(R.string.workout_name_side_plank));
        setDescription(getContext().getString(R.string.workout_description_side_plank));
        setImagePath("side_plank.png");
        setVideoPath("side_plank.mp4");
    }
}
