/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    protected abstract void onSelectCallback(int position);
    protected abstract void onEditCallback(int position);
    protected abstract void onDuplicateCallback(int position);
    protected abstract void onDeleteCallback(int position);
    protected abstract void onAddClick();
    protected abstract void onResetClick();
    protected void onExportClick(int position) {};

    private ProgressBar getProgressBar() {
        if (getView() != null) {
            return getView().findViewById(R.id.progressBar);
        }

        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
                        onSelectCallback(position);
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
                        onEditCallback(position);
                    }
                }
            });

            getAdapter().setOnItemDuplicateClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(final int position, View v) {
                    if (position != -1) {
                        getProgressBar().setVisibility(View.VISIBLE);
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void ... params) {
                                onDuplicateCallback(position);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void o) {
                                if (getProgressBar() != null) {
                                    getProgressBar().setVisibility(View.GONE);
                                    getAdapter().notifyItemInserted(position+1);
                                }
                            }
                        }.execute();
                    }
                }
            });

            getAdapter().setOnItemDeleteClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        getAdapter().notifyItemRemoved(position);

                        onDeleteCallback(position);
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

            getAdapter().setOnItemExportClickListener(new GenericAdapter.OnGenericClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    if (position != -1) {
                        onExportClick(position);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                getProgressBar().setVisibility(View.VISIBLE);
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void ... params) {
                                        onResetClick();
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void o) {
                                        if (getProgressBar() != null) {
                                            getProgressBar().setVisibility(View.GONE);
                                        }
                                        loadFromDatabase();

                                        Toast.makeText(getActivity(), String.format(getString(R.string.label_reset_toast), getTitle()), Toast.LENGTH_SHORT).show();
                                    }
                                }.execute();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                builder.setMessage(getString(R.string.label_really_reset_dialog)).setPositiveButton(getString(R.string.label_ok), dialogClickListener)
                        .setNegativeButton(getString(R.string.label_cancel), dialogClickListener).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
