/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.content.Context;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;

public abstract class GenericAdapter<VH extends GenericAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
    private GenericFragment.FRAGMENT_MODE mode;
    private Context context;
    private static OnGenericClickListener onDefaultClickListener;
    private static OnGenericClickListener onEditClickListener;
    private static OnGenericClickListener onDeleteClickListener;
    private static OnGenericClickListener onReorderClickListener;
    private static OnGenericClickListener onDuplicateClickListener;
    private static OnGenericClickListener onExportClickListener;

    public GenericAdapter(Context aContext) {
        this.mode = GenericFragment.FRAGMENT_MODE.VIEW;
        this.context = aContext;
    }

    public void setMode(GenericFragment.FRAGMENT_MODE mode) {
        this.mode = mode;
    }

    public GenericFragment.FRAGMENT_MODE getMode() {
        return mode;
    }

    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    abstract public long getItemId(int position);
    abstract public int getItemCount();

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        switch (mode) {
            case VIEW:
                holder.reorderView.setVisibility(View.GONE);
                holder.optionView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.GONE);
                break;
            case EDIT:
                holder.reorderView.setVisibility(View.VISIBLE);
                holder.optionView.setVisibility(View.VISIBLE);
                holder.editView.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setOnItemClickListener(OnGenericClickListener onWorkoutClickListener) {
        this.onDefaultClickListener = onWorkoutClickListener;
    }

    public void setOnItemEditClickListener(OnGenericClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnItemDeleteClickListener(OnGenericClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemReorderClickListener(OnGenericClickListener onReorderClickListener) {
        this.onReorderClickListener = onReorderClickListener;
    }

    public void setOnItemDuplicateClickListener(OnGenericClickListener onDuplicateClickListener) {
        this.onDuplicateClickListener = onDuplicateClickListener;
    }

    public void setOnItemExportClickListener(OnGenericClickListener onExportClickListener) {
        this.onExportClickListener = onExportClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
        ImageView reorderView;
        ImageView optionView;
        ImageView editView;
        PopupMenu popupMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            reorderView = itemView.findViewById(R.id.reorderView);
            optionView = itemView.findViewById(R.id.optionView);
            editView = itemView.findViewById(R.id.editView);

            popupMenu = new PopupMenu(itemView.getContext(), optionView);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.getMenuInflater().inflate(R.menu.item_menu, popupMenu.getMenu());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDefaultClickListener != null) {
                        onDefaultClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            optionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });

            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onEditClickListener != null) {
                        onEditClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            reorderView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        if (onReorderClickListener != null) {
                            onReorderClickListener.onItemClick(getAdapterPosition(), v);
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onItemClick(getAdapterPosition(), null);
                    }
                    return true;
                case R.id.duplicate:
                    if (onDuplicateClickListener != null) {
                        onDuplicateClickListener.onItemClick(getAdapterPosition(), null);
                    }
                    return true;
                case R.id.export:
                    if (onExportClickListener != null) {
                        onExportClickListener.onItemClick(getAdapterPosition(), null);
                    }
                    return true;
            }


            return false;
        }
    }

    public interface OnGenericClickListener {
        public void onItemClick(int position, View v);
    }
}
