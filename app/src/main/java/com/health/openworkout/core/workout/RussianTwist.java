/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class RussianTwist extends WorkoutItem {
    public RussianTwist() {
        super();
        setName(getContext().getString(R.string.workout_name_russian_twist));
        setDescription(getContext().getString(R.string.workout_description_russian_twist));
        setImagePath("russian_twist.png");
        setVideoPath("russian_twist.mp4");
        setTimeMode(false);
    }
}
