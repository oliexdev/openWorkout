/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class AirSquatBentArms extends WorkoutItem {
    public AirSquatBentArms() {
        super();
        setName(getContext().getString(R.string.workout_name_air_squad_bent_arms));
        setDescription(getContext().getString(R.string.workout_description_air_squad_bent_arms));
        setImagePath("air_squad_bent_arms.png");
        setVideoPath("air_squad_bent_arms.mp4");
        setTimeMode(false);
    }
}
