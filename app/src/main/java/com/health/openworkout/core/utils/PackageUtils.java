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

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.gson.Gson;
import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.GitHubFile;
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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import timber.log.Timber;

public class PackageUtils {
    private Context context;
    private Gson gson;
    private File trainingDir;
    private File trainingImageDir;
    private File trainingVideoDir;
    private Retrofit retrofit;
    private GitHubApi gitHubApi;
    private OnGitHubCallbackListener onGitHubCallbackListener;

    public PackageUtils(Context context) {
        this.context = context;
        gson = new Gson();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubApi = retrofit.create(GitHubApi.class);
    }

    public TrainingPlan importTrainingPlan(File zipFile) {
        String displayName = getDisplayName(zipFile);

        return importTrainingPlan(Uri.fromFile(zipFile), displayName);
    }

    public TrainingPlan importTrainingPlan(Uri zipFileUri) {
        String displayName = getDisplayName(zipFileUri);

        return importTrainingPlan(zipFileUri, displayName);
    }

    private TrainingPlan importTrainingPlan(Uri zipFileUri, String filename) {
        Timber.d("Import training plan " + filename);

        try {
            unzipFile(zipFileUri, filename);

            File trainingDatabase = new File(context.getFilesDir(), filename + "/database.json");

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

            Toast.makeText(context, String.format(context.getString(R.string.label_info_imported), gsonTrainingPlan.getName(), filename), Toast.LENGTH_LONG).show();

            return gsonTrainingPlan;
        } catch (IOException ex) {
            Toast.makeText(context, String.format(context.getString(R.string.error_no_valid_training_package), filename + ".zip"), Toast.LENGTH_LONG).show();
            Timber.e(ex);
        } finally {
            File zipFile = new File(context.getFilesDir(), filename + ".zip");
            if (zipFile.exists()) {
                Timber.d("Delete unzipped local zip file " + zipFile);
                zipFile.delete();
            }
        }

        return null;
    }

