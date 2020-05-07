/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class JumpPushUps extends WorkoutItem {
    public JumpPushUps() {
        super();
        setName(getContext().getString(R.string.workout_name_jump_push_ups));
        setDescription(getContext().getString(R.string.workout_description_jump_push_ups));
        setImagePath("jump_push_ups.png");
        setVideoPath("jump_push_ups.mp4");
        setTimeMode(false);
    }
}
