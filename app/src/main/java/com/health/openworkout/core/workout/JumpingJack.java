/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class JumpingJack extends WorkoutItem {
    public JumpingJack() {
        super();
        setName(getContext().getString(R.string.workout_name_jumping_jack));
        setDescription(getContext().getString(R.string.workout_description_jumping_jack));
        setImagePath("jumping_jack.png");
        setVideoPath("jumping_jack.mp4");
    }
}
