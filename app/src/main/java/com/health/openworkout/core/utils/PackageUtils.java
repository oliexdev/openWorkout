/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.health.openworkout.core.datatypes.TrainingPlan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class PackageUtils {
    private Context context;

    public PackageUtils(Context context) {
        this.context = context;
    }

    public void exportTrainingPlan(TrainingPlan trainingPlan) {
        Timber.d("EXPORT TRAINING PLAN " + trainingPlan.getName());

        try {
            File trainingDir = new File(context.getFilesDir(), trainingPlan.getName());
            File trainingImageDir = new File(context.getFilesDir(), trainingPlan.getName()+"/image");
            File trainingVideoDir = new File(context.getFilesDir(), trainingPlan.getName()+ "/video");

            trainingDir.mkdir();
            trainingImageDir.mkdir();
            trainingVideoDir.mkdir();

            File outputDir = context.getFilesDir();
            File zipFile = new File(outputDir, "myzip.zip");

            if (trainingPlan.isImagePathExternal()) {
                Uri fileUri = Uri.parse(trainingPlan.getImagePath());
                String displayName = getDisplayName(fileUri);
                File trainingImg = new File(trainingImageDir, displayName);

                InputStream in = context.getContentResolver().openInputStream(fileUri);
                FileOutputStream out = new FileOutputStream(trainingImg);

                copyFile(in, out);

                Timber.d("Copied file " + displayName + " to internal storage");

                zipDirectory(trainingDir, zipFile);
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

    public void zipDirectory(File directoryToCompress, File outputFile)  {
        try {
            FileOutputStream dest = new FileOutputStream(outputFile);
            ZipOutputStream zipOutputStream = new ZipOutputStream(dest);

            zipDirectoryHelper(directoryToCompress, directoryToCompress, zipOutputStream);
            zipOutputStream.close();
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private void zipDirectoryHelper(File rootDirectory, File currentDirectory, ZipOutputStream out) throws Exception {
        byte[] data = new byte[2048];

        File[] files = currentDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectoryHelper(rootDirectory, file, out);
                } else {
                    FileInputStream fi = new FileInputStream(file);
                    // creating structure and avoiding duplicate file names
                    String name = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), "");

                    ZipEntry entry = new ZipEntry(name);
                    out.putNextEntry(entry);
                    int count;
                    BufferedInputStream origin = new BufferedInputStream(fi, 2048);
                    while ((count = origin.read(data, 0, 2048)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
            }
        }
    }
}
