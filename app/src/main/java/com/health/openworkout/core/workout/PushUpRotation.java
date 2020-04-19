/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class PushUpRotation extends WorkoutItem {
    public PushUpRotation() {
        super();
        setName(getContext().getString(R.string.workout_name_push_up_rotation));
        setDescription(getContext().getString(R.string.workout_description_push_up_rotation));
        setImagePath("push_up_rotation.png");
        setVideoPath("push_up_rotation.mp4");
    }
}
