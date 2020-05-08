/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.health.openworkout.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public abstract class GenericSettingsFragment extends Fragment {
    @Keep
    public enum SETTING_MODE {EDIT, ADD}

    private SETTING_MODE mode = SETTING_MODE.EDIT;

    public GenericSettingsFragment() {
        setHasOptionsMenu(true);
    }

    protected abstract String getTitle();
    protected abstract void loadFromDatabase(SETTING_MODE mode);
    protected abstract boolean saveToDatabase(SETTING_MODE mode);

    protected void setMode(SETTING_MODE mode) {
        this.mode = mode;
        loadFromDatabase(mode);
    }

    protected SETTING_MODE getMode() {
        return mode;
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
}
