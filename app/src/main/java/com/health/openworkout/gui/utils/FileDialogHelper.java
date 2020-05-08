/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.utils;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.fragment.app.Fragment;

import com.health.openworkout.R;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

public class FileDialogHelper {

    private final int READ_EXTERNAL_STORAGE_PERMISSION_IMPORT = 1;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_EXPORT = 2;
    private final int READ_EXTERNAL_STORAGE_PERMISSION_OPEN_IMAGE = 3;
    private final int READ_EXTERNAL_STORAGE_PERMISSION_OPEN_VIDEO = 4;
    private final int REQUEST_OPEN_IMAGE_DIALOG = 10;
    private final int REQUEST_OPEN_VIDEO_DIALOG = 20;
    private final int REQUEST_EXPORT_FILE_DIALOG = 30;
    private final int REQUEST_IMPORT_FILE_DIALOG = 40;

    private Fragment fragment;

    public FileDialogHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    public void openImportFileDialog() {
        if (checkPermissionForReadExternalStorage()) {
            Intent intent = new Intent()
                    .setType("application/zip")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);

            fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.label_select_image_file)), REQUEST_IMPORT_FILE_DIALOG);
        } else {
            requestPermissionForReadExternalStorage(READ_EXTERNAL_STORAGE_PERMISSION_IMPORT);
        }
    }

    public void openExportFileDialog() {
        if (checkPermissionForWriteExternalStorage()) {
            Intent intent = new Intent()
                    .setType("application/zip")
                    .setAction(Intent.ACTION_CREATE_DOCUMENT);

            fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.label_select_image_file)), REQUEST_EXPORT_FILE_DIALOG);
        } else {
            requestPermissionForWriteExternalStorage(WRITE_EXTERNAL_STORAGE_PERMISSION_EXPORT);
        }
    }

    public void openImageFileDialog() {
        if (checkPermissionForReadExternalStorage()) {
            Intent intent = new Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);

            fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.label_select_image_file)), REQUEST_OPEN_IMAGE_DIALOG);
        } else {
            requestPermissionForReadExternalStorage(READ_EXTERNAL_STORAGE_PERMISSION_OPEN_IMAGE);
        }
    }

    public void openVideoFileDialog() {
        if (checkPermissionForReadExternalStorage()) {
            Intent intent = new Intent()
                    .setType("video/*")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);

            fragment.startActivityForResult(Intent.createChooser(intent, fragment.getString(R.string.label_select_video_file)), REQUEST_OPEN_VIDEO_DIALOG);
        } else {
            requestPermissionForReadExternalStorage(READ_EXTERNAL_STORAGE_PERMISSION_OPEN_VIDEO);
        }
    }

    private boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = fragment.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }


    private void requestPermissionForReadExternalStorage(int requestCode) {
        try {
            fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestCode);
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }


    private boolean checkPermissionForWriteExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = fragment.getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    private void requestPermissionForWriteExternalStorage(int requestCode) {
        try {
            fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case READ_EXTERNAL_STORAGE_PERMISSION_IMPORT:
                    openImportFileDialog();
                    break;
                case WRITE_EXTERNAL_STORAGE_PERMISSION_EXPORT:
                    openExportFileDialog();
                    break;
                case READ_EXTERNAL_STORAGE_PERMISSION_OPEN_IMAGE:
                    openImageFileDialog();
                    break;
                case READ_EXTERNAL_STORAGE_PERMISSION_OPEN_VIDEO:
                    openVideoFileDialog();
                    break;
            }
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMPORT_FILE_DIALOG ||
                    requestCode == REQUEST_EXPORT_FILE_DIALOG ||
                    requestCode == REQUEST_OPEN_IMAGE_DIALOG ||
                    requestCode == REQUEST_OPEN_VIDEO_DIALOG) {
                return true;
            }
        }

        return false;
    }
}
