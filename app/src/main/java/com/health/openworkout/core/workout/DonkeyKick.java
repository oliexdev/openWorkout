/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.workout;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutItem;

public class DonkeyKick extends WorkoutItem {
    public DonkeyKick() {
        super();
        setName(getContext().getString(R.string.workout_name_donkey_kick));
        setDescription(getContext().getString(R.string.workout_description_donkey_kick));
        setImagePath("donkey_kick.png");
        setVideoPath("donkey_kick.mp4");
        setTimeMode(false);
    }
}
