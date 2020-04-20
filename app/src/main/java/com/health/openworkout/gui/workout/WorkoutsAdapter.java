/*
 * Copyright (C) 2020 by olie.xdev@googlemail.com All Rights Reserved
 */

package com.health.openworkout.gui.workout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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
    private List<WorkoutItem> workoutItemList;
    private Context context;
    private static OnWorkoutClickListener onWorkoutClickListener;

    public WorkoutsAdapter(Context aContext, List<WorkoutItem> workoutItemList) {
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
            holder.detailedView.setText(String.format(context.getString(R.string.label_duration_item_info), workoutItem.getWorkoutTime()));
        } else {
            holder.detailedView.setText(String.format(context.getString(R.string.label_repetition_item_info), workoutItem.getRepetitionCount()));
        }

        // if workout item the last in list don't show the break view text
        if (workoutItemList.get(workoutItemList.size() - 1).getWorkoutItemId() == workoutItem.getWorkoutItemId()) {
            holder.breakView.setVisibility(View.GONE);
        } else {
            holder.breakView.setVisibility(View.VISIBLE);
            holder.breakView.setText(String.format(context.getString(R.string.label_break_item_info), workoutItem.getBreakTime()));
        }

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        ImageView doneView;
        TextView breakView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            doneView = itemView.findViewById(R.id.doneView);
            breakView = itemView.findViewById(R.id.breakView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWorkoutClickListener.onItemClick(getAdapterPosition(), v);
                }
            });
        }
    }

    public interface OnWorkoutClickListener {
        public void onItemClick(int position, View v);
    }
}
