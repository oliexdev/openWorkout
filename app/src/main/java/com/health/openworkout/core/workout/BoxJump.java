/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class BoxJump extends WorkoutItem {
    public BoxJump() {
        super();
        setName(getContext().getString(R.string.workout_name_box_jump));
        setDescription(getContext().getString(R.string.workout_description_box_jump));
        setImagePath("box_jump.png");
        setVideoPath("box_jump.mp4");
        setTimeMode(false);
    }
}
