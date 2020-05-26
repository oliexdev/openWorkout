/*
 * Copyright (C) 2020 olie.xdev <olie.xdev@googlemail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
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
