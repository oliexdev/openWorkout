/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class WallSit extends WorkoutItem {
    public WallSit() {
        super();
        setName(getContext().getString(R.string.workout_name_wall_sit));
        setDescription(getContext().getString(R.string.workout_description_wall_sit));
        setImagePath("wall_sit.png");
        setVideoPath("wall_sit.mp4");
    }
}
