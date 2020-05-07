/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class CrossJumps extends WorkoutItem {
    public CrossJumps() {
        super();
        setName(getContext().getString(R.string.workout_name_cross_jump));
        setDescription(getContext().getString(R.string.workout_description_cross_jump));
        setImagePath("cross_jump.png");
        setVideoPath("cross_jump.mp4");
    }
}
