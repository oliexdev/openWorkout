/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class GenericSettingsFragment extends Fragment {
    @Keep
    public enum SETTING_MODE {EDIT, ADD}

    private SETTING_MODE mode = SETTING_MODE.EDIT;

    private final int REQUEST_OPEN_IMAGE_DIALOG = 1;
    private final int REQUEST_OPEN_VIDEO_DIALOG = 2;
    private final int READ_STORAGE_PERMISSION_REQUEST_CODE = 3;

    private String imgPath;
    private String videoPath;

    public GenericSettingsFragment() {
        setHasOptionsMenu(true);

        imgPath = new String();
        videoPath = new String();
    }

    protected abstract String getTitle();
    protected abstract void loadFromDatabase(SETTING_MODE mode);
    protected abstract boolean saveToDatabase(SETTING_MODE mode);
    protected void onNewImagePath(Uri uri){};
    protected void onNewVideoPath(Uri uri){};

    protected void setMode(SETTING_MODE mode) {
        this.mode = mode;
        loadFromDatabase(mode);
    }

    protected SETTING_MODE getMode() {
        return mode;
    }
    protected String getImagePath() {
        return imgPath;
    }
    protected String getVideoPath() {
        return videoPath;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        MenuItem editMenu = menu.findItem(R.id.edit);
        editMenu.setVisible(false);

        MenuItem addMenu = menu.findItem(R.id.add);
        addMenu.setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // close keyboard
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }

        switch (item.getItemId()) {
            case R.id.save:
                if (saveToDatabase(mode)) {
                    Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), getTitle()), Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigateUp();
                }
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), getTitle()), Toast.LENGTH_SHORT).show();
                loadFromDatabase(mode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void openImageFileDialog() {
        if (checkPermissionForReadExternalStorage()) {
            Intent intent = new Intent()
                    .setType("image/*")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);

            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_image_file)), REQUEST_OPEN_IMAGE_DIALOG);
        } else {
            requestPermissionForReadExternalStorage();
        }
    }

    protected void openVideoFileDialog() {
        if (checkPermissionForReadExternalStorage()) {
            Intent intent = new Intent()
                    .setType("video/*")
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);

            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video_file)), REQUEST_OPEN_VIDEO_DIALOG);
        } else {
            requestPermissionForReadExternalStorage();
        }
    }

    protected boolean checkPermissionForReadExternalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    protected void requestPermissionForReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if (requestCode == REQUEST_OPEN_IMAGE_DIALOG) {
                onNewImagePath(uri);
                imgPath = uri.toString();
            }

            if (requestCode == REQUEST_OPEN_VIDEO_DIALOG) {
                onNewVideoPath(uri);
                videoPath = uri.toString();
            }
        }
    }
}
