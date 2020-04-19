/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class Squat extends WorkoutItem {
    public Squat() {
        super();
        setName(getContext().getString(R.string.workout_name_squat));
        setDescription(getContext().getString(R.string.workout_description_squat));
        setImagePath("squad.png");
        setVideoPath("squad.mp4");
    }
}
