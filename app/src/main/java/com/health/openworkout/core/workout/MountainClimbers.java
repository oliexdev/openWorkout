/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class MountainClimbers extends WorkoutItem {
    public MountainClimbers() {
        super();
        setName(getContext().getString(R.string.workout_name_mountain_climbers));
        setDescription(getContext().getString(R.string.workout_description_mountain_climbers));
        setImagePath("mountain_climbers.png");
        setVideoPath("mountain_climbers.mp4");
    }
}
