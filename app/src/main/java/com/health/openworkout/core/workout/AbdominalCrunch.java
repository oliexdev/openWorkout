/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class AbdominalCrunch extends WorkoutItem {
    public AbdominalCrunch() {
        super();
        setName(getContext().getString(R.string.workout_name_abdominal_crunch));
        setDescription(getContext().getString(R.string.workout_description_abdominal_crunch));
        setImagePath("abdonminal_crunch.png");
        setVideoPath("abdonminal_crunch.mp4");
    }
}
