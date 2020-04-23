/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;

import java.util.Collections;
import java.util.List;

public abstract class GenericFragment extends Fragment {
    @Keep
    public enum FRAGMENT_MODE {VIEW, EDIT}

    private FRAGMENT_MODE mode = FRAGMENT_MODE.VIEW;

    private ItemTouchHelper touchHelper;

    private MenuItem saveMenu;
    private MenuItem editMenu;

    public GenericFragment() {
        setHasOptionsMenu(true);

        touchHelper = new ItemTouchHelper(new ItemTouchHelper
                .SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT, ItemTouchHelper.ACTION_STATE_IDLE) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();

                Collections.swap(getItemList(), from, to);
                getAdapter().notifyItemMoved(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        });
    }

    public FRAGMENT_MODE getMode() {
        return mode;
    }

    protected abstract String getTitle();
    protected abstract void loadFromDatabase();
    protected abstract void saveToDatabase();
    protected abstract GenericAdapter getAdapter();
    protected abstract RecyclerView getRecyclerView();
    protected abstract List getItemList();
    protected abstract void onSelectClick(int position);
    protected abstract void onEditClick(int position);
    protected abstract void onDeleteClick(int position);
    protected abstract void onAddClick();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);

        saveMenu = menu.findItem(R.id.save);
        editMenu = menu.findItem(R.id.edit);

        refreshMenuVisibility();

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void refreshMenuVisibility() {
        switch (mode) {
            case VIEW:
                saveMenu.setVisible(false);
                editMenu.setVisible(true);
                break;
            case EDIT:
                saveMenu.setVisible(true);
                editMenu.setVisible(false);
                break;
        }

        if (mode == GenericFragment.FRAGMENT_MODE.VIEW) {
            touchHelper.attachToRecyclerView(null);

            getAdapter().setOnItemClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        onSelectClick(position);
                    }
                }
            });
        }

        if (mode == GenericFragment.FRAGMENT_MODE.EDIT) {
            getAdapter().setOnItemClickListener(null);

            touchHelper.attachToRecyclerView(getRecyclerView());

            getAdapter().setOnItemEditClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        onEditClick(position);
                    }
                }
            });

            getAdapter().setOnItemDeleteClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        getAdapter().notifyItemRemoved(position);

                        onDeleteClick(position);
                    }
                }
            });

            getAdapter().setOnItemReorderClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        touchHelper.startDrag(getRecyclerView().findViewHolderForLayoutPosition(position));
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                onAddClick();
                return true;
            case R.id.edit:
                mode = GenericFragment.FRAGMENT_MODE.EDIT;
                getAdapter().setMode(mode);
                refreshMenuVisibility();
                loadFromDatabase();
                return true;
            case R.id.save:
                mode = GenericFragment.FRAGMENT_MODE.VIEW;
                getAdapter().setMode(mode);
                refreshMenuVisibility();
                saveToDatabase();
                loadFromDatabase();
                Toast.makeText(getContext(), String.format(getString(R.string.label_save_toast), getTitle()), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.reset:
                Toast.makeText(getContext(), String.format(getString(R.string.label_reset_toast), getTitle()), Toast.LENGTH_SHORT).show();
                loadFromDatabase();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
