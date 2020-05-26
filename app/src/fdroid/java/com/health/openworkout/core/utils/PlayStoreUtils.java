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

package com.health.openworkout.core.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

public class PlayStoreUtils {
    private static PlayStoreUtils instance;

    private PlayStoreUtils(Context aContext) {
        // empty
    }

    public static void createInstance(Context aContext) {
        if (instance != null) {
            return;
        }

        instance = new PlayStoreUtils(aContext);
    }

    public static PlayStoreUtils getInstance() {
        if (instance == null) {
            throw new RuntimeException("No PlayStoreUtils instance created");
        }

        return instance;
    }

    public void initMobileAds(Activity activity) {
        // empty
    }

    public View getAdView(Context aContext) {
        return null;
    }


    public boolean isAdRemovalPaid() {
        return true;
    }

}
