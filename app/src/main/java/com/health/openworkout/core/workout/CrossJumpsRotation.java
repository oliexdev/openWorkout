/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class CrossJumpsRotation extends WorkoutItem {
    public CrossJumpsRotation() {
        super();
        setName(getContext().getString(R.string.workout_name_cross_jump_rotation));
        setDescription(getContext().getString(R.string.workout_description_cross_jump_rotation));
        setImagePath("cross_jump_rotation.png");
        setVideoPath("cross_jump_rotation.mp4");
    }
}
