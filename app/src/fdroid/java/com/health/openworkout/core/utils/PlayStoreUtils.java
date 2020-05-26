/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
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
