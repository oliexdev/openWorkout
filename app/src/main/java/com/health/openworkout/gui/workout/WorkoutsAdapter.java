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

import com.health.openworkout.R;
import com.health.openworkout.core.OpenWorkout;
import com.health.openworkout.core.datatypes.WorkoutItem;
import com.health.openworkout.gui.datatypes.GenericAdapter;
import com.health.openworkout.gui.datatypes.GenericFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import timber.log.Timber;

public class WorkoutsAdapter extends GenericAdapter<WorkoutsAdapter.ViewHolder> {
    private GenericFragment.FRAGMENT_MODE mode;
    private final List<WorkoutItem> workoutItemList;
    private Context context;

    public WorkoutsAdapter(Context aContext, List<WorkoutItem> workoutItemList, GenericFragment.FRAGMENT_MODE mode) {
        super(aContext, mode);
        this.mode = mode;
        this.context = aContext;
        this.workoutItemList = workoutItemList;
    }

    @Override
    public WorkoutsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
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
                break;
            case EDIT:
                holder.doneView.setVisibility(View.GONE);
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

    static class ViewHolder extends GenericAdapter.ViewHolder {
        TextView prepView;
        ImageView imgView;
        TextView nameView;
        TextView detailedView;
        ImageView doneView;
        TextView breakView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prepView = itemView.findViewById(R.id.prepView);
            imgView = itemView.findViewById(R.id.imgView);
            nameView = itemView.findViewById(R.id.nameView);
            detailedView = itemView.findViewById(R.id.detailedView);
            doneView = itemView.findViewById(R.id.doneView);
            breakView = itemView.findViewById(R.id.breakView);
        }
    }
}
