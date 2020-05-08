/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.core.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.TrainingPlan;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.core.datatypes.WorkoutSession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

public class PackageUtils {
    private Context context;
    private Gson gson;
    private File trainingDir;
    private File trainingImageDir;
    private File trainingVideoDir;

    public PackageUtils(Context context) {
        this.context = context;
        gson = new Gson();
    }

    public void importTrainingPlan(Uri zipFileUri) {
        Timber.d("Import training plan");

        try {
            String displayName = getDisplayName(zipFileUri);
            unzipFile(zipFileUri);

            File trainingDatabase = new File(context.getFilesDir(), displayName + "/database.json");

            StringBuilder result;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(trainingDatabase)));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            reader.close();

            TrainingPlan gsonTrainingPlan = gson.fromJson(result.toString(), TrainingPlan.class);
            Timber.d("Read training database " + gsonTrainingPlan.getName());
            OpenWorkout.getInstance().insertTrainingPlan(gsonTrainingPlan);

            File rootDir = new File(context.getFilesDir(), displayName);
            File renamedRootDir = new File(context.getFilesDir(), gsonTrainingPlan.getName());

            if (renamedRootDir.exists()) {
                deleteDirectory(renamedRootDir);
            }

            rootDir.renameTo(renamedRootDir);

        } catch (IOException ex) {
            Timber.e(ex);
        }
    }

    public void exportTrainingPlan(TrainingPlan trainingPlan, Uri zipFileUri) {
        Timber.d("Export training plan " + trainingPlan.getName());

        try {
            trainingDir = new File(context.getFilesDir(), trainingPlan.getName());
            trainingImageDir = new File(context.getFilesDir(), trainingPlan.getName()+"/image");
            trainingVideoDir = new File(context.getFilesDir(), trainingPlan.getName()+ "/video");

            if (trainingDir.exists()) {
                deleteDirectory(trainingDir);
            }

            trainingDir.mkdir();
            trainingImageDir.mkdir();
            trainingVideoDir.mkdir();

            File outputDir = context.getFilesDir();
            //File zipFile = new File(outputDir, trainingPlan.getName()+ ".zip");

            if (trainingPlan.isImagePathExternal()) {
                trainingPlan.setTrainingPlanId(0);
                trainingPlan.setImagePath(copyImageToInternalStorage(trainingPlan.getImagePath()));

                for (WorkoutSession workoutSession : trainingPlan.getWorkoutSessions()) {
                    workoutSession.setWorkoutSessionId(0);

                    for (WorkoutItem workoutItem : workoutSession.getWorkoutItems()) {
                        workoutItem.setWorkoutItemId(0);
                       if (workoutItem.isImagePathExternal()) {
                           workoutItem.setImagePath(copyImageToInternalStorage(workoutItem.getImagePath()));
                       }

                       if (workoutItem.isVideoPathExternal()) {
                           workoutItem.setVideoPath(copyVideoToInternalStorage(workoutItem.getVideoPath()));
                       }
                    }
                }
            }

            String jsonString = gson.toJson(trainingPlan);
            File trainingDatabase = new File(trainingDir, "database.json");
            FileOutputStream jsonOut = new FileOutputStream(trainingDatabase);
            jsonOut.write(jsonString.getBytes());
            jsonOut.close();
            Timber.d("Written database.json");

            zipDirectory(trainingDir, zipFileUri);
            Timber.d("Zipped " + trainingPlan.getName());
            //deleteDirectory(trainingDir);
        }catch (IOException ex) {
            Timber.e(ex);
        }
    }

    private String copyImageToInternalStorage(String imagePath) throws IOException {
        Uri fileUri = Uri.parse(imagePath);
        String displayName = getDisplayName(fileUri);
        File trainingImg = new File(trainingImageDir, displayName);

        if (!trainingImg.exists()) {
            InputStream in = context.getContentResolver().openInputStream(fileUri);
            FileOutputStream out = new FileOutputStream(trainingImg);

            copyFile(in, out);

            Timber.d("Copied file " + displayName + " to internal storage");
        }

        return Uri.fromFile(trainingImg).toString();
    }

    private String copyVideoToInternalStorage(String videoPath) throws IOException {
        Uri fileUri = Uri.parse(videoPath);
        String displayName = getDisplayName(fileUri);
        File trainingVideo = new File(trainingVideoDir, displayName);

        if (!trainingVideo.exists()) {
            InputStream in = context.getContentResolver().openInputStream(fileUri);
            FileOutputStream out = new FileOutputStream(trainingVideo);

            copyFile(in, out);

            Timber.d("Copied file " + displayName + " to internal storage");
        }

        return Uri.fromFile(trainingVideo).toString();
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

    public void zipDirectory(File directoryToCompress, Uri outputFile) throws IOException {
        OutputStream dest = context.getContentResolver().openOutputStream(outputFile);
        ZipOutputStream zipOutputStream = new ZipOutputStream(dest);

        compressDirectory(directoryToCompress, directoryToCompress, zipOutputStream);
        zipOutputStream.close();
    }

    private void compressDirectory(File rootDirectory, File currentDirectory, ZipOutputStream out) throws IOException {
        byte[] data = new byte[2048];

        File[] files = currentDirectory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    compressDirectory(rootDirectory, file, out);
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

    public void unzipFile(Uri zipFileUri) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(zipFileUri);
        String displayName = getDisplayName(zipFileUri);
        ZipInputStream zipIn = new ZipInputStream(in);

        File rootDir = new File(context.getFilesDir(),  displayName);
        rootDir.mkdir();

        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            File zipOut = new File(context.getFilesDir(),  displayName + entry.getName());

            if (!entry.isDirectory()) {
                zipOut.getParentFile().mkdir();
                // if the entry is a file, extracts it
                extractFile(zipIn, zipOut);
                Timber.d("Extract file " + entry.getName());
            } else {
                zipOut.mkdir();
                Timber.d("Extract folder " + entry.getName());
            }
            zipIn.closeEntry();

            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, File fileOutput) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileOutput));
        byte[] bytesIn = new byte[2048];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.flush();
        bos.close();
    }

    private void deleteDirectory(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteDirectory(child);

        fileOrDirectory.delete();
    }
}
