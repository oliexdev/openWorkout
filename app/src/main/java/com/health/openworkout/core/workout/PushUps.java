/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class PushUps extends WorkoutItem {
    public PushUps() {
        super();
        setName(getContext().getString(R.string.workout_name_push_ups));
        setDescription(getContext().getString(R.string.workout_description_push_ups));
        setImagePath("push_ups.png");
        setVideoPath("push_ups.mp4");
    }
}
