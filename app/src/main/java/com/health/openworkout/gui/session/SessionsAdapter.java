/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.datatypes.WorkoutSession;

import java.util.List;

public class SessionsAdapter extends RecyclerView.Adapter<SessionsAdapter.ViewHolder> {
    private SessionFragment.SESSION_MODE mode;
    private List<WorkoutSession> workoutSessionList;
    private Context context;
    private static OnSessionClickListener onDefaultClickListener;
    private static OnSessionClickListener onEditClickListener;
    private static OnSessionClickListener onDeleteClickListener;
    private static OnSessionClickListener onReorderClickListener;

    public SessionsAdapter(Context aContext, List<WorkoutSession> workoutSessionList, SessionFragment.SESSION_MODE mode) {
        this.mode = mode;
        this.context = aContext;
        this.workoutSessionList = workoutSessionList;
    }

    @Override
    public SessionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_session, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SessionsAdapter.ViewHolder holder, int position) {
        WorkoutSession workoutSession = workoutSessionList.get(position);
        holder.nameView.setText(workoutSession.getName());

        if (workoutSession.isFinished()) {
            holder.imgView.setImageResource(R.drawable.ic_session_done);
        } else {
            holder.imgView.setImageResource(R.drawable.ic_session_undone);
        }

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

    @Override
    public long getItemId(int position) {
        return workoutSessionList.get(position).getWorkoutSessionId();
    }

    @Override
    public int getItemCount() {
        return workoutSessionList.size();
    }

    public void setOnItemClickListener(OnSessionClickListener onWorkoutClickListener) {
        this.onDefaultClickListener = onWorkoutClickListener;
    }

    public void setOnItemEditClickListener(OnSessionClickListener onEditClickListener) {
        this.onEditClickListener = onEditClickListener;
    }

    public void setOnItemDeleteClickListener(OnSessionClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemReorderClickListener(OnSessionClickListener onReorderClickListener) {
        this.onReorderClickListener = onReorderClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView nameView;
        ImageView reorderView;
        ImageView deleteView;
        ImageView editView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
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

    public interface OnSessionClickListener {
        public void onItemClick(int position, View v);
    }
}