    public void exportTrainingPlan(TrainingPlan trainingPlan, Uri zipFileUri) {
        Timber.d("Export training plan " + trainingPlan.getName());

        try {
            String zipFileDisplayName = getDisplayName(zipFileUri);

            trainingDir = new File(context.getFilesDir(), trainingPlan.getName());
            trainingImageDir = new File(context.getFilesDir(), trainingPlan.getName()+"/image");
            trainingVideoDir = new File(context.getFilesDir(), trainingPlan.getName()+ "/video");

            if (trainingDir.exists()) {
                deleteDirectory(trainingDir);
            }

            trainingDir.mkdir();
            trainingImageDir.mkdir();
            trainingVideoDir.mkdir();

            trainingPlan.setTrainingPlanId(0);

            if (trainingPlan.isImagePathExternal()) {
                trainingPlan.setImagePath(copyImageToInternalStorage(trainingPlan.getImagePath()));
            }

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

            String jsonString = gson.toJson(trainingPlan);
            File trainingDatabase = new File(trainingDir, "database.json");
            FileOutputStream jsonOut = new FileOutputStream(trainingDatabase);
            jsonOut.write(jsonString.getBytes());
            jsonOut.close();
            Timber.d("Written database.json");

            zipDirectory(trainingDir, zipFileUri);
            Timber.d("Zipped " + trainingPlan.getName());
            deleteDirectory(trainingDir);
            Toast.makeText(context, String.format(context.getString(R.string.label_info_exported), trainingPlan.getName(), zipFileDisplayName), Toast.LENGTH_LONG).show();
        }catch (IOException ex) {
            Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();

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

    private String getDisplayName(File file) {
        String displayName = file.getName();

        if (displayName.endsWith(".zip")) {
            displayName = displayName.substring(0, displayName.length() - 4);
        }

        return displayName;
    }

    private String getDisplayName(Uri uri) {
        String displayName = new String();
        String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};

        Cursor metaCursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (metaCursor != null) {
            try {
                if (metaCursor.moveToFirst()) {
                    displayName = metaCursor.getString(0);
                }
            } finally {
                metaCursor.close();
            }
        }

        if (displayName.endsWith(".zip")) {
            displayName = displayName.substring(0, displayName.length() - 4);
        }

        return displayName;
    }

    private void zipDirectory(File directoryToCompress, Uri outputFile) throws IOException {
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

    private void unzipFile(Uri zipFileUri, String filename) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(zipFileUri);
        ZipInputStream zipIn = new ZipInputStream(in);

        File rootDir = new File(context.getFilesDir(),  filename);
        rootDir.mkdir();

        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            File zipOut = new File(context.getFilesDir(),  filename + "/" + entry.getName());

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

    public void getGitHubFiles() {
        Call<List<GitHubFile>> gitHubFileList = gitHubApi.getFileList();

        gitHubFileList.enqueue(new Callback<List<GitHubFile>>() {
            @Override
            public void onResponse(Call<List<GitHubFile>> call, Response<List<GitHubFile>> response) {
                if (response.isSuccessful()) {
                    Timber.d("Successful file list from GitHub received");

                    if (onGitHubCallbackListener != null) {
                        onGitHubCallbackListener.onGitHubFileList(response.body());
                    }
                } else {
                    Timber.e("Get GitHub file list error");
                }
            }

            @Override
            public void onFailure(Call<List<GitHubFile>> call, Throwable t) {
                onGitHubCallbackListener.onGitHubFailure(new Exception(context.getString(R.string.error_no_github_connection)));
                Timber.e("GitHub call failed " + t.getMessage());
            }
        });
    }

    public void downloadFile(GitHubFile gitHubFile) {
        Call<ResponseBody> downloadFile = gitHubApi.downloadFile(gitHubFile.getDownloadURL());

        downloadFile.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            boolean writtenToDisk = writeResponseBodyToDisk(gitHubFile.getName(), gitHubFile.getSize(), response.body());

                            if (writtenToDisk) {
                                if (onGitHubCallbackListener != null) {
                                    Timber.d("Successful " + gitHubFile.getName() + " file downloaded from " + gitHubFile.getDownloadURL());
                                    onGitHubCallbackListener.onGitHubDownloadFile(new File(context.getFilesDir(), gitHubFile.getName()));
                                }
                            }
                            return null;
                        }
                    }.execute();
                } else {
                    Timber.e("Download failed for URL " + gitHubFile.getDownloadURL());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onGitHubCallbackListener.onGitHubFailure(new Exception(context.getString(R.string.error_no_github_download) + "(" + t.getMessage() + ")"));
                Timber.e("Download failed " + t.getMessage());
            }
        });
    }

    private boolean writeResponseBodyToDisk(final String filename, final long fileSize, final ResponseBody body) {
        try {
            File zipFile = new File(context.getFilesDir(), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(zipFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    if (onGitHubCallbackListener != null) {
                        onGitHubCallbackListener.onGitHubDownloadProgressUpdate(fileSizeDownloaded, fileSize);
                    }
                }

                outputStream.flush();

                if (onGitHubCallbackListener != null) {
                    onGitHubCallbackListener.onGitHubDownloadProgressUpdate(fileSizeDownloaded, fileSize);
                }

                return true;
            } catch (IOException e) {
                Timber.e("Error writing to disk " + e);
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Timber.e("Error writing to disk " + e);
            return false;
        }
    }

    public void setOnGitHubCallbackListener(OnGitHubCallbackListener onGitHubCallbackListener) {
        this.onGitHubCallbackListener = onGitHubCallbackListener;
    }

    public interface OnGitHubCallbackListener {
        void onGitHubFileList(List<GitHubFile> gitHubFileList);
        void onGitHubDownloadFile(File uriFilename);
        void onGitHubDownloadProgressUpdate(long bytesDownloaded, long bytesTotal);
        void onGitHubFailure(Exception ex);
    }

    private interface GitHubApi {
        @GET("repos/oliexdev/openWorkout/contents/pkg")
        Call<List<GitHubFile>> getFileList();
        @Streaming
        @GET
        Call<ResponseBody> downloadFile(@Url String fileUrl);
    }
}
