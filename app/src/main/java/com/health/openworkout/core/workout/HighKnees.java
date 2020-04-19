/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class HighKnees extends WorkoutItem {
    public HighKnees() {
        super();
        setName(getContext().getString(R.string.workout_name_high_knees));
        setDescription(getContext().getString(R.string.workout_description_high_knees));
        setImagePath("high_knees.png");
        setVideoPath("high_knees.mp4");
    }
}
