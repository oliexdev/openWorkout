/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class Lunge extends WorkoutItem {
    public Lunge() {
        super();
        setName(getContext().getString(R.string.workout_name_lunge));
        setDescription(getContext().getString(R.string.workout_description_lunge));
        setImagePath("lunge.png");
        setVideoPath("lunge.mp4");
    }
}
