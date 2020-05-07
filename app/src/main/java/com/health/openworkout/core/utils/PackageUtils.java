/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.health.openworkout.core.datatypes.TrainingPlan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

public class PackageUtils {
    private Context context;

    public PackageUtils(Context context) {
        this.context = context;
    }

    public void exportTrainingPlan(TrainingPlan trainingPlan) {
        Timber.d("EXPORT TRAINING PLAN " + trainingPlan.getName());

        try {
            File trainingDir = context.getDir(trainingPlan.getName(), Context.MODE_PRIVATE);

            if (trainingPlan.isImagePathExternal()) {
                Uri fileUri = Uri.parse(trainingPlan.getImagePath());
                String displayName = getDisplayName(fileUri);
                File trainingImg = new File(trainingDir, displayName);

                InputStream in = context.getContentResolver().openInputStream(fileUri);
                FileOutputStream out = new FileOutputStream(trainingImg);

                copyFile(in, out);

                Timber.d("Copied file " + displayName + " to internal storage");
            }
        }catch (IOException ex) {
            Timber.e(ex);
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }

        in.close();
        out.flush();
        out.close();
    }

    private String getDisplayName(Uri uri) {
        String fileName = new String();
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};

        Cursor metaCursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    fileName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }

        return fileName;
    }
}
