/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class TricepsDip extends WorkoutItem {
    public TricepsDip() {
        super();
        setName(getContext().getString(R.string.workout_name_triceps_dip));
        setDescription(getContext().getString(R.string.workout_description_triceps_dip));
        setImagePath("tricep_dips.png");
        setVideoPath("tricep_dips.mp4");
    }
}
