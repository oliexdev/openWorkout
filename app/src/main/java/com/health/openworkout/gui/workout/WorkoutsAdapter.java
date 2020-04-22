/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.ViewHolder> {
    private WorkoutFragment.WORKOUT_MODE mode;
    private final List<WorkoutItem> workoutItemList;
    private Context context;
    private static OnWorkoutClickListener onWorkoutClickListener;
    private static OnWorkoutClickListener onWorkoutEditClickListener;
    private static OnWorkoutClickListener onWorkoutDeleteClickListener;
    private static OnWorkoutClickListener onWorkoutReorderClickListener;

    public WorkoutsAdapter(Context aContext, List<WorkoutItem> workoutItemList, WorkoutFragment.WORKOUT_MODE mode) {
        this.mode = mode;
        this.context = aContext;
        this.workoutItemList = workoutItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        WorkoutItem workoutItem = workoutItemList.get(position);
        holder.nameView.setText(workoutItem.getName());

        if (workoutItem.isFinished()) {
            holder.doneView.setVisibility(View.VISIBLE);
        } else {
            holder.doneView.setVisibility(View.INVISIBLE);
        }

        if (workoutItem.isTimeMode()) {
            holder.detailedView.setText(String.format(context.getString(R.string.label_work_duration_item_info), workoutItem.getWorkoutTime()));
        } else {
            holder.detailedView.setText(String.format(context.getString(R.string.label_repetition_item_info), workoutItem.getRepetitionCount()));
        }

        holder.prepView.setText(String.format(context.getString(R.string.label_prep_duration_item_info), workoutItem.getPrepTime()));
        holder.breakView.setText(String.format(context.getString(R.string.label_break_duration_item_info), workoutItem.getBreakTime()));

        try {

            String subFolder;
            if (OpenWorkout.getInstance().getCurrentUser().isMale()) {
                subFolder = "male";
            } else {
                subFolder = "female";
            }

            InputStream ims = context.getAssets().open("image/" + subFolder + "/" + workoutItem.getImagePath());
            holder.imgView.setImageDrawable(Drawable.createFromStream(ims, null));

            ims.close();
        }
        catch(IOException ex) {
            Timber.e(ex);
        }

        switch (mode) {
            case VIEW:
                holder.doneView.setVisibility(View.VISIBLE);
                holder.reorderView.setVisibility(View.GONE);
                holder.deleteView.setVisibility(View.GONE);
                holder.editView.setVisibility(View.GONE);
                break;
            case EDIT:
                holder.doneView.setVisibility(View.GONE);
                holder.reorderView.setVisibility(View.VISIBLE);
                holder.deleteView.setVisibility(View.VISIBLE);
                holder.editView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return workoutItemList.get(position).getWorkoutItemId();
    }

    @Override
    public int getItemCount() {
        return workoutItemList.size();
    }

    public void setOnItemClickListener(OnWorkoutClickListener onWorkoutClickListener) {
        this.onWorkoutClickListener = onWorkoutClickListener;
    }

    public void setOnItemEditClickListener(OnWorkoutClickListener onWorkoutClickListener) {
        this.onWorkoutEditClickListener = onWorkoutClickListener;
    }

    public void setOnItemDeleteClickListener(OnWorkoutClickListener onWorkoutClickListener) {
        this.onWorkoutDeleteClickListener = onWorkoutClickListener;
    }

    public void setOnItemReorderClickListener(OnWorkoutClickListener onWorkoutClickListener) {
        this.onWorkoutReorderClickListener = onWorkoutClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView prepView;
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        ImageView doneView;
        ImageView reorderView;
        ImageView deleteView;
        ImageView editView;
        TextView breakView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prepView = itemView.findViewById(R.id.prepView);
            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            doneView = itemView.findViewById(R.id.doneView);
            reorderView = itemView.findViewById(R.id.reorderView);
            deleteView = itemView.findViewById(R.id.deleteView);
            editView = itemView.findViewById(R.id.editView);
            breakView = itemView.findViewById(R.id.breakView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onWorkoutClickListener != null) {
                        onWorkoutClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onWorkoutDeleteClickListener != null) {
                        onWorkoutDeleteClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            editView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onWorkoutEditClickListener != null) {
                        onWorkoutEditClickListener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });

            reorderView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        if (onWorkoutReorderClickListener != null) {
                            onWorkoutReorderClickListener.onItemClick(getAdapterPosition(), v);
                        }
                    }
                    return false;
                }
            });
        }
    }

    public interface OnWorkoutClickListener {
        public void onItemClick(int position, View v);
    }
}
