/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class LungeKick extends WorkoutItem {
    public LungeKick() {
        super();
        setName(getContext().getString(R.string.workout_name_lunge_kick));
        setDescription(getContext().getString(R.string.workout_description_lunge_kick));
        setImagePath("lunge_kick.png");
        setVideoPath("lunge_kick.mp4");
        setTimeMode(false);
    }
}
