/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.datatypes;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;

public abstract class GenericAdapter<VH extends GenericAdapter.ViewHolder> extends RecyclerView.Adapter<VH> {
    @Keep
    public enum FRAGMENT_MODE {VIEW, EDIT}
    private FRAGMENT_MODE mode;
    private Context context;
    private static OnGenericClickListener onDefaultClickListener;
    private static OnGenericClickListener onEditClickListener;
    private static OnGenericClickListener onDeleteClickListener;
    private static OnGenericClickListener onReorderClickListener;

    public GenericAdapter(Context aContext, FRAGMENT_MODE mode) {
        this.mode = mode;
        this.context = aContext;
    }

    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    abstract public long getItemId(int position);
    abstract public int getItemCount();

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        switch (mode) {
            case VIEW:
                holder.reorderView.setVisibility(View.GONE);
                holder.deleteView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.GONE);
                break;
            case EDIT:
                holder.reorderView.setVisibility(View.VISIBLE);
                holder.deleteView.setVisibility(View.VISIBLE);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView reorderView;
        ImageView deleteView;
        ImageView editView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            reorderView = itemView.findViewById(R.id.reorderView);
            deleteView = itemView.findViewById(R.id.deleteView);
            editView = itemView.findViewById(R.id.editView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDefaultClickListener != null) {
                        onDefaultClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener != null) {
                        onDeleteClickListener.onItemClick(getAdapterPosition(), v);
                    }
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
    }

    public interface OnGenericClickListener {
        public void onItemClick(int position, View v);
    }
}
